package com.ucoz.handyby.mrocourse.processors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Handy on 15.11.2015.
 */
public class ColorWordsProcessor {

    int[] colorArray = {0xFF00FF00, 0xFF0000FF, 0xFF00FFFF, 0xFFFF00FF};

    private String mImagePath;
    private ArrayList<GystMember> mRowsGystogram;
    private SpacesHolder mSpacesHolder;

    private ArrayList<PartImageMember> mPretendents = new ArrayList<>();

    public ColorWordsProcessor(String mImagePath, ArrayList<GystMember> mRowsGystogram, SpacesHolder mSpacesHolder) {
        this.mImagePath = mImagePath;
        this.mRowsGystogram = mRowsGystogram;
        this.mSpacesHolder = mSpacesHolder;
    }

    public void detectAndPaintWords() {
        Bitmap bitmap = BitmapFactory.decodeFile(mImagePath);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        int[] pixels = new int[size];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.recycle();

        ArrayList<PartImageMember> wordParts = new ArrayList<>();
        ArrayList<GystMember> oneRowGystogram = new ArrayList<>();

        int yStart = 0, yEnd = 0, yIter = -1;
        boolean inLine = false;
        for (GystMember gystMember : mRowsGystogram) {
            yIter++;

            if (gystMember.count > 0 && !inLine) {
                inLine = true;
                yStart = yIter;
            } else if (gystMember.count == 0 && inLine) {
                inLine = false;
                yEnd = yIter;

                oneRowGystogram.clear();

                for (int x = 0; x < width; x++) {
                    GystMember member = new GystMember(x);

                    for (int y = yStart; y < yEnd; y++) {
                        int color = pixels[x + y * width];
                        if (color == Color.BLACK) {
                            member.add();
                        }
                    }
                    oneRowGystogram.add(member);
                }

                int xStart = 0, xEnd = 0, xIter = -1, xEndPrev = -1;
                boolean inRow = false;
                int chars = 0;
                for (GystMember oneRowMember : oneRowGystogram) {
                    xIter++;

                    if (oneRowMember.count == 0 && !inRow) {
                        inRow = true;
                        xStart = xIter;

                    } else if ((oneRowMember.count > 0 || xIter == oneRowGystogram.size() - 1) && inRow) {
                        inRow = false;
                        xEnd = xIter;

                        int xValue = xEnd - xStart;
                        if (!mSpacesHolder.isInWordSpace(xValue)) {
                            if (xEndPrev != -1) {
                                PartImageMember wordMember = new PartImageMember();
                                wordMember.startX = xEndPrev;
                                wordMember.startY = yStart;
                                wordMember.endX = xStart;
                                wordMember.endY = yEnd;
                                wordMember.chars = chars + 1;
                                wordParts.add(wordMember);
                                chars = 0;
                            }

                            xEndPrev = xEnd;
                        } else {
                            chars++;
                        }

                    }
                }
            }
        }

        int pretendentSymbolsCount = wordParts.get(0).chars;
        int colorToSet = colorArray[0];
        int randomColor;
        int pretendentColor = 0xfff0bb2d;
        Random randomer = new Random();
        for (PartImageMember wordPart : wordParts) {
            if (wordPart.chars == pretendentSymbolsCount) {
                colorToSet = pretendentColor;
                mPretendents.add(wordPart);
            } else {
                do {
                    randomColor = colorArray[randomer.nextInt(4)];
                } while (randomColor == colorToSet);
                colorToSet = randomColor;
            }
            for (int x = wordPart.startX; x < wordPart.endX; x++) {
                for (int y = wordPart.startY; y < wordPart.endY; y++) {
                    int color = pixels[x + y * width];
                    if (color != Color.WHITE) {
                        pixels[x + y * width] = colorToSet;
                    }
                }
            }
        }

        Utils.saveBitmap(mImagePath, width, height, pixels);
    }

    public ArrayList<PartImageMember> getPretendents() {
        return mPretendents;
    }

    public void thickPixelIter() {
        Bitmap bitmap = BitmapFactory.decodeFile(mImagePath);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        int[] pixels = new int[size];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.recycle();


        for (PartImageMember pretendent : mPretendents) {
            int pretendentWidth = pretendent.endX - pretendent.startX;
            int pretendentHeight = pretendent.endY - pretendent.startY;
            int[][] workPixels = new int[pretendentWidth][pretendentHeight];

            //Get pretendent matrix from big image
            for (int pY = pretendent.startY, py1 = 0; pY < pretendent.endY; pY++, py1++) {
                for (int pX = pretendent.startX, px1 = 0; pX < pretendent.endX; pX++, px1++) {
                    workPixels[px1][py1] = pixels[pX + pY * width];
                }
            }

            boolean exitByPixelIter = false;
            //ByPixelIter cycle
            do {
                int cycleChanges = 0;
                //top-bottom cycle
                for (int ly = 0; ly < pretendentHeight; ly++) {
                    //left-right cycle
                    for (int lx = 0; lx < pretendentWidth; lx++) {
                        int currentColor = workPixels[lx][ly];

                        //only for color (black) pixels
                        if (currentColor != Color.WHITE) {
                            //fill 3x3 matrix
                            int[][] pixelNeibours = Utils.fill3x3Matrix(pretendentWidth, pretendentHeight, workPixels, ly, lx);
                            //1 case: min 1 white by 4x;
                            //2 case: >2 black by 8x;
                            //3 case: min 1 black not checked by 8x;
                            int countWhite4X = 0, countBlack = 0, countNCBlack = 0;
                            for (int pnY = 0; pnY < 3; pnY++) {
                                for (int pnX = 0; pnX < 3; pnX++) {
                                    if (pnY == 1 && pnX == 1) {
                                        continue; //skip center pixel;
                                    }
                                    if (pixelNeibours[pnX][pnY] == Color.WHITE) {
                                        countWhite4X += pixelIn4x(pnX, pnY) ? 1 : 0;
                                    } else {
                                        countBlack++;
                                        countNCBlack += pixelNeibours[pnX][pnY] != Color.BLACK ? 1 : 0;
                                    }
                                }
                            }

                            //4 case: not break point
                            int countChanges = 0;
                            int[] line = Utils.getLineFromMatrixByCircle(pixelNeibours);
                            int prevU = line[0];
                            for (int u : line) {
                                countChanges += prevU != u && prevU == Color.WHITE ? 1 : 0;
                                prevU = u;
                            }

                            //1,2,3,4 cases check
                            if (countWhite4X >= 1 && countBlack >= 2 && countNCBlack >= 1 && countChanges <= 1) {
                                //5 case:
                                countChanges = 0;
                                if (line[2] == Color.BLACK) {
                                    line[2] = Color.WHITE;
                                    prevU = line[0];
                                    for (int u : line) {
                                        countChanges += prevU != u && prevU == Color.WHITE ? 1 : 0;
                                        prevU = u;
                                    }
                                    line[2] = Color.BLACK;
                                }

                                if (countChanges <= 1) {
                                    //6 case:
                                    countChanges = 0;
                                    if (line[4] == Color.BLACK) {
                                        line[4] = Color.WHITE;
                                        prevU = line[0];
                                        for (int u : line) {
                                            countChanges += prevU != u && prevU == Color.WHITE ? 1 : 0;
                                            prevU = u;
                                        }
                                        line[4] = Color.BLACK;
                                    }

                                    if (countChanges <= 1) {
                                        //all cases complete. Set pixel on BLACK
                                        workPixels[lx][ly] = Color.BLACK;
                                        cycleChanges++;
                                    }
                                }
                            }
                        }
                    }
                }

                if (cycleChanges != 0) {
                    //remove checked BLACK pixels
                    for (int y = 0; y < pretendentHeight; y++)
                        for (int x = 0; x < pretendentWidth; x++) {
                            if (workPixels[x][y] == Color.BLACK) {
                                workPixels[x][y] = Color.WHITE;
                            }
                        }
                } else {
                    exitByPixelIter = true;
                    break;
                }
            } while (!exitByPixelIter);

            //update big image with new pretendent
            for (int pY = pretendent.startY, py1 = 0; pY < pretendent.endY; pY++, py1++)
                for (int pX = pretendent.startX, px1 = 0; pX < pretendent.endX; pX++, px1++) {
                    pixels[pX + pY * width] = workPixels[px1][py1] == Color.WHITE ? Color.WHITE : Color.BLACK;
                }
        }

        Utils.saveBitmap(mImagePath, width, height, pixels);
    }

    private boolean pixelIn4x(int x, int y) {
        return ((x == 1 && y == 0)
                || (x == 0 && y == 1)
                || (x == 2 && y == 1)
                || (x == 1 && y == 2));
    }

}
