
package org.x2ools.t9apps;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.x2ools.R;

public class T9AppsView extends RelativeLayout {

    public static final boolean DEBUG = true;

    private static final String TAG = "T9AppsView";

    public T9AppsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    private Context mContext;

    private TextView mFilterView;

    private AppsGridView mAppsGridView;

    private StringBuilder mFilterText = new StringBuilder();

    OnLongClickListener mOnLongClickListener = new OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
            switch (v.getId()) {
                case R.id.button0:
                case R.id.button1:
                case R.id.button2:
                case R.id.button3:
                case R.id.button4:
                case R.id.button5:
                case R.id.button6:
                case R.id.button7:
                case R.id.button8:
                case R.id.button9:
                    int number = getNumberById(v.getId());
                    int index = number == 0 ? 10 : number - 1;
                    final boolean started = mAppsGridView.startAcivityByIndex(index);
                    if (started) {
                        hideView();
                    }
                    break;
                case R.id.buttonBack:
                    hideView();
                    break;
                case R.id.buttonDelete:
                    clearFilter();
            }
            return false;
        }

    };

    OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button0:
                case R.id.button1:
                case R.id.button2:
                case R.id.button3:
                case R.id.button4:
                case R.id.button5:
                case R.id.button6:
                case R.id.button7:
                case R.id.button8:
                case R.id.button9:
                    int number = getNumberById(view.getId());
                    mFilterText.append(number);
                    onTextChanged();
                    break;
                case R.id.buttonBack:
                    hideView();
                    break;
                case R.id.buttonDelete:
                    if (TextUtils.isEmpty(mFilterText))
                        break;
                    mFilterText.deleteCharAt(mFilterText.length() - 1);
                    onTextChanged();
            }
        }

    };

    public void clearFilter() {
        mFilterText = new StringBuilder();
        onTextChanged();
    }

    private void onTextChanged() {
        mAppsGridView.filter(mFilterText.toString());
        mFilterView.setText(mFilterText);
    }

    private int getNumberById(int id) {
        switch (id) {
            case R.id.button0:
                return 0;
            case R.id.button1:
                return 1;
            case R.id.button2:
                return 2;
            case R.id.button3:
                return 3;
            case R.id.button4:
                return 4;
            case R.id.button5:
                return 5;
            case R.id.button6:
                return 6;
            case R.id.button7:
                return 7;
            case R.id.button8:
                return 8;
            case R.id.button9:
                return 9;
            default:
                throw new RuntimeException("wrong number");
        }
    }

    @Override
    protected void onFinishInflate() {
        int[] buttons = new int[] {
                R.id.button1, R.id.button2, R.id.button3,

                R.id.button4, R.id.button5, R.id.button6,

                R.id.button7, R.id.button8, R.id.button9,

                R.id.buttonBack, R.id.button0, R.id.buttonDelete
        };

        for (int id : buttons) {
            findViewById(id).setOnClickListener(mOnClickListener);
        }
        for (int id : buttons) {
            findViewById(id).setOnLongClickListener(mOnLongClickListener);
        }
        setOnClickListener(mOnClickListener);
        mAppsGridView = (AppsGridView)findViewById(R.id.appsList);
        mFilterView = (TextView)findViewById(R.id.numFilter);

        super.onFinishInflate();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(TAG, "dispatchKeyEvent : " + KeyEvent.keyCodeToString(event.getKeyCode()));
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            hideView();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void hideView() {
        ((Activity)mContext).finish();
    }

    public void onMainViewShow() {
        mAppsGridView.setApplicationsData();
    }
}
