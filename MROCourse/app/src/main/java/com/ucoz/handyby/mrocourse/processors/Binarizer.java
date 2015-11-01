package com.ucoz.handyby.mrocourse.processors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Handy on 01.11.2015.
 */
public class Binarizer {

    public void binarizeByThreshold(String imagePath, int threshold) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        int[] pixels = new int[size];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.recycle();

        for (int i = 0; i < size; i++) {
            int color = pixels[i];
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);
            double luminance = (0.299 * r + 0.0f + 0.587 * g + 0.0f + 0.114 * b + 0.0f);
            pixels[i] = luminance > threshold ? Color.WHITE : Color.BLACK;
        }

        saveBitmap(imagePath, width, height, pixels);
    }

    public int binarizeBy120Method(String imagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;
        int[] pixels = new int[size];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.recycle();

        int maxGray = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            int color = pixels[i];
            int r = Color.red(color);
            int g = Color.green(color);
            int b = Color.blue(color);
            double luminance = (0.299 * r + 0.0f + 0.587 * g + 0.0f + 0.114 * b + 0.0f);
            pixels[i] = ((int) luminance);
            if (pixels[i] < 120 && pixels[i] > maxGray) {
                maxGray = pixels[i];
            }
        }

        for (int i = 0; i < size; i++) {
            int luminance = pixels[i];
            if (luminance < (maxGray + 12)) {
                pixels[i] = Color.BLACK;
            } else {
                pixels[i] = Color.WHITE;
            }
        }

        saveBitmap(imagePath, width, height, pixels);
        return maxGray;
    }

    private void saveBitmap(String imagePath, int width, int height, int[] pixels) {
        Bitmap bitmap;
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            bitmap.recycle();
        }
    }
}
