
package org.x2ools.contextsettings;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import org.x2ools.R;
import org.x2ools.X2oolsActivity;

public class ContextSettingsService extends Service implements ContextSettingsView.CallBack {
    private static final String TAG = "ContextSettingsService";
    private ContextSettingsView mParentView;
    private WindowManager mWindowManager;
    public static String ACTION_CONTEXT_SETTINGS = "ACTION_CONTEXT_SETTINGS";
    public static String KEY_ENABLE = "enable";

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private LayoutParams mLayoutParams;

    @Override
    public void onCreate() {
        IntentFilter filter = new IntentFilter(ACTION_CONTEXT_SETTINGS);
        registerReceiver(mReceiver, filter);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getBoolean(X2oolsActivity.KEY_CONTEXT_SETTINGS, true)) {
            addView();
        }
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeView();
    }

    public void addView() {
        if (mParentView == null || mLayoutParams == null) {
            mParentView = (ContextSettingsView) LayoutInflater.from(this)
                    .inflate(R.layout.context_settings, null)
                    .findViewById(R.id.contextSettingsView);
            mParentView.setCallBack(this);
            mLayoutParams = new LayoutParams();
            mLayoutParams.y = mWindowManager.getDefaultDisplay().getHeight()/2;
            mLayoutParams.x = mWindowManager.getDefaultDisplay().getWidth();
            mLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
            mLayoutParams.width = LayoutParams.WRAP_CONTENT;
            mLayoutParams.height = LayoutParams.WRAP_CONTENT;
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

            mLayoutParams.flags =
                    LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | LayoutParams.FLAG_NOT_FOCUSABLE;

            mLayoutParams.format = PixelFormat.TRANSLUCENT;
        }
        if (mParentView.getWindowToken() == null) {
            mWindowManager.addView(mParentView, mLayoutParams);
        }
    }

    public void removeView() {
        Log.d(TAG, "removeView " + mParentView + mParentView.getWindowToken());
        if (mParentView != null && mParentView.getWindowToken() != null) {
            mWindowManager.removeView(mParentView);
        }
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean enable = intent.getBooleanExtra(KEY_ENABLE, true);
            Log.d(TAG, "onReceive " + enable);
            if (enable) {
                addView();
            }
            else {
                removeView();
            }
        }

    };

    @Override
    public void onMoved(int dx, int dy) {
        mLayoutParams.x = (int) dx;
        mLayoutParams.y = (int) dy;
        Log.d(TAG, "updateViewLayout : " + mLayoutParams.x + " , " + mLayoutParams.y);
        mWindowManager.updateViewLayout(mParentView, mLayoutParams);
    }

    public static final boolean FOCUSABLE_IN_EXPAND_MODE = false;

    @Override
    public void onModeChanged(boolean expand) {
        if (FOCUSABLE_IN_EXPAND_MODE) {
            if (expand) {
                mLayoutParams.flags &= ~LayoutParams.FLAG_NOT_FOCUSABLE;
            }
            else {
                mLayoutParams.flags |= LayoutParams.FLAG_NOT_FOCUSABLE;
            }
            mWindowManager.updateViewLayout(mParentView, mLayoutParams);
        }
    }
}
