package org.x2ools.system;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import android.content.Context;
import android.content.IntentFilter;
import android.view.View;
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers.ClassNotFoundError;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class XSystemUI {
    public static final String PACKAGE_NAME = "com.android.systemui";

    public static View sNavigationBarView;
    public static View sStatusBarView;

    public static void initZygote(StartupParam startupParam) throws Throwable {
    }

    public static void handleInitPackageResources(InitPackageResourcesParam resparam) throws Throwable {
        if (!resparam.packageName.equals(PACKAGE_NAME))
            return;
    }

    public static void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {

        if (!lpparam.packageName.equals(PACKAGE_NAME))
            return;

        Class<?> PhoneStatusBar = findClass("com.android.systemui.statusbar.phone.PhoneStatusBar", lpparam.classLoader);
        try {
            findAndHookMethod(PhoneStatusBar, "makeStatusBarView", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        sNavigationBarView = (View) getObjectField(param.thisObject, "mNavigationBarView");
                    } catch (NoSuchFieldError e) {
                    }
                }
            });
        } catch (NoSuchMethodError e) {
        }

        try {
            Class<?> PhoneStatusBarView = findClass("com.android.systemui.statusbar.phone.PhoneStatusBarView",
                    lpparam.classLoader);

            XposedBridge.hookAllConstructors(PhoneStatusBarView, new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    sStatusBarView = (View) param.thisObject;
                    Context context = (Context) param.args[0];
                    IntentFilter iF = new IntentFilter();
                    iF.addAction(XActivity.ACTION_CHANGE_STATUS_BAR);
                    iF.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
                    context.registerReceiver(XActivity.mReceiver, iF);
                    
                    IntentFilter iF2 = new IntentFilter();
                    iF2.addAction(XActivity.ACTION_CHANGE_NAVIGATION_BAR);
                    iF2.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
                    context.registerReceiver(XActivity.mReceiver, iF2);
                }
            });
        } catch (ClassNotFoundError e) {

        }
    }
}
