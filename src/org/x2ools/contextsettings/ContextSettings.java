
package org.x2ools.contextsettings;

import android.util.Log;
import android.view.View;

import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import java.lang.ref.WeakReference;

public class ContextSettings {
    public static final String PACKAGE_NAME = "com.android.systemui";

    public static final String CLASS_StatusBarWindowView = "com.android.systemui.statusbar.phone.StatusBarWindowView";

    public static final String CLASS_Window = "android.view.Window$Callback";

    public static final String CLASS_PhoneWindow = "com.android.internal.policy.impl.PhoneWindow$DecorView";

    public static final boolean DEBUG = true;

    public static final String TAG = "SuperDebug";

    public static void initZygote(StartupParam startupParam) throws Throwable {

    }

    public static WeakReference<View> sFocusedWindowView = null;

    public static WeakReference<View> sStatusBarWindowView = null;

    public static View sStatusBarWindowViewX = null;

    public static void handleInitPackageResources(InitPackageResourcesParam resparam)
            throws Throwable {

    }

    public static void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        final String packageInfo = "package : " + lpparam.packageName + "  process: "
                + lpparam.processName;
        XposedBridge.log(packageInfo);

        Class<?> phoneWindowClz = XposedHelpers.findClass(CLASS_PhoneWindow, lpparam.classLoader);
        XposedHelpers.findAndHookMethod(phoneWindowClz, "onWindowFocusChanged", boolean.class,
                new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        final View dectorView = (View)param.thisObject;
                        final boolean hasFocus = (Boolean)param.args[0];
                        if (hasFocus) {
                            sFocusedWindowView = new WeakReference<View>(dectorView);
                            Log.d(TAG, "currentDectorView : " + sFocusedWindowView);
                        }
                        super.afterHookedMethod(param);
                    }
                });
    }

}
