
package org.x2ools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;
import org.x2ools.quicksettings.QuickSettings;
import org.x2ools.superdebug.SuperDebug;
import org.x2ools.wechat.WeChat;

import android.os.Environment;
import android.text.TextUtils;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class X2ools implements IXposedHookZygoteInit, IXposedHookInitPackageResources,
        IXposedHookLoadPackage {
	
	public static final String PACKAGE_NAME = X2ools.class.getPackage().getName();

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
        QuickSettings.handleLoadPackage(lpparam);
    }
    
    public static JSONObject getJsonPrefs() {
    	JSONObject json = null;
        try {
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
        return json;
    }

}
