
package org.x2ools;

import java.io.File;

import org.x2ools.contextsettings.ContextSettingsService;
import org.x2ools.system.XPhoneStatusBar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

public class X2oolsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener,
        OnPreferenceClickListener {

    // public static final String KEY_WECHAT_REMOVE_GAME = "wechat_remove_game";
    public static final String KEY_WECHAT_CHAT_FONT = "wechat_chat_font";

    public static final String KEY_WECHAT_SCAN = "wechat_scan";

    public static final String KEY_CONTEXT_SETTINGS = "enable_context_settings";

    public static final String KEY_STATUS_COLOR = "status_color";

    public static final String KEY_T9_SEARCH = "t9_search";

    public static final String KEY_PERMISSION_ALLOW = "permission_allow";

    public static final String ACTION_WECHAT_SCAN_CHANGED = "x2ools.action.wechat.scan.changed";

    public static final String X2OOL_PACKAGE_NAME = X2oolsActivity.class.getPackage().getName();

    private static final String TAG = "X2oolsActivity";

    private SharedPreferences prefs;

    private X2oolsSharedPreferences x2ools_prefs;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        prefs = getPreferenceScreen().getSharedPreferences();
        getPreferenceScreen().setOnPreferenceClickListener(this);
        initX2oolsPrefs();
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
        // editor.putBoolean(KEY_WECHAT_REMOVE_GAME,
        // prefs.getBoolean(KEY_WECHAT_REMOVE_GAME, false));
        editor.putBoolean(KEY_WECHAT_SCAN, prefs.getBoolean(KEY_WECHAT_SCAN, false));
        editor.putBoolean(KEY_CONTEXT_SETTINGS, prefs.getBoolean(KEY_CONTEXT_SETTINGS, true));
        editor.putInt(KEY_STATUS_COLOR, prefs.getInt(KEY_STATUS_COLOR, Color.TRANSPARENT));
        editor.putBoolean(KEY_PERMISSION_ALLOW, prefs.getBoolean(KEY_PERMISSION_ALLOW, true));
        editor.putBoolean(KEY_T9_SEARCH, prefs.getBoolean(KEY_T9_SEARCH, true));
        editor.commit();
    }

    private void initX2oolsPrefs() {
        boolean isFirstRun = prefs.getBoolean("isFirstRun", true);
        if (isFirstRun || !new File(X2oolsApplication.X2OOLS_PREFS).exists()) {
            prefs.edit().putBoolean("isFirstRun", false).commit();
            updateX2oolsPrefs();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_WECHAT_SCAN)) {
            Intent intent = new Intent(ACTION_WECHAT_SCAN_CHANGED);
            intent.putExtra(KEY_WECHAT_SCAN, sharedPreferences.getBoolean(KEY_WECHAT_SCAN, false));
            sendBroadcast(intent);
        } else if (key.equals(KEY_CONTEXT_SETTINGS)) {
            boolean enable = sharedPreferences.getBoolean(KEY_CONTEXT_SETTINGS, true);
            Intent intent = new Intent(ContextSettingsService.ACTION_CONTEXT_SETTINGS);
            intent.putExtra(ContextSettingsService.KEY_ENABLE, enable);
            sendBroadcast(intent);
            Log.d(TAG, "KEY_CONTEXT_SETTINGS changed to  " + enable);
        } else if (key.equals(KEY_STATUS_COLOR)) {
            int color = sharedPreferences.getInt(KEY_STATUS_COLOR, Color.TRANSPARENT);
            if (color == Color.TRANSPARENT) {
                color = getResources().getColor(R.color.default_dark_actionbar);
            }
            Intent statusbarIntent = new Intent(XPhoneStatusBar.ACTION_CHANGE_STATUS_BAR);
            statusbarIntent.putExtra("statusBarColor", color);
            sendBroadcast(statusbarIntent);
        } else if (key.equals(KEY_PERMISSION_ALLOW)) {
            Toast.makeText(this, R.string.permission_work_after_reboot, Toast.LENGTH_LONG).show();
            ;
        }
        updateX2oolsPrefs();
    }

    @Override
    public boolean onPreferenceClick(Preference arg0) {
        return false;
    }

}
