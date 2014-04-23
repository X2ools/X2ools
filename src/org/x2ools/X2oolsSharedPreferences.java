package org.x2ools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;

/**
 * This class is basically the same as SharedPreferencesImpl from AOSP. You can
 * read and write by any app, but maybe slowly and without listeners support.
 */
public class X2oolsSharedPreferences implements SharedPreferences {

    private File mFile;
    private boolean mLoaded;
    private boolean mSaved;
    private JSONObject json;

    public X2oolsSharedPreferences() {
        mFile = new File(Environment.getExternalStorageDirectory(), "X2ools/" + "prefs.json");
        startLoadFromDisk();
    }

    private void startLoadFromDisk() {
        synchronized (this) {
            mLoaded = false;
        }
        new Thread("XSharedPreferences-load") {
            @Override
            public void run() {
                synchronized (X2oolsSharedPreferences.this) {
                    loadFromDiskLocked();
                }
            }
        }.start();
    }

    private void loadFromDiskLocked() {
        if (mLoaded) {
            return;
        }

        try {
            File jsonFile = new File(Environment.getExternalStorageDirectory() + "/X2ools/prefs.json");
            InputStreamReader isr = new InputStreamReader(new FileInputStream(jsonFile));
            BufferedReader bufferedReader = new BufferedReader(isr);
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((receiveString = bufferedReader.readLine()) != null) {
                stringBuilder.append(receiveString);
            }

            if (!TextUtils.isEmpty(stringBuilder))
                json = new JSONObject(stringBuilder.toString());

            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mLoaded = true;
        notifyAll();
    }

    private void awaitLoadedLocked() {
        while (!mLoaded) {
            try {
                wait();
            } catch (InterruptedException unused) {
            }
        }
    }

    private void startSaveToDisk() {
        synchronized (this) {
            mSaved = false;
        }
        new Thread("XSharedPreferences-save") {
            @Override
            public void run() {
                synchronized (X2oolsSharedPreferences.this) {
                    saveToDiskLocked();
                }
            }
        }.start();
    }

    private void saveToDiskLocked() {
        if (mSaved) {
            return;
        }

        try {
            FileOutputStream fos = new FileOutputStream(mFile);
            fos.write(json.toString().getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mSaved = true;
        notifyAll();
    }

    @Override
    public boolean contains(String key) {
        synchronized (this) {
            awaitLoadedLocked();
            return json.has(key);
        }
    }

    @Override
    public Editor edit() {
        synchronized (this) {
            awaitLoadedLocked();
        }

        return new X2oolsEditor();
    }

    @Override
    public Map<String, ?> getAll() {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        synchronized (this) {
            awaitLoadedLocked();
            try {
                return json.getBoolean(key);
            } catch (JSONException e) {
                return defValue;
            }
        }
    }

    @Override
    public float getFloat(String key, float defValue) {
        synchronized (this) {
            awaitLoadedLocked();
            try {
                return (float) json.getDouble(key);
            } catch (JSONException e) {
                return defValue;
            }
        }
    }

    @Override
    public int getInt(String key, int defValue) {
        synchronized (this) {
            awaitLoadedLocked();
            try {
                return json.getInt(key);
            } catch (JSONException e) {
                return defValue;
            }
        }
    }

    @Override
    public long getLong(String key, long defValue) {
        synchronized (this) {
            awaitLoadedLocked();
            try {
                return json.getLong(key);
            } catch (JSONException e) {
                return defValue;
            }
        }
    }

    @Override
    public String getString(String key, String defValue) {
        synchronized (this) {
            awaitLoadedLocked();
            try {
                return json.getString(key);
            } catch (JSONException e) {
                return defValue;
            }
        }
    }

    @Override
    public Set<String> getStringSet(String arg0, Set<String> arg1) {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        throw new UnsupportedOperationException("not supported");
    }

    public final class X2oolsEditor implements Editor {

        @Override
        public void apply() {
            startSaveToDisk();
        }

        @Override
        public Editor clear() {
            synchronized (this) {
                json = new JSONObject();
                return this;
            }
        }

        @Override
        public boolean commit() {
            startSaveToDisk();
            return true;
        }

        @Override
        public Editor putBoolean(String key, boolean value) {
            synchronized (this) {
                try {
                    json.put(key, value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return this;
            }
        }

        @Override
        public Editor putFloat(String key, float value) {
            synchronized (this) {
                try {
                    json.put(key, value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return this;
            }
        }

        @Override
        public Editor putInt(String key, int value) {
            synchronized (this) {
                try {
                    json.put(key, value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return this;
            }
        }

        @Override
        public Editor putLong(String key, long value) {
            synchronized (this) {
                try {
                    json.put(key, value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return this;
            }
        }

        @Override
        public Editor putString(String key, String value) {
            synchronized (this) {
                try {
                    json.put(key, value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return this;
            }
        }

        @Override
        public Editor putStringSet(String arg0, Set<String> arg1) {
            throw new UnsupportedOperationException("not supported");
        }

        @Override
        public Editor remove(String key) {
            synchronized (this) {
                json.remove(key);
                return this;
            }
        }

    }

}
