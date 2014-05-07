
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
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.x2ools.R;
import org.x2ools.t9apps.match.Matcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AppsGridView extends GridView {
    private ActivityManager mActivityManager;
    private PackageManager mPackageManager;
    private List<ApplicationInfo> mApplications;
    private LayoutInflater mLayoutInflater;
    private List<ApplicationInfo> mFilteredApplications;
    private AppsAdapter mAppsAdapter;
    private static final String TAG = "AppsGridView";
    private static final boolean DEBUG = true;
    private Context mContext;

    public AppsGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        mPackageManager = context.getPackageManager();
        mLayoutInflater = LayoutInflater.from(context);
        setApplicationsData();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        Log.d(TAG, "visibility changed to " + visibility);
        super.onVisibilityChanged(changedView, visibility);
    }

    public List<ApplicationInfo> setApplicationsData() {
        PackageManager pm = mPackageManager;
        ActivityManager am = mActivityManager;
        List<RecentTaskInfo> recentTasks = am.getRecentTasks(30,
                ActivityManager.RECENT_IGNORE_UNAVAILABLE | ActivityManager.RECENT_WITH_EXCLUDED);
        List<ApplicationInfo> allapplications = new ArrayList<ApplicationInfo>();
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
                    ApplicationInfo info = pm.getApplicationInfo(recentInfo.baseIntent
                            .getComponent().getPackageName(), 0);
                    allapplications.add(info);
                } catch (NameNotFoundException e) {
                    Log.e(TAG, "cannot find package", e);
                }
            }
        }

        allapplications.addAll(pm.getInstalledApplications(0));
        Iterator<ApplicationInfo> iterator = allapplications.iterator();
        Set<String> addedPackages = new HashSet<String>();
        // remove duplicate and not launchable package
        while (iterator.hasNext()) {
            ApplicationInfo info = iterator.next();
            if (pm.getLaunchIntentForPackage(info.packageName) == null
                    || addedPackages.contains(info.packageName)) {
                iterator.remove();
            }
            else {
                addedPackages.add(info.packageName);
            }
        }
        mApplications = allapplications;
        mFilteredApplications = mApplications;
        mAppsAdapter = new AppsAdapter();
        setAdapter(mAppsAdapter);
        mAppsAdapter.notifyDataSetChanged();
        return allapplications;
    }
    
    private Comparator<ApplicationInfo> comparator = new Comparator<ApplicationInfo>() {

        @Override
        public int compare(ApplicationInfo lhs, ApplicationInfo rhs) {
            CharSequence llabel = lhs.loadLabel(mPackageManager);
            CharSequence rlabel = rhs.loadLabel(mPackageManager);
            
            if(llabel.length() > rlabel.length()) {
                return 1;
            } else if(llabel.length() < rlabel.length()) {
                return -1;
            } else {
                return 0;
            }
        }
    };

    public void filter(String filter) {
        mAppsAdapter.getFilter().filter(filter);
    }

    static class ViewHolder {
        TextView textTitle;
        ImageView icon;
    }

    class AppsAdapter extends BaseAdapter implements Filterable {

        class NameFilter extends Filter {

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                List<ApplicationInfo> filteredApplications = new ArrayList<ApplicationInfo>();
                for (Iterator<ApplicationInfo> iterator = mApplications
                        .iterator(); iterator.hasNext();) {
                    ApplicationInfo info = iterator.next();
                    CharSequence label = info.loadLabel(mPackageManager);
                    Log.d(TAG, "---> name=" + label + "charSequence : "
                            + charSequence);
                    if (Matcher
                            .match(label.toString(), charSequence.toString())) {
                        filteredApplications.add(info);
                        Log.d(TAG, "add " + label);
                    }
                }
                Collections.sort(filteredApplications, comparator);
                filterResults.values = filteredApplications;
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence,
                    FilterResults results) {
                mFilteredApplications = (List<ApplicationInfo>) results.values;
                if(TextUtils.isEmpty(charSequence)) {
                    mFilteredApplications = mApplications;
                }
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        }

        @Override
        public int getCount() {
            return mFilteredApplications.size();
        }

        @Override
        public Object getItem(int position) {
            return mFilteredApplications.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            final ApplicationInfo info = (ApplicationInfo) getItem(position);
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
                    getContext().startActivity(
                            mPackageManager.getLaunchIntentForPackage(
                                    info.packageName).addFlags(
                                    Intent.FLAG_ACTIVITY_NEW_TASK));
                    ((Activity)getContext()).finish();

                }

            });

            convertView.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View arg0) {
                    Log.d(TAG, "onLongClick ");
                    Intent i = new Intent();
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    i.setData(Uri.parse("package:" + info.packageName));
                    getContext().startActivity(i);
                    ((Activity)getContext()).finish();
                    return true;
                }

            });
            viewHolder.textTitle.setText(info.loadLabel(mPackageManager));
            viewHolder.icon.setImageDrawable(info.loadIcon(mPackageManager));
            return convertView;
        }

        public android.widget.Filter getFilter() {
            return new NameFilter();
        }
    }

    public boolean startAcivityByIndex(int index) {
        if (DEBUG) {
            dumpApplications();
        }
        if (index < mFilteredApplications.size()) {
            ApplicationInfo info = mFilteredApplications.get(index);
            Intent i = mPackageManager.getLaunchIntentForPackage(info.packageName);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(i);
            Log.d(TAG, "start " + info.packageName);
            return true;
        }
        return false;
    }

    public void dumpApplications() {
        for (ApplicationInfo info : mFilteredApplications) {
            Log.d(TAG, "info.packageName " + info.packageName);
        }
    }
}
