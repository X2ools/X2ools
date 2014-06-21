
package org.x2ools.system;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import org.x2ools.Utils;
import org.x2ools.X2oolsActivity;
import org.x2ools.X2oolsApplication;
import org.x2ools.X2oolsSharedPreferences;

import java.io.File;

public class XRecents {

    public static final String PACKAGE_NAME = "com.android.systemui";

    private static Context mContext;

    private static Class<?> SurfaceControl;

    public static void handleLoadPackage(LoadPackageParam lpparam) {
        if (!lpparam.packageName.equals(PACKAGE_NAME))
            return;

        Class<?> Recents = XposedHelpers.findClass("com.android.systemui.recent.Recents",
                lpparam.classLoader);
        try {
            SurfaceControl = XposedHelpers.findClass("android.view.SurfaceControl",
                    lpparam.classLoader);
        } catch (Throwable t) {
        }

        XposedHelpers.findAndHookMethod(Recents, "toggleRecents", Display.class, int.class,
                View.class, toggleRecentsHook);

    }

    private static XC_MethodReplacement toggleRecentsHook = new XC_MethodReplacement() {

        @Override
        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
            mContext = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
            X2oolsSharedPreferences prefs = new X2oolsSharedPreferences();
            boolean isPortrait = (mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
            boolean t9_search = prefs.getBoolean(X2oolsActivity.KEY_T9_SEARCH, true);
            if (isPortrait && t9_search) {
                saveScreenshot(mContext);

                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);

                intent.setClassName("org.x2ools", "org.x2ools.t9apps.T9AppsActivity");
                ActivityOptions opt = ActivityOptions.makeCustomAnimation(mContext, android.R.anim.fade_in, android.R.anim.fade_out);
                mContext.startActivity(intent, opt.toBundle());
            } else {
                XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
            }
            return null;
        }
    };

    private static void saveScreenshot(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Bitmap shotBitmap = null;
        try {
            shotBitmap = (Bitmap) XposedHelpers.callStaticMethod(Surface.class, "screenshot",
                    Math.min(display.getWidth(), display.getHeight()) / 6,
                    Math.max(display.getWidth(), display.getHeight()) / 6);
        } catch (Throwable t) {
            if (SurfaceControl != null) {
                shotBitmap = (Bitmap) XposedHelpers.callStaticMethod(SurfaceControl, "screenshot",
                        Math.min(display.getWidth(), display.getHeight()) / 6,
                        Math.max(display.getWidth(), display.getHeight()) / 6);
            }
        }
        if (shotBitmap != null) {
            Utils.storeImage(shotBitmap, new File(X2oolsApplication.X2OOLS_DIR, "screenshot.png"));
        }
    }

}
