package org.x2ools;

import java.util.List;

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
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;

public class Utils {
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

    public static int getMainColorFromActionBarDrawable(Drawable drawable) throws IllegalArgumentException {
        /*
         * This should fix the bug where a huge part of the ActionBar background
         * is drawn white.
         */
        Drawable copyDrawable = drawable.getConstantState().newDrawable();

        if (copyDrawable instanceof ColorDrawable) {
            return ((ColorDrawable) drawable).getColor();
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

    public static int getMainColorFromActionBarBitmap(Bitmap bitmap) throws IllegalArgumentException {

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
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
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
}
