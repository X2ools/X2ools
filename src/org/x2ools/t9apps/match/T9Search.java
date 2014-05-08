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
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import org.x2ools.R;

import java.util.ArrayList;
import java.util.Comparator;
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
            if(mPackageManager.getLaunchIntentForPackage(appinfo.packageName) == null) 
                continue;
            ApplicationItem appitem = new ApplicationItem();
            appitem.name = appinfo.loadLabel(mPackageManager).toString();
            appitem.pinyinNum = ToPinYinUtils.getPinyinNum(appitem.name);
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
        public String packageName;
        public Drawable drawable;
        int nameMatchId;
    }

    public T9SearchResult search(String number) {
        mNameResults.clear();
        number = removeNonDigits(number);
        int pos = 0;
        boolean newQuery = mPrevInput == null || number.length() <= mPrevInput.length();
        // Go through each contact
        for (ApplicationItem item : (newQuery ? mApps : mAllResults)) {
            item.nameMatchId = -1;
            pos = item.pinyinNum.indexOf(number);
            if (pos != -1) {
                int last_space = item.pinyinNum.lastIndexOf("0", pos);
                if (last_space == -1) {
                    last_space = 0;
                }
                item.nameMatchId = pos - last_space;
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

    public static class NameComparator implements Comparator<ApplicationItem> {
        @Override
        public int compare(ApplicationItem lhs, ApplicationItem rhs) {
            int ret = lhs.nameMatchId > rhs.nameMatchId ? lhs.nameMatchId : rhs.nameMatchId;
            if (ret == 0)
                ret = rhs.name.length() > lhs.name.length() ? rhs.name.length()
                        : lhs.name.length();
            if (ret == 0)
                ret = 0;
            return ret;
        }
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

    public static String removeNonDigits(String number) {
        int len = number.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char ch = number.charAt(i);
            if ((ch >= '0' && ch <= '9') || ch == '*' || ch == '#' || ch == '+') {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

}
