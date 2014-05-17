
package org.x2ools;
import android.content.res.XResources;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import org.x2ools.permission.Permissions;
import org.x2ools.quicksettings.QuickSettings;
import org.x2ools.superdebug.SuperDebug;
import org.x2ools.system.XActionBarContainer;
import org.x2ools.system.XActivity;
import org.x2ools.system.XPhoneStatusBar;
import org.x2ools.system.XRecents;
import org.x2ools.wechat.WeChat;

public class X2ools implements IXposedHookZygoteInit, IXposedHookInitPackageResources,
        IXposedHookLoadPackage {

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        WeChat.initZygote(startupParam);
        SuperDebug.initZygote(startupParam);
        XActivity.initZygote(startupParam);
        XActionBarContainer.initZygote(startupParam);
    }

    @Override
    public void handleInitPackageResources(InitPackageResourcesParam resparam) throws Throwable {
        XActivity.handleInitPackageResources(resparam);
        WeChat.handleInitPackageResources(resparam);
        SuperDebug.handleInitPackageResources(resparam);
    }

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        WeChat.handleLoadPackage(lpparam);
        // SuperDebug.handleLoadPackage(lpparam);
        QuickSettings.handleLoadPackage(lpparam);
        XPhoneStatusBar.handleLoadPackage(lpparam);
        XRecents.handleLoadPackage(lpparam);
        Permissions.handleLoadPackage(lpparam);
    }

}
