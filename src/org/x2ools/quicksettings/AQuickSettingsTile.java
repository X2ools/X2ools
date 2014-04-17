package org.x2ools.quicksettings;

import org.x2ools.quicksettings.QuickSettings.TileLayout;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public abstract class AQuickSettingsTile implements OnClickListener {
	protected static final String PACKAGE_NAME = "com.android.systemui";

	protected Context mContext;
	protected Context mGbContext;
	protected FrameLayout mTile;
	protected OnClickListener mOnClick;
	protected OnLongClickListener mOnLongClick;
	protected Resources mResources;
	protected Resources mGbResources;
	protected Object mStatusBar;
	protected Object mPanelBar;
	protected Object mQuickSettings;
	protected ViewGroup mContainer;

	public AQuickSettingsTile(Context context, Context gbContext,
			Object statusBar, Object panelBar) {
		mContext = context;
		mGbContext = gbContext;
		mResources = mContext.getResources();
		mGbResources = mGbContext.getResources();
		mStatusBar = statusBar;
		mPanelBar = panelBar;
	}

	public void setupQuickSettingsTile(ViewGroup viewGroup,
			LayoutInflater inflater, Object quickSettings) {
		mContainer = viewGroup;
		mQuickSettings = quickSettings;
		int layoutId = mResources.getIdentifier("quick_settings_tile",
				"layout", PACKAGE_NAME);
		mTile = (FrameLayout) inflater.inflate(layoutId, viewGroup, false);
		onTileCreate();
		viewGroup.addView(mTile);
		updateResources();
		mTile.setOnClickListener(this);
		mTile.setOnLongClickListener(mOnLongClick);
		onTilePostCreate();
	}
	
	public void removeFromContainer(ViewGroup mContainerView) {
		Resources res = mContainerView.getResources();
		int orientation = res.getConfiguration().orientation;

		int mNumColumns = XposedHelpers.getIntField(mContainerView,
				"mNumColumns");

		TileLayout tl = new TileLayout(mContainerView.getContext(),
				mNumColumns, orientation, TileLayout.LabelStyle.ALLCAPS);
		
		mContainerView.removeView(mTile);
		updateLayout(tl);
		((FrameLayout) mContainerView).requestLayout();
	}
	
	public void reAddFromContainer(ViewGroup mContainerView) {
		Resources res = mContainerView.getResources();
		int orientation = res.getConfiguration().orientation;

		int mNumColumns = XposedHelpers.getIntField(mContainerView,
				"mNumColumns");

		TileLayout tl = new TileLayout(mContainerView.getContext(),
				mNumColumns, orientation, TileLayout.LabelStyle.ALLCAPS);
		
		mContainerView.addView(mTile);
		updateLayout(tl);
		((FrameLayout) mContainerView).requestLayout();
	}

	public void updateLayout(TileLayout tileLayout) {
		if (mTile != null) {
			onLayoutUpdated(tileLayout);
		}
	}

	protected abstract void onTileCreate();

	protected void onTilePostCreate() {
	};

	protected abstract void onLayoutUpdated(TileLayout tileLayout);

	protected abstract void updateTile();

	public void updateResources() {
		if (mTile != null) {
			updateTile();
		}
	}

	@Override
	public void onClick(View v) {
		if (mOnClick != null) {
			mOnClick.onClick(v);
		}
	}

	protected void startActivity(String action) {
		Intent intent = new Intent(action);
		startActivity(intent);
	}

	protected void startActivity(Intent intent) {
		try {
			XposedHelpers.callMethod(mQuickSettings, "startSettingsActivity",
					intent);
		} catch (Throwable t) {
			// fallback in case of troubles
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TOP);
			mContext.startActivity(intent);
			collapsePanels();
		}
	}

	protected void collapsePanels() {
		try {
			XposedHelpers.callMethod(mStatusBar, "animateCollapsePanels");
		} catch (Throwable t) {
			XposedBridge.log("Error calling animateCollapsePanels: "
					+ t.getMessage());
		}
	}
}
