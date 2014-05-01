package org.x2ools;

import org.x2ools.quicksettings.QuickSettings;
import org.x2ools.superdebug.SuperDebug;
import org.x2ools.system.XActivity;
import org.x2ools.system.XSystemUI;
import org.x2ools.wechat.WeChat;

import android.content.res.XResources;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class X2ools implements IXposedHookZygoteInit, IXposedHookInitPackageResources, IXposedHookLoadPackage {

    public static final String PACKAGE_NAME = X2ools.class.getPackage().getName();

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        WeChat.initZygote(startupParam);
        SuperDebug.initZygote(startupParam);
        XActivity.initZygote(startupParam);
    }

    @Override
    public void handleInitPackageResources(InitPackageResourcesParam resparam) throws Throwable {
        WeChat.handleInitPackageResources(resparam);
        SuperDebug.handleInitPackageResources(resparam);
        XResources.setSystemWideReplacement("android", "dimen", "status_bar_height", 200);

//        XResources.setSystemWideReplacement("com.android.internal", "dimen", "status_bar_height", 200);
    }

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        WeChat.handleLoadPackage(lpparam);
        //SuperDebug.handleLoadPackage(lpparam);
        QuickSettings.handleLoadPackage(lpparam);
        XSystemUI.handleLoadPackage(lpparam);
    }

}
