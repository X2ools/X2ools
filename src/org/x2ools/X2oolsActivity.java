package org.x2ools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class X2oolsActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	public static final String KEY_WECHAT_REMOVE_GAME = "wechat_remove_game";
	public static final String KEY_WECHAT_CHAT_FONT = "wechat_chat_font";
	public static final String KEY_WECHAT_SCAN = "wechat_scan";

	public static final String ACTION_WECHAT_SCAN_CHANGED = "x2ools.action.wechat.scan.changed";

	public static final String X2OOL_DIR = Environment
			.getExternalStorageDirectory() + "/X2ools/";
	public static final String X2OOL_PACKAGE_NAME = X2oolsActivity.class
			.getPackage().getName();

	private SharedPreferences prefs;

	private JSONObject json;
	private File jsonFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);
		prefs = getPreferenceScreen().getSharedPreferences();
		updateJsonData();
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

	private void updateJsonData() {
		json = new JSONObject();
		initFile();
		try {
			json.put(KEY_WECHAT_CHAT_FONT, URLEncoder.encode(
					prefs.getString(KEY_WECHAT_CHAT_FONT, ""), "UTF-8"));
			json.put(KEY_WECHAT_REMOVE_GAME,
					prefs.getBoolean(KEY_WECHAT_REMOVE_GAME, false));
			json.put(KEY_WECHAT_SCAN, prefs.getBoolean(KEY_WECHAT_SCAN, false));
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		try {
			FileOutputStream fos = new FileOutputStream(jsonFile);
			fos.write(json.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void initFile() {
		File x2oolsDir = new File(X2OOL_DIR);
		if (!x2oolsDir.exists()) {
			x2oolsDir.mkdir();
		}
		jsonFile = new File(x2oolsDir + "/prefs.json");
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(KEY_WECHAT_SCAN)) {
			Intent intent = new Intent(ACTION_WECHAT_SCAN_CHANGED);
			intent.putExtra(KEY_WECHAT_SCAN,
					sharedPreferences.getBoolean(KEY_WECHAT_SCAN, false));
			sendBroadcast(intent);
		}

		updateJsonData();
	}

}
