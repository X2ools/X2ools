/*
 * Copyright (C) 2011 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.x2ools.t9apps.match;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import org.x2ools.R;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author shade, Danesh, pawitp
 */
public class T9Search {

    // List sort modes
    private static final boolean DEBUG = false;

    private static final String TAG = "T9Search";

    // Local variables
    private Context mContext;

    private ArrayList<ApplicationItem> mNameResults = new ArrayList<ApplicationItem>();

    private Set<ApplicationItem> mAllResults = new LinkedHashSet<ApplicationItem>();

    private ArrayList<ApplicationItem> mApps = new ArrayList<ApplicationItem>();

    private String mPrevInput;

    private PackageManager mPackageManager;

    private List<ApplicationInfo> mApplications;

    private static char[][] sT9Map;

    public T9Search(Context context) {
        mContext = context;
        mPackageManager = context.getPackageManager();
        getAll();
    }

    private void getAll() {
        if (sT9Map == null)
            initT9Map();

        mApplications = new ArrayList<ApplicationInfo>();

        mApplications.addAll(mPackageManager.getInstalledApplications(0));
        for (ApplicationInfo appinfo : mApplications) {
            if (mPackageManager.getLaunchIntentForPackage(appinfo.packageName) == null)
                continue;
            ApplicationItem appitem = new ApplicationItem();
            appitem.name = appinfo.loadLabel(mPackageManager).toString();
            appitem.pinyinNum = ToPinYinUtils.getPinyinNum(appitem.name, false);
            appitem.fullpinyinNum = ToPinYinUtils.getPinyinNum(appitem.name, true);
            appitem.packageName = appinfo.packageName;
            appitem.drawable = appinfo.loadIcon(mPackageManager);
            mApps.add(appitem);
        }
    }

    public static class T9SearchResult {

        private final ArrayList<ApplicationItem> mResults;

        public T9SearchResult(final ArrayList<ApplicationItem> results, final Context mContext) {
            mResults = results;
        }

        public int getNumResults() {
            return mResults.size();
        }

        public ArrayList<ApplicationItem> getResults() {
            return mResults;
        }
    }

    public static class ApplicationItem {
        public String name;

        public String pinyinNum;

        public String fullpinyinNum;

        public String packageName;

        public int taskId;

        public Intent baseIntent;

        public Drawable drawable;
    }

    public T9SearchResult search(String number) {
        mNameResults.clear();
        int pos = 0;
        boolean newQuery = mPrevInput == null || number.length() <= mPrevInput.length();
        // Go through each contact
        for (ApplicationItem item : (newQuery ? mApps : mAllResults)) {
            pos = item.pinyinNum.indexOf(number);
            if (pos != -1) {
                mNameResults.add(item);
            }

            pos = item.fullpinyinNum.indexOf(number);
            if (pos != -1) {
                mNameResults.add(item);
            }
        }
        mAllResults.clear();
        mPrevInput = number;
        if (mNameResults.size() > 0) {
            mAllResults.addAll(mNameResults);
            return new T9SearchResult(new ArrayList<ApplicationItem>(mAllResults), mContext);
        }
        return null;
    }

    private void initT9Map() {
        String[] t9Array = mContext.getResources().getStringArray(R.array.t9_map);
        sT9Map = new char[t9Array.length][];
        int rc = 0;
        for (String item : t9Array) {
            int cc = 0;
            sT9Map[rc] = new char[item.length()];
            for (char ch : item.toCharArray()) {
                sT9Map[rc][cc] = ch;
                cc++;
            }
            rc++;
        }
    }

}
