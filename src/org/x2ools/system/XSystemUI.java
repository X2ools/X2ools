
package org.x2ools.system;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;

import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XposedHelpers.ClassNotFoundError;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import org.x2ools.R;
import org.x2ools.Utils;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XSystemUI {
    public static final String PACKAGE_NAME = "com.android.systemui";

    public static final String ACTION_CHANGE_STATUS_BAR = "X2ools.action.change.status.bar";

    public static final int KITKAT_TRANSPARENT_COLOR = Color.parseColor("#66000000");

    public static View sNavigationBarView;
    public static View sStatusBarView;

    public static BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(ACTION_CHANGE_STATUS_BAR)) {
                int color = intent.getIntExtra("statusBarColor", 0);
                if (color != 0 && XSystemUI.sStatusBarView != null) {
                    if (color == KITKAT_TRANSPARENT_COLOR) {
                        setStatusBar2Translucent();
                    } else {
                        XSystemUI.sStatusBarView.setBackgroundColor(color);
                        if (XSystemUI.sNavigationBarView != null) {
                            XSystemUI.sNavigationBarView.setBackgroundColor(color);
                        }
                    }
                }
            }

            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON) &&
                    Utils.isKeyguardLocked(context)) {
                setStatusBar2Translucent();
            }
        }
    };

    private static void setStatusBar2Translucent() {
        XSystemUI.sStatusBarView.setBackgroundColor(KITKAT_TRANSPARENT_COLOR);
        XSystemUI.sStatusBarView.setBackground(new BarBackgroundDrawable(
                XSystemUI.sStatusBarView.getContext(), XActivity.mResources,
                R.drawable.status_background));
        if (XSystemUI.sNavigationBarView != null) {
            XSystemUI.sNavigationBarView.setBackgroundColor(KITKAT_TRANSPARENT_COLOR);
            XSystemUI.sNavigationBarView.setBackground(new BarBackgroundDrawable(
                    XSystemUI.sNavigationBarView.getContext(),
                    XActivity.mResources, R.drawable.nav_background));
        }
    }

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

        Class<?> PhoneStatusBar = XposedHelpers.findClass(
                "com.android.systemui.statusbar.phone.PhoneStatusBar",
                lpparam.classLoader);
        try {
            XposedHelpers.findAndHookMethod(PhoneStatusBar, "makeStatusBarView",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            try {
                                sNavigationBarView = (View) XposedHelpers.getObjectField(
                                        param.thisObject,
                                        "mNavigationBarView");
                            } catch (NoSuchFieldError e) {
                            }
                        }
                    });
        } catch (NoSuchMethodError e) {
        }

        try {
            Class<?> PhoneStatusBarView = XposedHelpers.findClass(
                    "com.android.systemui.statusbar.phone.PhoneStatusBarView",
                    lpparam.classLoader);

            XposedBridge.hookAllConstructors(PhoneStatusBarView, new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    sStatusBarView = (View) param.thisObject;
                    Context context = (Context) param.args[0];
                    IntentFilter iF = new IntentFilter();
                    iF.addAction(ACTION_CHANGE_STATUS_BAR);
                    iF.addAction(Intent.ACTION_SCREEN_ON);
                    iF.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
                    context.registerReceiver(mReceiver, iF);
                }
            });
        } catch (ClassNotFoundError e) {

        }
    }

    public static void changeColorAuto(Activity activity, boolean hasActionBar) {

        Intent statusBarIntent = new Intent();
        statusBarIntent.setAction(XSystemUI.ACTION_CHANGE_STATUS_BAR);

        int tintColor = XActivity.getTranslucentState(activity) ? KITKAT_TRANSPARENT_COLOR
                : Color.BLACK;

        if (hasActionBar) {
            View container = (View) XposedHelpers.getObjectField(activity.getActionBar(),
                    "mContainerView");
            Drawable mBackground = (Drawable) XposedHelpers.getObjectField(
                    container, "mBackground");

            if (mBackground != null) {
                tintColor = Utils.getMainColorFromActionBarDrawable(mBackground);
            }

            tintColor = overrideWhiteColor(tintColor);

            statusBarIntent.putExtra("statusBarColor", tintColor);
            container.getContext().sendBroadcast(statusBarIntent);
        } else {
            View container = null;
            boolean hasActionBarLikeView = false;
            View dector = activity.getWindow().getDecorView();
            List<View> findViews = new ArrayList<View>();
            XmlResourceParser xrp = XActivity.mResources.getXml(R.xml.custom_bars);
            try {
                while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
                    if (xrp.getEventType() == XmlResourceParser.START_TAG) {
                        String tagName = xrp.getName();
                        String packageName = xrp.getAttributeValue(null, "package");
                        String className = xrp.getAttributeValue(null, "class");
                        String idName = xrp.getAttributeValue(null, "id");

                        if (tagName.equals("Bar")) {
                            if (idName != null && XActivity.mCallback != null) {
                                int id = XActivity.mCallback.getIdentifier(idName, packageName);
                                if (activity.getPackageName().equals(packageName)) {
                                    Utils.findViewsById(dector, id, findViews);
                                    if (findViews.size() > 0) {
                                        container = findViews.get(0);
                                        hasActionBarLikeView = true;
                                    }
                                }
                            }
                            else if (className != null) {
                                if (activity.getPackageName().equals(packageName)) {
                                    Utils.findViewsByClass(dector, className, findViews);
                                    if (findViews.size() > 0) {
                                        container = findViews.get(0);
                                        hasActionBarLikeView = true;
                                    }
                                }
                            }
                        }
                    }
                    xrp.next();
                }
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (hasActionBarLikeView) {
                if (container != null) {
                    Drawable backgroundDrawable = (Drawable) XposedHelpers.getObjectField(
                            container, "mBackground");
                    if (backgroundDrawable != null) {
                        tintColor = Utils
                                .getMainColorFromActionBarDrawable(backgroundDrawable);
                    } else {
                        Bitmap backgroundBitmap = Utils.convertViewToBitmap(container);
                        tintColor = Utils.getMainColorFromActionBarBitmap(backgroundBitmap);
                    }

                }
            }

            tintColor = overrideWhiteColor(tintColor);

            statusBarIntent.putExtra("statusBarColor", tintColor);
            activity.sendBroadcast(statusBarIntent);
        }
    }

    public static void changeColorCustom(Activity activity, int tintColor) {
        tintColor = XActivity.getTranslucentState(activity) ? KITKAT_TRANSPARENT_COLOR
                : tintColor;

        Intent statusBarIntent = new Intent();
        statusBarIntent.setAction(XSystemUI.ACTION_CHANGE_STATUS_BAR);
        statusBarIntent.putExtra("statusBarColor", tintColor);
        activity.sendBroadcast(statusBarIntent);
    }

    private static int overrideWhiteColor(int tintColor) {
        int red = Color.red(tintColor);
        int green = Color.green(tintColor);
        int blue = Color.blue(tintColor);
        if (red > 204 && green > 204 && blue > 204) {
            // do nothing when color is about white
            tintColor = Color.BLACK;
        }

        // something wrong!
        if (tintColor == Color.TRANSPARENT) {
            tintColor = Color.BLACK;
        }
        return tintColor;
    }
}
