package org.x2ools;

import org.x2ools.superdebug.SuperDebug;
import org.x2ools.wechat.WeChat;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class X2ools implements IXposedHookZygoteInit, IXposedHookInitPackageResources, IXposedHookLoadPackage {

	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		WeChat.initZygote(startupParam);
		SuperDebug.initZygote(startupParam);
	}

	@Override
	public void handleInitPackageResources(InitPackageResourcesParam resparam) throws Throwable {
		WeChat.handleInitPackageResources(resparam);
		SuperDebug.handleInitPackageResources(resparam);
	}

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		WeChat.handleLoadPackage(lpparam);
		SuperDebug.handleLoadPackage(lpparam);
	}

}
