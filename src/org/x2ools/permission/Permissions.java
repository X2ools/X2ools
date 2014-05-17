package org.x2ools.permission;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.x2ools.X2oolsActivity;
import org.x2ools.X2oolsSharedPreferences;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Permissions {
    protected static final String TAG = "Permissions : ";
	private static final boolean DEBUG_PROTECTED_BROADCAST = false;
	private static final boolean DEBUG_findClass_forName = true;
	public static boolean DEBUG = false;
    private static X2oolsSharedPreferences x2ools_prefs;

	public static void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        x2ools_prefs = new X2oolsSharedPreferences();
        if(x2ools_prefs.getBoolean(X2oolsActivity.KEY_PERMISSION_ALLOW, false)) {
        	return;
        }
		if(DEBUG) {
			if (!lpparam.packageName.equals("android")) {
	        	XposedBridge.log(TAG + "ignore" + lpparam.packageName);
	            return;
	        }
	        else {
	        	XposedBridge.log(TAG + "loading android");
	        }
		}

    	final String CLASS_PackageManagerService = "com.android.server.pm.PackageManagerService";
    	final String CLASS_ActivityManagerService = "com.android.server.am.ActivityManagerService";
    	final String CLASS_ProcessRecord = "com.android.server.am.ProcessRecord";

    	
    	Class<?> classPackageManagerService = XposedHelpers.findClass(CLASS_PackageManagerService, 
    			lpparam.classLoader);  
    	
    	Class<?> classActivityManagerService = XposedHelpers.findClass(CLASS_ActivityManagerService, 
    			lpparam.classLoader);  
    	
    	final Class<?> classProcessRecord = XposedHelpers.findClass(CLASS_ProcessRecord, 
    			lpparam.classLoader);  
    	
    	if(DEBUG_findClass_forName) {
    		Class<?> forNameClassProcessRecord =  Class.forName(CLASS_ProcessRecord);
    		XposedBridge.log("classProcessRecord : " +  classProcessRecord);
    		XposedBridge.log("forNameClassProcessRecord : " +  forNameClassProcessRecord);
    	}
    	XposedHelpers.findAndHookMethod(classPackageManagerService, 
    			"checkUidPermission", String.class, int.class, new XC_MethodReplacement() {

					@Override
					protected Object replaceHookedMethod(MethodHookParam param)
							throws Throwable {
						return 0;
					}
    		
    	});
    	/*Debug protectedBroadcast*/
    	if(DEBUG_PROTECTED_BROADCAST) {
    		
    		for (Method m : classActivityManagerService.getDeclaredMethods()) {
    			XposedBridge.log(TAG + m);
    		}

    		Method  method = XposedHelpers.findMethodBestMatch(classActivityManagerService, 
        			"broadcastIntentLocked", 
        			classProcessRecord,//0
        			String.class, //1
        			Intent.class,//2
        			String.class,//3
        			Class.forName("android.content.IIntentReceiver"),//4
        			int.class, //5
        			String.class,//6
        			Bundle.class, //7
        			String.class,//8
        			int.class,//9
        			boolean.class,//10
        			boolean.class,//11
        			int.class, //12
        			int.class,//13, callingUid 
        			int.class);//14, 
			XposedBridge.log(TAG + "method " + method);

        	XposedHelpers.findAndHookMethod(classActivityManagerService, 
        			"broadcastIntentLocked", 
        			classProcessRecord,//0
        			String.class, //1
        			Intent.class,//2
        			String.class,//3
        			Class.forName("android.content.IIntentReceiver"),//4
        			int.class, //5
        			String.class,//6
        			Bundle.class, //7
        			String.class,//8
        			int.class,//9
        			boolean.class,//10
        			boolean.class,//11
        			int.class, //12
        			int.class,//13, callingUid 
        			int.class,//14, 
        			new XC_MethodHook() {

						@Override
						protected void beforeHookedMethod(MethodHookParam param)
								throws Throwable {
							Intent intent = new Intent((Intent)param.args[2]);
							int callingUid = (Integer)param.args[12];
							Object processRecordObject = param.args[0];
							Field fieldPersistent = classProcessRecord.getDeclaredField("persistent");
							fieldPersistent.setAccessible(true);
							
							XposedBridge.log(intent.getAction() + 
									"\n callingUid : "  + callingUid + 
									"\n processRecordObject : "  + processRecordObject + 
									"\n persistent : "  + fieldPersistent.getBoolean(processRecordObject));
							
							Log.d(TAG, "action : " + intent.getAction() + 
									"\n callingUid : "  + callingUid + 
									"\n processRecordObject : "  + processRecordObject + 
									"\n persistent : "  + fieldPersistent.getBoolean(processRecordObject)
									);
							super.beforeHookedMethod(param);
						}
        			
        	});
    	}
    	if(!DEBUG_PROTECTED_BROADCAST) {
        	XposedHelpers.findAndHookMethod(classPackageManagerService, 
        			"isProtectedBroadcast", String.class, new XC_MethodReplacement() {

    					@Override
    					protected Object replaceHookedMethod(MethodHookParam param)
    							throws Throwable {
    						return false;
    					}
        		
        	});
    	}
    	
    	/**Signatures begin**/
    	XposedBridge.log("hook compareSignatures");
    	Class<?> classSignature = XposedHelpers.findClass("[Landroid.content.pm.Signature;", lpparam.classLoader);  
    	Method[] methods = classPackageManagerService.getMethods();
    	for(Method method : methods) {
    		Log.d(TAG, "method : " + method);
    		XposedBridge.log(TAG +  "method : " + method);
    	}
    	XposedHelpers.findAndHookMethod(classPackageManagerService, 
    			"compareSignatures", classSignature, classSignature, new XC_MethodReplacement() {

					@Override
					protected Object replaceHookedMethod(MethodHookParam param)
							throws Throwable {
						return 0;//PackageManager.SignatureMatch
					}
    		
    	});
    	
    	Class<?> classPackageSetting = XposedHelpers.findClass("com.android.server.pm.PackageSetting", lpparam.classLoader);  
    	Class<?> classPackageParser_Package = XposedHelpers.findClass("com.android.server.pm.PackageParser$Package", lpparam.classLoader);  
    	XposedBridge.log("hook verifySignaturesLP");
    	XposedHelpers.findAndHookMethod(classPackageManagerService, 
    			"verifySignaturesLP", classPackageSetting, classPackageParser_Package, new XC_MethodReplacement() {
					@Override
					protected Object replaceHookedMethod(MethodHookParam param)
							throws Throwable {
						XposedBridge.log(new Throwable());
						Log.d(TAG, "", new Throwable());
						return true;//PackageManager.SignatureMatch
					}
    		
    	});
    	
    	/**Signatures end**/
    }
}
