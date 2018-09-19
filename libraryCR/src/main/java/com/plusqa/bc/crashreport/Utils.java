package com.plusqa.bc.crashreport;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Utils {

    public static void saveBitmap(Context context, String imageName, Bitmap b) {
        File image_directory = context.getDir("imageDir", Context.MODE_PRIVATE);
        File path = new File(image_directory, imageName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            b.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap getBitMap(Context context, String imageName) {
        File image_directory = context.getDir("imageDir", Context.MODE_PRIVATE);
        Bitmap bm = null;
        try {
            FileInputStream fi = new FileInputStream(image_directory.toString() + "/" + imageName);
            bm = BitmapFactory.decodeStream(fi);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bm;
    }

    public static void saveLog(Context context, String logName) {

        File logDirectory = context.getDir("logDir", Context.MODE_PRIVATE);

        try {
            Runtime.getRuntime().exec(new String[]{"logcat", "-d", "-f", logDirectory.toString() + "/" + logName});
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static StringBuilder readLogFromInternalMemory(Context context) {
        File log_directory = context.getDir("logDir", Context.MODE_PRIVATE);
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(log_directory.toString() + "/" + "logcat.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();

        if (fis != null) {
            InputStreamReader isr = new InputStreamReader(fis);
            try (BufferedReader bufferedReader = new BufferedReader(isr)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb;
    }

    public static Rect getViewRect(View v) {
        Rect r = new Rect();
        int[] location = new int[2];
        v.getDrawingRect(r);
        v.getLocationOnScreen(location);
        r.offset(location[0], location[1]);
        r.left -= 30; r.top -= 30; r.right += 30; r.bottom += 30;
        return r;
    }

}
