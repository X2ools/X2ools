package org.x2ools.systemui;

import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class XSystemUI {
    public static final String PACKAGE_NAME = "com.android.systemui";

    public static void initZygote(StartupParam startupParam) throws Throwable {
    }

    public static void handleInitPackageResources(InitPackageResourcesParam resparam) throws Throwable {
        if (!resparam.packageName.equals(PACKAGE_NAME))
            return;
    }

    public static void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {

    }
}
