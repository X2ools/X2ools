
package org.x2ools.system;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;

import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import org.x2ools.X2oolsActivity;
import org.x2ools.X2oolsSharedPreferences;

public class XActionBarContainer {
    public static void initZygote(StartupParam startupParam) throws Throwable {
        Class<?> ActionBarContainerClass = XposedHelpers.findClass(
                "com.android.internal.widget.ActionBarContainer", null);
        try {
            XposedHelpers.findAndHookMethod(ActionBarContainerClass, "setPrimaryBackground",
                    Drawable.class, setPrimaryBackgroundHook);
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
    }

    private static XC_MethodHook setPrimaryBackgroundHook = new XC_MethodHook() {

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            X2oolsSharedPreferences prefs = new X2oolsSharedPreferences();
            int tintColor = prefs.getInt(X2oolsActivity.KEY_STATUS_COLOR, Color.TRANSPARENT);
            if (tintColor == Color.TRANSPARENT) {
                Activity activity = (Activity) ((View) param.thisObject).getContext();
                XPhoneStatusBar.changeColorAuto(activity, true);
            }
        }

    };

}
