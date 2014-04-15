package org.x2ools;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class X2oolsActivity extends PreferenceActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }
    
}
