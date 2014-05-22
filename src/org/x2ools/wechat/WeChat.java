
package org.x2ools.wechat;

import android.graphics.Typeface;
import android.text.TextUtils;
import android.widget.TextView;

import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import org.x2ools.X2oolsActivity;
import org.x2ools.X2oolsSharedPreferences;

public class WeChat {
    public static final String PACKAGE_NAME = "com.tencent.mm";

    public static final String CLASS_MM_TEXTVIEW = "com.tencent.mm.ui.base.MMTextView";

    // public static final String CLASS_FIND_MORE_FRIENDS_UI =
    // "com.tencent.mm.ui.pluginapp.FindMoreFriendsUI";

    private static X2oolsSharedPreferences x2ools_prefs;

    public static void initZygote(StartupParam startupParam) throws Throwable {
    }

    public static void handleInitPackageResources(InitPackageResourcesParam resparam)
            throws Throwable {

        if (!resparam.packageName.equals(PACKAGE_NAME))
            return;

    }

    // FIXME only work for wechat version 5.2.1,versioncode 400
    public static void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {

        if (!lpparam.packageName.equals(PACKAGE_NAME))
            return;

        x2ools_prefs = new X2oolsSharedPreferences();

        Class<?> mmTextViewClz = XposedHelpers.findClass(CLASS_MM_TEXTVIEW, lpparam.classLoader);
        // Class<?> findMoreFriendsUI =
        // XposedHelpers.findClass(CLASS_FIND_MORE_FRIENDS_UI,
        // lpparam.classLoader);

        // change chatting font
        XposedHelpers.findAndHookMethod(mmTextViewClz, "init", new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                TextView tv = (TextView)param.thisObject;
                String font = x2ools_prefs.getString(X2oolsActivity.KEY_WECHAT_CHAT_FONT, "");
                if (!TextUtils.isEmpty(font))
                    tv.setTypeface(Typeface.createFromFile(font));
            }
        });
        /*
         * // remove game plugin
         * XposedHelpers.findAndHookMethod(findMoreFriendsUI, "aNn", new
         * XC_MethodHook() {
         * @Override protected void afterHookedMethod(MethodHookParam param)
         * throws Throwable { Object cFy =
         * XposedHelpers.getObjectField(param.thisObject, "cFy"); boolean
         * canRemove =
         * x2ools_prefs.getBoolean(X2oolsActivity.KEY_WECHAT_REMOVE_GAME,
         * false); XposedHelpers.callMethod(cFy, "S", "more_tab_game_recommend",
         * canRemove); } });
         */
    }
}
