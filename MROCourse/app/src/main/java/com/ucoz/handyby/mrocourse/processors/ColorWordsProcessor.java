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
                for (GystMember oneRowMember : oneRowGystogram) {
                    xIter++;

                    if (oneRowMember.count == 0 && !inRow) {
                        inRow = true;
                        xStart = xIter;

                    } else if ((oneRowMember.count > 0 || xIter == oneRowGystogram.size()-1) && inRow) {
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
                                wordParts.add(wordMember);
                            }

                            xEndPrev = xEnd;
                        }
                    }
                }
            }
        }

        int colorToSet = colorArray[0];
        int randomColor;
        Random randomer = new Random();
        for (PartImageMember wordPart : wordParts) {
            do {
                randomColor = colorArray[randomer.nextInt(4)];
            } while (randomColor == colorToSet);
            colorToSet = randomColor;
            for (int x = wordPart.startX; x < wordPart.endX; x++) {
                for (int y = wordPart.startY; y < wordPart.endY; y++) {
                    int color = pixels[x + y * width];
                    if (color == Color.BLACK) {
                        pixels[x + y * width] = colorToSet;
                    }
                }
            }
        }

        Utils.saveBitmap(mImagePath, width, height, pixels);
    }
}
