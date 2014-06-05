
package org.x2ools;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class Utils {
    private static final String TAG = "Utils";

    public static void findViewsByClass(View v, String className, List<View> result) {
        if (v.getClass().getName().contains(className)) {
            result.add(v);
        }
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                findViewsByClass(vg.getChildAt(i), className, result);
            }
        }
    }

    public static void findViewsById(View v, int id, List<View> result) {
        if (v.getId() == id) {
            result.add(v);
        }
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                findViewsById(vg.getChildAt(i), id, result);
            }
        }
    }

    public static int getMainColorFromActionBarDrawable(Drawable drawable)
            throws IllegalArgumentException {
        /*
         * This should fix the bug where a huge part of the ActionBar background
         * is drawn white.
         */
        Drawable copyDrawable = drawable.getConstantState().newDrawable();

        if (copyDrawable instanceof ColorDrawable) {
            return ((ColorDrawable) drawable).getColor();
        }

        if (copyDrawable instanceof GradientDrawable) {
            Log.d(TAG, "getMainColorFromActionBarDrawable: GradientDrawable");
            try {
                Class<?> GradientDrawableClz = Class
                        .forName("android.graphics.drawable.GradientDrawable");
                Class<?> GradientStateclz = Class
                        .forName("android.graphics.drawable.GradientDrawable$GradientState");
                Field mGradientStateField = GradientDrawableClz.getDeclaredField("mGradientState");
                Field mColorsField = GradientStateclz.getDeclaredField("mSolidColor");
                mGradientStateField.setAccessible(true);
                mColorsField.setAccessible(true);
                Object mGradientState = mGradientStateField.get((GradientDrawable) copyDrawable);
                int color = (Integer) mColorsField.get(mGradientState);
                Log.d(TAG, "getMainColorFromActionBarDrawable: mSolidColor:" + color);
                return color;
            } catch (ClassNotFoundException e) {
            } catch (IllegalAccessException e) {
            } catch (NoSuchFieldException e) {
            }
        }

        Bitmap bitmap = drawableToBitmap(copyDrawable);
        int pixel = bitmap.getPixel(bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        int red = Color.red(pixel);
        int blue = Color.blue(pixel);
        int green = Color.green(pixel);
        int alpha = Color.alpha(pixel);
        copyDrawable = null;
        return Color.argb(alpha, red, green, blue);
    }

    public static int getMainColorFromActionBarBitmap(Bitmap bitmap)
            throws IllegalArgumentException {

        int pixel = bitmap.getPixel(bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        int red = Color.red(pixel);
        int blue = Color.blue(pixel);
        int green = Color.green(pixel);
        int alpha = Color.alpha(pixel);
        return Color.argb(alpha, red, green, blue);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) throws IllegalArgumentException {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap;

        try {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        } catch (IllegalArgumentException e) {
            throw e;
        }

        return bitmap;
    }

    public static Bitmap convertViewToBitmap(View view) {
        view.setDrawingCacheEnabled(true);
        view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        view.destroyDrawingCache();

        return bitmap;
    }

    @SuppressLint("NewApi")
    public static boolean isKeyguardLocked(Context context) {
        KeyguardManager kgm = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        boolean keyguardLocked;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            keyguardLocked = kgm.isKeyguardLocked();
        } else {
            keyguardLocked = kgm.inKeyguardRestrictedInputMode();
        }
        return keyguardLocked;
    }
    
    /**
     * Stores an image on the storage
     * 
     * @param image
     *            the image to store.
     * @param pictureFile
     *            the file in which it must be stored
     */
    public static void storeImage(Bitmap image, File pictureFile) {
        if (pictureFile == null) {
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }
}
