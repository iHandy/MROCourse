package com.ucoz.handyby.mrocourse.processors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.util.ArrayMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Handy on 29.11.2015.
 */
public class Recognizer {

    private ArrayList<PartImageMember> mPretendents;
    private int[] mImagePixels;
    private int mImageWidth;
    private int mImageHeight;
    private String mImagePath;

    private ArrayList<RecognizeMember> mRecognizeMembers = new ArrayList<>();
    private ArrayList<PartImageMember> mResultPartImageMembers = new ArrayList<>();

    public Recognizer(ArrayList<PartImageMember> mPretendents, String mImagePath) {
        this.mPretendents = mPretendents;
        this.mImagePath = mImagePath;

        init();

        recognize();
    }

    private void init() {
        Bitmap bitmap = BitmapFactory.decodeFile(mImagePath);
        mImageWidth = bitmap.getWidth();
        mImageHeight = bitmap.getHeight();
        int size = mImageWidth * mImageHeight;
        mImagePixels = new int[size];
        bitmap.getPixels(mImagePixels, 0, mImageWidth, 0, 0, mImageWidth, mImageHeight);
        bitmap.recycle();
    }

    private void recognize() {
        generateRecognizeMembers();

        mResultPartImageMembers.clear();
        //ArrayMap<Double, PartImageMember> mapR = new ArrayMap<>();
        //ArrayList<RecognizeMember> mapR = new ArrayList<>();
        ArrayList<Double> keys = new ArrayList<>();

        RecognizeMember firstMember = mRecognizeMembers.get(0);
        mRecognizeMembers.remove(firstMember);
        for (RecognizeMember recognizeMember : mRecognizeMembers) {
            if (recognizeMember.getPretendent() != firstMember.getPretendent()) {
                double keyR1 = firstMember.equalsR1(recognizeMember);
                double keyR2 = firstMember.equalsR2(recognizeMember);
                recognizeMember.R = keyR1 + keyR2;
                //mapR.add(recognizeMember);
                keys.add(keyR1 + keyR2);
            }
        }

        Collections.sort(mRecognizeMembers, new Comparator<RecognizeMember>() {
            @Override
            public int compare(RecognizeMember lhs, RecognizeMember rhs) {
                return (int) Math.round(lhs.R - rhs.R);
            }
        });

        //mResultPartImageMembers.add(mRecognizeMembers.get(0).getPretendent())

        double firstKey = 0;
        double secondKey = 0;
        double thirdKey = 0;
        for (RecognizeMember member : mRecognizeMembers) {
            double key = member.R;
            if (firstKey == 0) {
                firstKey = key;
                mResultPartImageMembers.add(member.getPretendent());
            } else if (key == firstKey) {
                mResultPartImageMembers.add(member.getPretendent());
            } else if (secondKey == 0) {
                secondKey = key;
                mResultPartImageMembers.add(member.getPretendent());
            } else if (secondKey == key) {
                mResultPartImageMembers.add(member.getPretendent());
            } else if (thirdKey == 0) {
                thirdKey = key;
                mResultPartImageMembers.add(member.getPretendent());
            } else if (thirdKey == key) {
                mResultPartImageMembers.add(member.getPretendent());
            }
        }
    }


    private void generateRecognizeMembers() {
        mRecognizeMembers.clear();
        for (PartImageMember pretendent : mPretendents) {
            RecognizeMember recognizeMember = new RecognizeMember(pretendent);

            int pretendentWidth = pretendent.endX - pretendent.startX;
            int pretendentHeight = pretendent.endY - pretendent.startY;
            int[][] workPixels = new int[pretendentWidth][pretendentHeight];

            //Get pretendent matrix from big image
            for (int pY = pretendent.startY, py1 = 0; pY < pretendent.endY; pY++, py1++)
                for (int pX = pretendent.startX, px1 = 0; pX < pretendent.endX; pX++, px1++) {
                    workPixels[px1][py1] = mImagePixels[pX + pY * mImageWidth];
                }

            int half = pretendentHeight / 2;
            //top-bottom cycle
            for (int ly = 0; ly < pretendentHeight; ly++) {
                //left-right cycle
                for (int lx = 0; lx < pretendentWidth; lx++) {
                    int currentColor = workPixels[lx][ly];

                    //only for BLACK pixels
                    if (currentColor != Color.WHITE) {
                        //fill 3x3 matrix
                        int[][] pixelNeibours = Utils.fill3x3Matrix(pretendentWidth, pretendentHeight, workPixels, ly, lx);
                        int[] pixelsLine = Utils.getLineFromMatrixByCircle(pixelNeibours);

                        int blackCount = 0;
                        int iter = 0;
                        for (int pixel : pixelsLine) {
                            pixelsLine[iter] = pixel == Color.WHITE ? 0 : 1;
                            blackCount += pixelsLine[iter] == 1 ? 1 : 0;
                            iter++;
                        }

                        switch (blackCount) {
                            case 5:
                                if (ly < half) {
                                    recognizeMember.neighbor5Count++;
                                } else {
                                    recognizeMember.neighbor5Count2++;
                                }
                                break;
                            case 4:
                                if (ly < half) {
                                    recognizeMember.neighbor4Count++;
                                } else {
                                    recognizeMember.neighbor4Count2++;
                                }
                                break;
                            case 3:
                                if (ly < half) {
                                    recognizeMember.neighbor3Count++;
                                } else {
                                    recognizeMember.neighbor3Count2++;
                                }
                                break;
                            default:
                                break;
                        }

                        //ends count
                        int A4 = getA4(pixelsLine);
                        int A8 = getA8(pixelsLine);
                        int B8 = getB8(pixelsLine);
                        int C8 = getC8(pixelsLine);
                        int Nc4 = A4 - C8;
                        int CN = A8 - B8;

                        if (A8 == 1 && Nc4 == 1 && CN == 1) {
                            if (ly < half) {
                                recognizeMember.endsCount++;
                            } else {
                                recognizeMember.endsCount2++;
                            }
                        }
                    }
                }
            }
            mRecognizeMembers.add(recognizeMember);
        }
    }

    private int getA4(int[] pixelsLine) {
        int result = 0;
        for (int k = 1; k < 5; k++) {
            result += pixelsLine[2 * k - 2];
        }
        return result;
    }

    private int getA8(int[] pixelsLine) {
        int result = 0;
        for (int k = 1; k < 9; k++) {
            result += pixelsLine[k - 1];
        }
        return result;
    }

    private int getB8(int[] pixelsLine) {
        int result = 0;
        for (int k = 1; k < 9; k++) {
            result += pixelsLine[k - 1] * pixelsLine[k];
        }
        return result;
    }

    private int getC8(int[] pixelsLine) {
        int result = 0;
        for (int k = 1; k < 5; k++) {
            result += pixelsLine[2 * k - 2] * pixelsLine[2 * k - 1] * pixelsLine[2 * k];
        }
        return result;
    }

    public ArrayList<PartImageMember> getParts() {
        return mResultPartImageMembers;
    }

    public void drawResult(ArrayList<PartImageMember> resultMembers) {
        for (PartImageMember wordPart : resultMembers) {
            for (int x = wordPart.startX; x < wordPart.endX; x++) {
                for (int y = wordPart.startY; y < wordPart.endY; y++) {
                    int color = mImagePixels[x + y * mImageWidth];
                    if (color != Color.WHITE) {
                        mImagePixels[x + y * mImageWidth] = Color.RED;
                    }
                }
            }
        }

        Utils.saveBitmap(mImagePath, mImageWidth, mImageHeight, mImagePixels);
    }
}
