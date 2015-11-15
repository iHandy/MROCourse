package com.ucoz.handyby.mrocourse.processors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by Handy on 01.11.2015.
 */
public class GystogramBuilder {

    public ArrayList<GystMember> getImageGystogram(String imagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        int[] pixels = new int[size];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.recycle();

        ArrayList<GystMember> gystogram = new ArrayList<>();
        GystMember gm;

        for (int i = 0; i < size; i++) {
            int color = pixels[i];
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);
            int luminance = (int) (0.299 * r + 0.0f + 0.587 * g + 0.0f + 0.114 * b + 0.0f);
            gm = new GystMember(luminance);
            boolean success = false;
            for (GystMember member : gystogram) {
                if (member.grayValue == luminance) {
                    member.add();
                    success = true;
                }
            }
            if (!success) {
                gm.add();
                gystogram.add(gm);
            }
        }
        return gystogram;
    }

    public ArrayList<GystMember> getRowsGystogram(String imagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        int[] pixels = new int[size];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.recycle();

        ArrayList<GystMember> gystogram = new ArrayList<>();

        for (int x = 0; x < height; x++) {
            gystogram.add(new GystMember(x));
            for (int y = 0; y < width; y++) {
                int color = pixels[y + x * width];
                if (color == Color.BLACK) {
                    gystogram.get(x).add();
                }
            }
        }
        return gystogram;
    }

    public ArrayList<GystMember> getColumnsGystogram(String imagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        int[] pixels = new int[size];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.recycle();

        ArrayList<GystMember> gystogram = new ArrayList<>();

        for (int x = 0; x < width; x++) {
            gystogram.add(new GystMember(x));
            for (int y = 0; y < height; y++) {
                int color = pixels[x + y * width];
                if (color == Color.BLACK) {
                    gystogram.get(x).add();
                }
            }
        }
        return gystogram;
    }


    public ArrayList<GystMember> getSpacesInRowsGystogram(String imagePath, ArrayList<GystMember> rowsGystogram) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        int[] pixels = new int[size];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.recycle();

        ArrayList<GystMember> oneRowGystogram = new ArrayList<>();
        ArrayList<Integer> spaces = new ArrayList<>();
        ArrayList<GystMember> spacesInRowsGystogram = new ArrayList<>();

        int yStart = 0, yEnd = 0, yIter = -1;
        boolean inLine = false;
        for (GystMember gystMember : rowsGystogram) {
            yIter++;

            if (gystMember.count > 0 && !inLine) {
                inLine = true;
                yStart = yIter;
            } else if (gystMember.count == 0 && inLine) {
                inLine = false;
                yEnd = yIter;

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

                int xStart = 0, xEnd = 0, xIter = -1;
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
                        spaces.add(xValue);
                    }
                }
            }
        }

        Collections.sort(spaces);
        int lastSpace = -1;
        GystMember gystMember = null;
        for (Integer space : spaces) {
            if (space > lastSpace) {
                if (gystMember != null) {
                    spacesInRowsGystogram.add(gystMember);
                }
                gystMember = new GystMember(space);
            }
            gystMember.add();
            lastSpace = space;
        }

        return spacesInRowsGystogram;
    }
}
