
package org.x2ools.system;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import org.x2ools.X2oolsActivity;
import org.x2ools.X2oolsSharedPreferences;

public class XSearchPanelView {

    public static final String PACKAGE_NAME = "com.android.systemui";
    private static Context mContext;

    public static void handleLoadPackage(LoadPackageParam lpparam) {
        if (!lpparam.packageName.equals(PACKAGE_NAME))
            return;

        Class<?> SearchPanelViewClass = XposedHelpers.findClass(
                "com.android.systemui.SearchPanelView", lpparam.classLoader);
        Class<?> GlowPadTriggerListenerClass = XposedHelpers.findClass(
                "com.android.systemui.SearchPanelView$GlowPadTriggerListener", lpparam.classLoader);

        XposedBridge.hookAllConstructors(SearchPanelViewClass, new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                mContext = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
            }
        });
        XposedHelpers.findAndHookMethod(GlowPadTriggerListenerClass, "onTrigger", View.class,
                int.class, onTriggerHook);

    }

    private static XC_MethodReplacement onTriggerHook = new XC_MethodReplacement() {
        
        @Override
        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
            X2oolsSharedPreferences prefs = new X2oolsSharedPreferences();
            boolean t9_search = prefs.getBoolean(X2oolsActivity.KEY_T9_SEARCH, true);
            if(t9_search) {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setClassName("org.x2ools", "org.x2ools.t9apps.T9AppsActivity");
                mContext.startActivity(intent);
            } else {
                XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
            }
            return null;
        }
    };

}
