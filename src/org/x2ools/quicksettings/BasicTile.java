
package org.x2ools.quicksettings;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.x2ools.R;
import org.x2ools.quicksettings.QuickSettings.TileLayout;

// Abstract Basic Tile definition
// Tile layout should consist of 2 View elements: ImageView and TextView
public abstract class BasicTile extends AQuickSettingsTile {

    protected ImageView mImageView;

    protected TextView mTextView;

    protected int mDrawableId;

    protected String mLabel;

    public BasicTile(Context context, Context gbContext, Object statusBar, Object panelBar) {
        super(context, gbContext, statusBar, panelBar);
    }

    // each basic tile clone must provide its own layout identified by unique ID
    protected abstract int onGetLayoutId();

    // basic tile clone can override this to supply custom ImageView ID
    protected int onGetImageViewId() {
        return R.id.image;
    }

    // basic tile clone can override this to supply custom TextView ID
    protected int onGetTextViewId() {
        return R.id.text;
    }

    @Override
    protected void onTileCreate() {
        LayoutInflater inflater = LayoutInflater.from(mGbContext);
        inflater.inflate(onGetLayoutId(), mTile);
        mImageView = (ImageView)mTile.findViewById(onGetImageViewId());
        mTextView = (TextView)mTile.findViewById(onGetTextViewId());
    }

    @Override
    protected void updateTile() {
        mTextView.setText(mLabel);
        mImageView.setImageResource(mDrawableId);
    }

    @Override
    protected void onLayoutUpdated(TileLayout tileLayout) {
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, tileLayout.textSize);
        mTextView.setAllCaps(tileLayout.labelStyle == TileLayout.LabelStyle.ALLCAPS);
        mTextView.setVisibility(tileLayout.labelStyle == TileLayout.LabelStyle.HIDDEN ? View.GONE
                : View.VISIBLE);
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams)mImageView
                .getLayoutParams();
        lp.width = lp.height = tileLayout.imageSize;
        lp.topMargin = tileLayout.imageMarginTop;
        lp.bottomMargin = tileLayout.imageMarginBottom;
        mImageView.setLayoutParams(lp);
        mImageView.requestLayout();
    }
}
