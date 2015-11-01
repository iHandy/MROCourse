package com.ucoz.handyby.mrocourse.processors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import java.util.ArrayList;

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

}
