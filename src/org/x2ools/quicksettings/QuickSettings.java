package org.x2ools.quicksettings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import org.x2ools.R;
import org.x2ools.X2oolsActivity;
import org.x2ools.X2oolsSharedPreferences;

public class QuickSettings {

    public static final String PACKAGE_NAME = "com.android.systemui";
    private static final String CLASS_QUICK_SETTINGS = "com.android.systemui.statusbar.phone.QuickSettings";
    private static final String CLASS_PHONE_STATUSBAR = "com.android.systemui.statusbar.phone.PhoneStatusBar";
    private static final String CLASS_PANEL_BAR = "com.android.systemui.statusbar.phone.PanelBar";

    private static Context mContext;
    private static Context mX2oolContext;
    private static ViewGroup mContainerView;
    private static Object mPanelBar;
    private static Object mStatusBar;
    private static Object mQuickSettings;

    private static WechatScanTile scanTile;
    private static boolean canAddScanTile = false;
    private static X2oolsSharedPreferences x2ools_prefs;

    private static BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(X2oolsActivity.ACTION_WECHAT_SCAN_CHANGED)) {
                canAddScanTile = intent.getBooleanExtra(X2oolsActivity.KEY_WECHAT_SCAN, false);
                updateTileLayout();
            }
        }
    };

    public static void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {

        if (!lpparam.packageName.equals(PACKAGE_NAME))
            return;

        x2ools_prefs = new X2oolsSharedPreferences();

        Class<?> quickSettingsClass = XposedHelpers.findClass(CLASS_QUICK_SETTINGS, lpparam.classLoader);
        Class<?> phoneStatusBarClass = XposedHelpers.findClass(CLASS_PHONE_STATUSBAR, lpparam.classLoader);
        Class<?> panelBarClass = XposedHelpers.findClass(CLASS_PANEL_BAR, lpparam.classLoader);

        XposedBridge.hookAllConstructors(quickSettingsClass, quickSettingsConstructHook);
        XposedHelpers.findAndHookMethod(quickSettingsClass, "setBar", panelBarClass, quickSettingsSetBarHook);
        XposedHelpers.findAndHookMethod(quickSettingsClass, "setService", phoneStatusBarClass,
                quickSettingsSetServiceHook);
        XposedHelpers.findAndHookMethod(quickSettingsClass, "addSystemTiles", ViewGroup.class, LayoutInflater.class,
                quickSettingsAddSystemTilesHook);
    }
    
    private static XC_MethodHook quickSettingsConstructHook = new XC_MethodHook() {

        @Override
        protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
            mQuickSettings = param.thisObject;
            mContext = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
            mX2oolContext = mContext.createPackageContext(X2oolsActivity.X2OOL_PACKAGE_NAME,
                    Context.CONTEXT_IGNORE_SECURITY);
            mContainerView = (ViewGroup) XposedHelpers.getObjectField(param.thisObject, "mContainerView");

            IntentFilter filter = new IntentFilter();
            filter.addAction(X2oolsActivity.ACTION_WECHAT_SCAN_CHANGED);
            mContext.registerReceiver(mBroadcastReceiver, filter);
        }
    };

    private static XC_MethodHook quickSettingsSetBarHook = new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
            mPanelBar = param.args[0];
        }
    };

    private static XC_MethodHook quickSettingsSetServiceHook = new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
            mStatusBar = param.args[0];
        }
    };

    private static XC_MethodHook quickSettingsAddSystemTilesHook = new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(final MethodHookParam param) throws Throwable {

            LayoutInflater inflater = (LayoutInflater) param.args[1];

            scanTile = new WechatScanTile(mContext, mX2oolContext, mStatusBar, mPanelBar);
            scanTile.setupQuickSettingsTile(mContainerView, inflater, mQuickSettings);

            scanTile.setVisibility(mContainerView, x2ools_prefs.getBoolean(X2oolsActivity.KEY_WECHAT_SCAN, false));
        }
    };

    private static void updateTileLayout() {
        if (mContainerView == null)
            return;

        scanTile.setVisibility(mContainerView, canAddScanTile);
    }

    public static class TileLayout {
        public int numColumns;
        public int textSize;
        public int imageSize;
        public int imageMarginTop;
        public int imageMarginBottom;
        public LabelStyle labelStyle;

        public enum LabelStyle {
            NORMAL, ALLCAPS, HIDDEN
        };

        public TileLayout(Context context, int numCols, int orientation, LabelStyle lStyle) {
            numColumns = numCols;
            labelStyle = lStyle;

            final Resources res = context.getResources();
            textSize = 12;
            try {
                imageMarginTop = res.getDimensionPixelSize(res.getIdentifier("qs_tile_margin_above_icon", "dimen",
                        PACKAGE_NAME));
                imageMarginBottom = res.getDimensionPixelSize(res.getIdentifier("qs_tile_margin_below_icon", "dimen",
                        PACKAGE_NAME));
                imageSize = res.getDimensionPixelSize(res.getIdentifier("qs_tile_icon_size", "dimen", PACKAGE_NAME));
            } catch (Resources.NotFoundException rnfe) {
                final Resources x2oolRes = mX2oolContext.getResources();
                imageMarginTop = x2oolRes.getDimensionPixelSize(R.dimen.qs_tile_margin_above_icon);
                imageMarginBottom = x2oolRes.getDimensionPixelSize(R.dimen.qs_tile_margin_below_icon);
                imageSize = x2oolRes.getDimensionPixelSize(R.dimen.qs_tile_icon_size);
            }
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                switch (numColumns) {
                case 4:
                    textSize = 10;
                    imageMarginTop = Math.round(imageMarginTop * 0.6f);
                    imageMarginBottom = Math.round(imageMarginBottom * 0.6f);
                    break;
                case 5:
                    textSize = 8;
                    imageMarginTop = Math.round(imageMarginTop * 0.3f);
                    imageMarginBottom = Math.round(imageMarginBottom * 0.3f);
                    break;
                }
            }

            if (labelStyle == LabelStyle.HIDDEN) {
                imageMarginTop += TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, textSize,
                        res.getDisplayMetrics());
            }
        }
    }
}