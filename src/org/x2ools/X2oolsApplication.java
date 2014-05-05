
package org.x2ools;

import android.app.Application;
import android.content.Intent;
import android.os.Environment;

import com.juuda.droidmock.mock.Mocks;

import org.x2ools.contextsettings.ContextSettingsService;
import org.x2ools.mocks.ViewDebugMocker;

import java.io.File;

public class X2oolsApplication extends Application {

    // private static final String TAG = "X2oolsApplication";
    public static final String X2OOLS_DIR = Environment.getExternalStorageDirectory() + "/X2ools/";
    public static final String X2OOLS_PREFS = Environment.getExternalStorageDirectory() + "/X2ools/"
            + "prefs.json";

    @Override
    public void onCreate() {
        initX2oolsDir();
        Mocks.sModuleMap.put("viewdebug", ViewDebugMocker.class);
        startService(new Intent(this, ContextSettingsService.class));
        super.onCreate();
    }

    private void initX2oolsDir() {
        File dir = new File(X2OOLS_DIR);
        if (!dir.exists()) {
            dir.mkdir();
        } else if (!dir.isDirectory()) {
            dir.delete();
            dir.mkdir();
        }
    }

}
