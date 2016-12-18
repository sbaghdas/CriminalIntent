package com.example.suren.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class PictureUtils {
    public static Bitmap getScaledBitmap(String fileName, int destWidth, int destHeight) {
        BitmapFactory.Options options;
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileName, options);
        int scaleFactor;
        if (options.outWidth > destWidth || options.outHeight > destHeight) {
            if (options.outWidth / destWidth > options.outHeight / destHeight) {
                scaleFactor = (int)Math.ceil(options.outWidth / destWidth);
            } else {
                scaleFactor = (int)Math.ceil(options.outHeight / destHeight);
            }
        } else {
            scaleFactor = 1;
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = scaleFactor;
        return BitmapFactory.decodeFile(fileName, options);
    }

    public static Bitmap getScaledBitmap(String fileName, Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return getScaledBitmap(fileName, size.x, size.y);
    }
}
