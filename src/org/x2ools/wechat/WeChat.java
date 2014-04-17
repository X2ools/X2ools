
package org.x2ools.wechat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;

import org.json.JSONObject;
import org.x2ools.X2oolsActivity;

import android.graphics.Typeface;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.TextView;
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
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
    
    private static JSONObject json = null;
    

    public static void initZygote(StartupParam startupParam) throws Throwable {
        // TODO Auto-generated method stub

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
        
        File jsonFile = new File(Environment.getExternalStorageDirectory()+"/X2ools/prefs.json");
        InputStreamReader isr = new InputStreamReader(new FileInputStream(jsonFile));
        BufferedReader bufferedReader = new BufferedReader(isr);
        String receiveString = "";
        StringBuilder stringBuilder = new StringBuilder();

        while ( (receiveString = bufferedReader.readLine()) != null ) {
            stringBuilder.append(receiveString);
        }

        if(!TextUtils.isEmpty(stringBuilder))
        	json = new JSONObject(stringBuilder.toString());

        Class<?> mmTextViewClz = XposedHelpers.findClass(CLASS_MM_TEXTVIEW, lpparam.classLoader);
        Class<?> findMoreFriendsUI = XposedHelpers.findClass(CLASS_FIND_MORE_FRIENDS_UI,
                lpparam.classLoader);

        // change chatting font
        XposedHelpers.findAndHookMethod(mmTextViewClz, "init", new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                TextView tv = (TextView) param.thisObject;
                if(json != null) {
                	String font = URLDecoder.decode((String) json.get(X2oolsActivity.KEY_WECHAT_CHAT_FONT), "UTF-8");
                	if(!TextUtils.isEmpty(font))
                		tv.setTypeface(Typeface.createFromFile(font));	
                }
            }
        });

        // remove game plugin
        XposedHelpers.findAndHookMethod(findMoreFriendsUI, "aNn", new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object cFy = XposedHelpers.getObjectField(param.thisObject, "cFy");
                if(json != null) {
                	boolean canRemove = json.getBoolean(X2oolsActivity.KEY_WECHAT_REMOVE_GAME);
                	XposedHelpers.callMethod(cFy, "S", "more_tab_game_recommend", canRemove);
                }
            }
        });
        

    }

}
