package org.x2ools;

import android.app.Application;
import android.util.Log;

import com.juuda.droidmock.mock.Mocks;

import org.x2ools.mocks.ViewDebugMocker;

public class X2oolsApplication extends Application{

    private static final String TAG = "X2oolsApplication";

    @Override
    public void onCreate() {
        Log.d(TAG, "hahah");
        Mocks.sModuleMap.put("viewdebug", ViewDebugMocker.class);

        super.onCreate();
    }
    
}
