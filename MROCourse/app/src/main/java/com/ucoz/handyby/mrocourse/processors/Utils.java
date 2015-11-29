package com.ucoz.handyby.mrocourse.processors;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Handy on 15.11.2015.
 */
public class Utils {

    public static void saveBitmap(String imagePath, int width, int height, int[] pixels) {
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

    public static int[][] fill3x3Matrix(int width, int height, int[][] workPixels, int y, int x) {
        int[][] matrix3x3 = new int[3][3];
        matrix3x3[0][0] = (x - 1) < 0 || (y - 1) < 0 ? Color.WHITE : workPixels[(x - 1)][(y - 1)];
        matrix3x3[0][1] = (y - 1) < 0 ? Color.WHITE : workPixels[(x)][(y - 1)];
        matrix3x3[0][2] = (x + 1) >= width || (y - 1) < 0 ? Color.WHITE : workPixels[(x + 1)][(y - 1)];
        matrix3x3[1][0] = (x - 1) < 0 ? Color.WHITE : workPixels[(x - 1)][(y)];
        matrix3x3[1][1] = workPixels[(x)][(y)];
        matrix3x3[1][2] = (x + 1) >= width ? Color.WHITE : workPixels[(x + 1)][(y)];
        matrix3x3[2][0] = (x - 1) < 0 || (y + 1) >= height ? Color.WHITE : workPixels[(x - 1)][(y + 1)];
        matrix3x3[2][1] = (y + 1) >= height ? Color.WHITE : workPixels[(x)][(y + 1)];
        matrix3x3[2][2] = (x + 1) >= width || (y + 1) >= height ? Color.WHITE : workPixels[(x + 1)][(y + 1)];
        return matrix3x3;
    }

    public static int[] getLineFromMatrixByCircle(int[][] pixelNeibours) {
        return new int[]{pixelNeibours[2][1], pixelNeibours[2][0], pixelNeibours[1][0],
                pixelNeibours[0][0], pixelNeibours[0][1], pixelNeibours[0][2],
                pixelNeibours[1][2], pixelNeibours[2][2], pixelNeibours[2][1]};
    }
}
