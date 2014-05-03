
package org.x2ools.system;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.XModuleResources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.WindowManager;

import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import org.x2ools.R;
import org.x2ools.Utils;
import org.x2ools.X2oolsActivity;
import org.x2ools.X2oolsSharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class XActivity {

    public static final String ACTION_CHANGE_STATUS_BAR = "X2ools.action.change.status.bar";
    public static final String ACTION_CHANGE_NAVIGATION_BAR = "X2ools.action.change.navigation.bar";

    public static final int KITKAT_TRANSPARENT_COLOR = Color.parseColor("#66000000");
    public static XModuleResources mResources;

    public static BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_CHANGE_STATUS_BAR)) {
                int color = intent.getIntExtra("statusBarColor", 0);
                if (color != 0 && XSystemUI.sStatusBarView != null) {
                    XSystemUI.sStatusBarView.setBackgroundColor(color);
                    if (intent.getBooleanExtra("shouldEnableDrawable", false)) {
                        XSystemUI.sStatusBarView.setBackground(new BarBackgroundDrawable(
                                XSystemUI.sStatusBarView
                                        .getContext(), mResources, R.drawable.status_background));
                    }
                }
            }

            if (intent.getAction().equals(ACTION_CHANGE_NAVIGATION_BAR)) {
                int color = intent.getIntExtra("navBarColor", 0);
                if (color != 0 && XSystemUI.sNavigationBarView != null) {
                    XSystemUI.sNavigationBarView.setBackgroundColor(color);
                    if (intent.getBooleanExtra("shouldEnableDrawable", false)) {
                        XSystemUI.sNavigationBarView.setBackground(new BarBackgroundDrawable(
                                XSystemUI.sNavigationBarView.getContext(), mResources,
                                R.drawable.nav_background));
                    }
                }
            }

            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                if (Utils.isKeyguardLocked(context)) {
                    XSystemUI.sStatusBarView.setBackgroundColor(KITKAT_TRANSPARENT_COLOR);
                    XSystemUI.sNavigationBarView.setBackgroundColor(KITKAT_TRANSPARENT_COLOR);
                    XSystemUI.sStatusBarView.setBackground(new BarBackgroundDrawable(
                            XSystemUI.sStatusBarView
                                    .getContext(), mResources, R.drawable.status_background));
                    XSystemUI.sNavigationBarView.setBackground(new BarBackgroundDrawable(
                            XSystemUI.sNavigationBarView
                                    .getContext(), mResources, R.drawable.nav_background));

                }
            }
        }
    };

    public static void initZygote(StartupParam startupParam) throws Throwable {

        mResources = XModuleResources.createInstance(startupParam.modulePath, null);

        Class<?> ActivityClass = XposedHelpers.findClass("android.app.Activity", null);
        XposedHelpers.findAndHookMethod(ActivityClass, "performResume", new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;

                Intent statusBarIntent = new Intent();
                statusBarIntent.setAction(ACTION_CHANGE_STATUS_BAR);
                Intent navBarIntent = new Intent();
                navBarIntent.setAction(ACTION_CHANGE_NAVIGATION_BAR);

                X2oolsSharedPreferences prefs = new X2oolsSharedPreferences();
                int tintColor = prefs.getInt(X2oolsActivity.KEY_STATUS_COLOR, Color.TRANSPARENT);

                boolean shouldEnableDrawable = false;

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    int flags = activity.getWindow().getAttributes().flags;
                    if ((flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) {
                        tintColor = KITKAT_TRANSPARENT_COLOR;
                        shouldEnableDrawable = true;
                    }

                    if ((flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION) == WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION) {
                        tintColor = KITKAT_TRANSPARENT_COLOR;
                        shouldEnableDrawable = true;
                    }
                }
                if (tintColor == Color.TRANSPARENT) {
                    View container = null;
                    boolean hasActionBarLikeView = false;
                    View dector = activity.getWindow().getDecorView();
                    List<View> findViews = new ArrayList<View>();
                    Utils.findViewsByClass(dector, "ActionBarContainer", findViews);
                    if (findViews.size() > 0) {
                        container = findViews.get(0);
                        hasActionBarLikeView = true;
                    } else {
                        if (activity.getPackageName().equals("com.baidu.tieba")) {
                            Utils.findViewsByClass(dector, "NavigationBar", findViews);
                            if (findViews.size() > 0) {
                                container = findViews.get(0);
                                hasActionBarLikeView = true;
                            }
                        }

                        if (activity.getPackageName().equals("com.twitter.android")) {
                            Utils.findViewsByClass(dector, "ToolBar", findViews);
                            if (findViews.size() > 0) {
                                container = findViews.get(0);
                                hasActionBarLikeView = true;
                            }
                        }

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

                            int red = Color.red(tintColor);
                            int green = Color.green(tintColor);
                            int blue = Color.blue(tintColor);
                            int alpha = Color.alpha(tintColor);
                            XposedBridge.log("red:" + red + " green:" + green + " blue:" + blue
                                    + " alpha:" + alpha);
                            if (red > 204 && green > 204 && blue > 204) {
                                // do nothing when color is about white
                                tintColor = Color.BLACK;
                            }
                            if (alpha < 152) {
                                // some apps don't have drawable, it's alpha
                                // is 0, like bilibili
                                tintColor = Color.BLACK;
                            }
                        }
                    }
                }

                statusBarIntent.putExtra("statusBarColor", tintColor);
                statusBarIntent.putExtra("shouldEnableDrawable", shouldEnableDrawable);
                activity.sendBroadcast(statusBarIntent);

                navBarIntent.putExtra("navBarColor", tintColor);
                navBarIntent.putExtra("shouldEnableDrawable", shouldEnableDrawable);
                activity.sendBroadcast(navBarIntent);
            }
        });
    }

}
