
package org.x2ools.wechat;

import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.TextView;
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class WeChat {

    public static final String PACKAGE_NAME = "com.tencent.mm";

    public static final String CLASS_SETTINGS_ABOUT_SYSTEM_UI = "com.tencent.mm.ui.setting.SettingsAboutSystemUI";
    public static final String CLASS_MM_TEXTVIEW = "com.tencent.mm.ui.base.MMTextView";
    public static final String CLASS_FIND_MORE_FRIENDS_UI = "com.tencent.mm.ui.pluginapp.FindMoreFriendsUI";
    public static final String CLASS_PREFERENCE = "com.tencent.mm.ui.base.preference.Preference";
    public static final String CLASS_PREFERENCE_TITLE_CATEGORY = "com.tencent.mm.ui.base.preference.PreferenceTitleCategory";

    private static int preference_layout;

    public static void initZygote(StartupParam startupParam) throws Throwable {
        // TODO Auto-generated method stub

    }

    public static void handleInitPackageResources(InitPackageResourcesParam resparam)
            throws Throwable {

        if (!resparam.packageName.equals(PACKAGE_NAME))
            return;

        preference_layout = resparam.res.getIdentifier("mm_preference", "layout", PACKAGE_NAME);
    }

    // FIXME only work for wechat version 5.2.1,versioncode 400
    public static void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {

        if (!lpparam.packageName.equals(PACKAGE_NAME))
            return;

        Class<?> settingsAboutSystemUI = XposedHelpers.findClass(CLASS_SETTINGS_ABOUT_SYSTEM_UI,
                lpparam.classLoader);
        Class<?> mmTextViewClz = XposedHelpers.findClass(CLASS_MM_TEXTVIEW, lpparam.classLoader);
        Class<?> findMoreFriendsUI = XposedHelpers.findClass(CLASS_FIND_MORE_FRIENDS_UI,
                lpparam.classLoader);

        // add font setting in general setting
        // TODO on click listener
        /*
        XposedHelpers.findAndHookMethod(settingsAboutSystemUI, "Bn", new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> preferenceClz = XposedHelpers.findClass(CLASS_PREFERENCE,
                        lpparam.classLoader);
                Class<?> preferenceTitleCategoryClz = XposedHelpers.findClass(
                        CLASS_PREFERENCE_TITLE_CATEGORY,
                        lpparam.classLoader);
                Object preference = preferenceClz.getConstructor(Context.class).newInstance(
                        param.thisObject);
                Object preferenceTitleCategory = preferenceTitleCategoryClz.getConstructor(
                        Context.class).newInstance(
                        param.thisObject);
                Object cFy = XposedHelpers.getObjectField(param.thisObject, "cFy");
                XposedHelpers.callMethod(preferenceTitleCategory, "setTitle", "Xposed设置");
                XposedHelpers.callMethod(preference, "setKey", "key_font");
                XposedHelpers.callMethod(preference, "setTitle", "字体");
                XposedHelpers.callMethod(preference, "setLayoutResource", preference_layout);
                XposedHelpers.callMethod(cFy, "b", preferenceTitleCategory);
                XposedHelpers.callMethod(cFy, "b", preference);
            }
        });
        */

        // change chatting font
        // TODO custom font
        XposedHelpers.findAndHookMethod(mmTextViewClz, "init", new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                TextView tv = (TextView) param.thisObject;
                tv.setTypeface(Typeface.createFromFile("/sdcard/DroidSansFallback.ttf"));
                //tv.setTextColor(Color.BLUE);
                tv.setTextSize(20);
            }
        });

        // remove game plugin
        // TODO add checkbox preference in general setting
        
        XposedHelpers.findAndHookMethod(findMoreFriendsUI, "aNn", new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object cFy = XposedHelpers.getObjectField(param.thisObject, "cFy");
                XposedHelpers.callMethod(cFy, "S", "more_tab_game_recommend", true);
            }
        });
        

    }

}
