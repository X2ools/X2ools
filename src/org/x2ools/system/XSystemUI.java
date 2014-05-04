
package org.x2ools.system;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;

import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers.ClassNotFoundError;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import org.x2ools.R;
import org.x2ools.Utils;

public class XSystemUI {
    public static final String PACKAGE_NAME = "com.android.systemui";

    public static final String ACTION_CHANGE_STATUS_BAR = "X2ools.action.change.status.bar";
    public static final String ACTION_CHANGE_NAVIGATION_BAR = "X2ools.action.change.navigation.bar";

    public static View sNavigationBarView;
    public static View sStatusBarView;

    public static BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_CHANGE_STATUS_BAR)) {
                int color = intent.getIntExtra("statusBarColor", 0);
                if (color != 0 && XSystemUI.sStatusBarView != null) {
                    XSystemUI.sStatusBarView.setBackgroundColor(color);
                    if (intent.getBooleanExtra("shouldEnableDrawable", false)) {
                        XSystemUI.sStatusBarView.setBackground(new BarBackgroundDrawable(
                                XSystemUI.sStatusBarView.getContext(), XActivity.mResources,
                                R.drawable.status_background));
                    }
                }
            }

            if (intent.getAction().equals(ACTION_CHANGE_NAVIGATION_BAR)) {
                int color = intent.getIntExtra("navBarColor", 0);
                if (color != 0 && XSystemUI.sNavigationBarView != null) {
                    XSystemUI.sNavigationBarView.setBackgroundColor(color);
                    if (intent.getBooleanExtra("shouldEnableDrawable", false)) {
                        XSystemUI.sNavigationBarView.setBackground(new BarBackgroundDrawable(
                                XSystemUI.sNavigationBarView.getContext(), XActivity.mResources,
                                R.drawable.nav_background));
                    }
                }
            }

            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                if (Utils.isKeyguardLocked(context)) {
                    XSystemUI.sStatusBarView.setBackgroundColor(XActivity.KITKAT_TRANSPARENT_COLOR);
                    XSystemUI.sNavigationBarView
                            .setBackgroundColor(XActivity.KITKAT_TRANSPARENT_COLOR);
                    XSystemUI.sStatusBarView.setBackground(new BarBackgroundDrawable(
                            XSystemUI.sStatusBarView.getContext(), XActivity.mResources,
                            R.drawable.status_background));
                    XSystemUI.sNavigationBarView.setBackground(new BarBackgroundDrawable(
                            XSystemUI.sNavigationBarView.getContext(),
                            XActivity.mResources, R.drawable.nav_background));

                }
            }
        }
    };

    public static void initZygote(StartupParam startupParam) throws Throwable {
    }

    public static void handleInitPackageResources(InitPackageResourcesParam resparam)
            throws Throwable {
        if (!resparam.packageName.equals(PACKAGE_NAME))
            return;
    }

    public static void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {

        if (!lpparam.packageName.equals(PACKAGE_NAME))
            return;

        Class<?> PhoneStatusBar = findClass("com.android.systemui.statusbar.phone.PhoneStatusBar",
                lpparam.classLoader);
        try {
            findAndHookMethod(PhoneStatusBar, "makeStatusBarView", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        sNavigationBarView = (View) getObjectField(param.thisObject,
                                "mNavigationBarView");
                    } catch (NoSuchFieldError e) {
                    }
                }
            });
        } catch (NoSuchMethodError e) {
        }

        try {
            Class<?> PhoneStatusBarView = findClass(
                    "com.android.systemui.statusbar.phone.PhoneStatusBarView",
                    lpparam.classLoader);

            XposedBridge.hookAllConstructors(PhoneStatusBarView, new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    sStatusBarView = (View) param.thisObject;
                    Context context = (Context) param.args[0];
                    IntentFilter iF = new IntentFilter();
                    iF.addAction(ACTION_CHANGE_STATUS_BAR);
                    iF.addAction(ACTION_CHANGE_NAVIGATION_BAR);
                    iF.addAction(Intent.ACTION_SCREEN_ON);
                    iF.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
                    context.registerReceiver(mReceiver, iF);
                }
            });
        } catch (ClassNotFoundError e) {

        }
    }
}
