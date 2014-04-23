package org.x2ools;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceActivity;

public class X2oolsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    public static final String KEY_WECHAT_REMOVE_GAME = "wechat_remove_game";
    public static final String KEY_WECHAT_CHAT_FONT = "wechat_chat_font";
    public static final String KEY_WECHAT_SCAN = "wechat_scan";

    public static final String ACTION_WECHAT_SCAN_CHANGED = "x2ools.action.wechat.scan.changed";

    public static final String X2OOL_DIR = Environment.getExternalStorageDirectory() + "/X2ools/";
    public static final String X2OOL_PACKAGE_NAME = X2oolsActivity.class.getPackage().getName();

    private SharedPreferences prefs;
    private X2oolsSharedPreferences x2ools_prefs;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        prefs = getPreferenceScreen().getSharedPreferences();
        updateX2oolsPrefs();
    }

    @Override
    protected void onResume() {
        prefs.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        prefs.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    private void updateX2oolsPrefs() {
        x2ools_prefs = new X2oolsSharedPreferences();
        Editor editor = x2ools_prefs.edit();
        editor.putString(KEY_WECHAT_CHAT_FONT, prefs.getString(KEY_WECHAT_CHAT_FONT, ""));
        editor.putBoolean(KEY_WECHAT_REMOVE_GAME, prefs.getBoolean(KEY_WECHAT_REMOVE_GAME, false));
        editor.putBoolean(KEY_WECHAT_SCAN, prefs.getBoolean(KEY_WECHAT_SCAN, false));
        editor.commit();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_WECHAT_SCAN)) {
            Intent intent = new Intent(ACTION_WECHAT_SCAN_CHANGED);
            intent.putExtra(KEY_WECHAT_SCAN, sharedPreferences.getBoolean(KEY_WECHAT_SCAN, false));
            sendBroadcast(intent);
        }

        updateX2oolsPrefs();
    }

}
