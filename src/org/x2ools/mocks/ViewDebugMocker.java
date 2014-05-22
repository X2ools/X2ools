
package org.x2ools.mocks;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;

import com.juuda.droidmock.mock.MockUtils;
import com.juuda.droidmock.mock.Mocker;

import org.x2ools.superdebug.SuperDebug;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ViewDebugMocker extends Mocker {
    public static final String TAG = "ViewDebugMocker";

    public ViewDebugMocker(Context context, Bundle extras) {
        super(context, extras);
    }

    @Override
    public void dump() {
        Log.d(TAG, "dumping ViewDebugMocker");
        // View v = SuperDebug.sStatusBarWindowView.get();
        View v = SuperDebug.sFocusedWindowView.get();
        if (v == null) {
            Log.d(TAG, "sStatusBarWindowViewX null");
            return;
        }
        final ArrayList<View> dumpViews = new ArrayList<View>();
        dumpViews.add(v);
        final boolean skip = (MockUtils.getInt(mExtras, "skip", 1) == 1);
        final boolean prop = (MockUtils.getInt(mExtras, "prop", 1) == 1);

        String id = mExtras.getString("id", null);

        if (id != null) {
            int viewId = mContext.getResources().getIdentifier(id, "id", mContext.getPackageName());
            dumpViews.clear();
            findViewsById(v, viewId, dumpViews);
        }

        String tag = mExtras.getString("tag", null);
        if (tag != null) {
            dumpViews.clear();
            findViewsByTag(v, tag, dumpViews);
        }

        String kclass = mExtras.getString("class", null);
        if (kclass != null) {
            dumpViews.clear();
            findViewsByClass(v, kclass, dumpViews);
        }

        new Thread() {
            @Override
            public void run() {
                for (View toDump : dumpViews) {
                    Log.e(TAG, "dumping view " + toDump);
                    try {
                        if (toDump instanceof ViewGroup) {
                            Method dumpView = ViewDebug.class.getDeclaredMethod(
                                    "dumpViewHierarchy", Context.class, ViewGroup.class,
                                    BufferedWriter.class, int.class, boolean.class, boolean.class);
                            dumpView.setAccessible(true);
                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                                    System.out, "utf-8"), 32 * 1024);
                            dumpView.invoke(ViewDebug.class, mContext, (ViewGroup) toDump, writer,
                                    0, skip, prop);
                            writer.flush();
                            writer.close();
                        } else {
                            Method dumpView = ViewDebug.class.getDeclaredMethod("dumpView",
                                    Context.class, View.class, BufferedWriter.class, int.class,
                                    boolean.class);
                            dumpView.setAccessible(true);
                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                                    System.out, "utf-8"), 32 * 1024);
                            dumpView.invoke(ViewDebug.class, mContext, toDump, writer, 0, prop);
                            writer.flush();
                            writer.close();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "", e);
                    }
                }
            }
        }.start();

    }

    public void findViewsByClass(View v, String className, List<View> result) {
        if (v.getClass().getName().contains(className)) {
            result.add(v);
        }
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                findViewsByClass(vg.getChildAt(i), className, result);
            }
        }
    }

    public void findViewsById(View v, int id, List<View> result) {
        if (v.getId() == id) {
            result.add(v);
        }
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                findViewsById(vg.getChildAt(i), id, result);
            }
        }
    }

    public static boolean equal(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    public void findViewsByTag(View v, Object tag, List<View> result) {
        if (equal(tag, v.getTag())) {
            result.add(v);
        }
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                findViewsByTag(vg.getChildAt(i), tag, result);
            }
        }
    }
}
