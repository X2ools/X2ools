
package org.x2ools.t9apps;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.x2ools.R;
import org.x2ools.t9apps.match.T9Search;
import org.x2ools.t9apps.match.T9Search.ApplicationItem;
import org.x2ools.t9apps.match.T9Search.T9SearchResult;

import java.util.ArrayList;
import java.util.List;

public class AppsGridView extends GridView {
    private AppsAdapter mAppsAdapter;
    private static final String TAG = "AppsGridView";
    private static final boolean DEBUG = false;
    protected static final int MSG_SEARCH_INITED = 0;
    private Context mContext;
    private static T9Search sT9Search;
    private ArrayList<ApplicationItem> apps;
    private PackageManager mPackageManager;
    private ActivityManager mActivityManager;
    private LayoutInflater mLayoutInflater;
    private String mFilterStr = null;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SEARCH_INITED:
                    if (!TextUtils.isEmpty(mFilterStr)) {
                        filter(mFilterStr);
                    }
                    break;

                default:
                    break;
            }
        }
    };

    public AppsGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mPackageManager = context.getPackageManager();
        mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        mLayoutInflater = LayoutInflater.from(context);
        // sT9Search = new T9Search(context);
        setApplicationsData();
        new Thread(new Runnable() {

            @Override
            public void run() {
                sT9Search = new T9Search(mContext);
                mHandler.sendEmptyMessage(MSG_SEARCH_INITED);
            }
        }).start();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        Log.d(TAG, "visibility changed to " + visibility);
        super.onVisibilityChanged(changedView, visibility);
    }

    public void setApplicationsData() {
        apps = getRecentApps();
        mAppsAdapter = new AppsAdapter(apps);
        setAdapter(mAppsAdapter);
        mAppsAdapter.notifyDataSetChanged();
    }

    public boolean startAcivityByIndex(int index) {
        if (DEBUG) {
            dumpApplications();
        }
        if (index < apps.size()) {
            ApplicationItem item = apps.get(index);
            Intent i = mPackageManager.getLaunchIntentForPackage(item.packageName);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(i);
            Log.d(TAG, "start " + item.packageName);
            return true;
        }
        return false;
    }

    public void dumpApplications() {
        for (ApplicationItem item : apps) {
            Log.d(TAG, "info.packageName " + item.packageName);
        }
    }

    public void filter(String string) {
        mFilterStr = string;
        if (sT9Search == null)
            return;
        if (TextUtils.isEmpty(string)) {
            apps = getRecentApps();
            mAppsAdapter = new AppsAdapter(apps);
            setAdapter(mAppsAdapter);
            mAppsAdapter.notifyDataSetChanged();
            return;
        }
        T9SearchResult result = sT9Search.search(string);
        if (result != null) {
            apps = sT9Search.search(string).getResults();
            mAppsAdapter = new AppsAdapter(apps);
            setAdapter(mAppsAdapter);
            mAppsAdapter.notifyDataSetChanged();
        }
    }

    public ArrayList<ApplicationItem> getRecentApps() {
        List<RecentTaskInfo> recentTasks = mActivityManager.getRecentTasks(9,
                ActivityManager.RECENT_IGNORE_UNAVAILABLE | ActivityManager.RECENT_WITH_EXCLUDED);
        ArrayList<ApplicationItem> recents = new ArrayList<ApplicationItem>();
        if (DEBUG) {
            Log.d(TAG, "recentTasks:  " + recentTasks);
        }
        if (recentTasks != null) {
            for (RecentTaskInfo recentInfo : recentTasks) {
                try {
                    if (DEBUG) {
                        Log.d(TAG, "recentInfo.baseIntent:  "
                                + recentInfo.baseIntent.getComponent().getPackageName());

                    }
                    ApplicationInfo info = mPackageManager.getApplicationInfo(recentInfo.baseIntent
                            .getComponent().getPackageName(), 0);
                    if (mPackageManager.getLaunchIntentForPackage(info.packageName) == null)
                        continue;
                    boolean added = false;
                    for (ApplicationItem tmp : recents) {
                        if (tmp.packageName.equals(info.packageName))
                            added = true;
                    }
                    if (!added) {
                        ApplicationItem item = new ApplicationItem();
                        item.name = info.loadLabel(mPackageManager).toString();
                        item.packageName = info.packageName;
                        item.drawable = info.loadIcon(mPackageManager);
                        recents.add(item);
                    }
                } catch (NameNotFoundException e) {
                    Log.e(TAG, "cannot find package", e);
                }
            }
        }

        return recents;
    }

    public class AppsAdapter extends BaseAdapter {

        private ArrayList<ApplicationItem> mAppItems;

        public AppsAdapter(ArrayList<ApplicationItem> apps) {
            mAppItems = apps;
        }

        @Override
        public int getCount() {
            return mAppItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mAppItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            final ApplicationItem item = (ApplicationItem) getItem(position);
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.package_item,
                        null);
                viewHolder = new ViewHolder();

                viewHolder.textTitle = (TextView) convertView
                        .findViewById(R.id.textTitle);
                viewHolder.icon = (ImageView) convertView
                        .findViewById(R.id.icon);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    mContext.startActivity(
                            mPackageManager.getLaunchIntentForPackage(
                                    item.packageName).addFlags(
                                    Intent.FLAG_ACTIVITY_NEW_TASK));
                    ((Activity) mContext).finish();

                }

            });

            convertView.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View arg0) {
                    Log.d(TAG, "onLongClick ");
                    Intent i = new Intent();
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    i.setData(Uri.parse("package:" + item.packageName));
                    mContext.startActivity(i);
                    ((Activity) mContext).finish();
                    return true;
                }

            });
            viewHolder.textTitle.setText(item.name);
            viewHolder.icon.setImageDrawable(item.drawable);
            return convertView;
        }
    }

    static class ViewHolder {
        TextView textTitle;
        ImageView icon;
    }
}
