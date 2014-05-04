
package org.x2ools.system;

import android.app.Activity;
import android.content.res.XModuleResources;
import android.graphics.Color;
import android.view.WindowManager;

import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

import org.x2ools.X2oolsActivity;
import org.x2ools.X2oolsSharedPreferences;

public class XActivity {

    public static XModuleResources mResources;
    public static Callback mCallback;

    public static void initZygote(StartupParam startupParam) throws Throwable {

        mResources = XModuleResources.createInstance(startupParam.modulePath, null);

        Class<?> ActivityClass = XposedHelpers.findClass("android.app.Activity", null);
        XposedHelpers.findAndHookMethod(ActivityClass, "performResume", new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;
                
                X2oolsSharedPreferences prefs = new X2oolsSharedPreferences();
                int tintColor = prefs.getInt(X2oolsActivity.KEY_STATUS_COLOR, Color.TRANSPARENT);
                
                if(tintColor == Color.TRANSPARENT) {
                    boolean hasActionBar = false;
                    if(activity.getActionBar()!=null)
                        hasActionBar = true;
                    XSystemUI.changeColorAuto(activity,hasActionBar);
                } else {
                    XSystemUI.changeColorCustom(activity,tintColor);
                }
                
            }
        });
    }

    public static void handleInitPackageResources(final InitPackageResourcesParam resparam)
            throws Throwable {
        mCallback = new Callback() {

            @Override
            public int getIdentifier(String name, String packageName) {
                return resparam.res.getIdentifier(name, "id", packageName);
            }
        };
    }

    public interface Callback {
        public int getIdentifier(String name, String packageName);
    }
    
    public static boolean getTranslucentState(Activity activity) {
        boolean translucent = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            int flags = activity.getWindow().getAttributes().flags;
            if ((flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) {
                translucent = true;
            }

            if ((flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION) == WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION) {
                translucent = true;
            }
        }
        return translucent;
    }

}
