package org.x2ools;

import android.app.Application;
import android.content.Intent;

import com.juuda.droidmock.mock.Mocks;

import org.x2ools.contextsettings.ContextSettingsService;
import org.x2ools.mocks.ViewDebugMocker;

public class X2oolsApplication extends Application{

    private static final String TAG = "X2oolsApplication";

    @Override
    public void onCreate() {
        Mocks.sModuleMap.put("viewdebug", ViewDebugMocker.class);
        startService(new Intent(this, ContextSettingsService.class));
        super.onCreate();
    }
    
}
