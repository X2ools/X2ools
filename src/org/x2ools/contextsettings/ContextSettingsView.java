package org.x2ools.contextsettings;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.x2ools.R;

import java.lang.ref.WeakReference;

public class ContextSettingsView extends FrameLayout{
    private static final String TAG = "ContextSettingsView";
    private TextView mTextCurrentPackage;
    private Button mButtonSettings;
    private View mX2ools;
    private View mLayoutSmall;
    private View mLayoutBig;
    private Context mContext;
    private CallBack mCallBack;
    public static final int DELAY_MILLS = 1000;
    private ActivityManager mAm;
    private PackageManager mPm;
    
    public ContextSettingsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = getContext();
        mAm = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
        mPm = mContext.getPackageManager();
    }

    @Override
    protected void onFinishInflate() {
        mTextCurrentPackage = (TextView)findViewById(R.id.currentPackage);
        mButtonSettings = (Button)findViewById(R.id.gotoSettings);
        mLayoutSmall = findViewById(R.id.layoutSmall);
        mLayoutBig = findViewById(R.id.layoutBig);
        mX2ools = findViewById(R.id.x2ools);
        super.onFinishInflate();
    }

    private boolean mMoved;
    private float mStartX;
    private float mStartY;
    private float mEndX;
    private float mEndY;
    
    public void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        final int action = motionEvent.getAction();
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                mMoved = false;
                mStartX = motionEvent.getX();
                mStartY = motionEvent.getY();
                Log.d(TAG, "mStartX : "+ mStartX + "  mStartY :" + mStartY + "  moved: " + mMoved);
                break;
            case MotionEvent.ACTION_MOVE:
                mEndX = motionEvent.getX();
                mEndY = motionEvent.getY();
                mMoved = true;
                Log.d(TAG, "moved mEndX : "+ mEndX + "  mEndY :" + mEndY +  "  moved: " + mMoved);
                mCallBack.onMoved((int)(mEndX - mStartX), (int)(mEndY - mStartY));
                break;
            case MotionEvent.ACTION_UP:
                if(mMoved) {
                    mEndX = motionEvent.getX();
                    mEndY = motionEvent.getY();
                    Log.d(TAG, "mEndX : "+ mEndX + "  mEndY :" + mEndY);
                    mCallBack.onMoved((int)(mEndX - mStartX), (int)(mEndY - mStartY));
                }
                else {
                    Log.d(TAG, "not Moved");
                    if(mLayoutSmall.getVisibility() == View.VISIBLE) {
                        mLayoutSmall.setVisibility(View.GONE);
                        mLayoutBig.setVisibility(View.VISIBLE);
                        mCallBack.onModeChanged(true);
                    }
                    else {
                        mLayoutSmall.setVisibility(View.VISIBLE);
                        mLayoutBig.setVisibility(View.GONE);
                        mCallBack.onModeChanged(false);
                    }
                }
                break;
        }
        super.onTouchEvent(motionEvent);
        return true;
    }
    
    public void updateView() {
        final String currentPackage = getRunningPackage().toString();
        final String applicationName = getApplicationName(currentPackage);

        mTextCurrentPackage.setText(currentPackage + "\n" + applicationName);        
        mButtonSettings.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                i.setData(Uri.parse("package:"+ currentPackage));
                mContext.startActivity(i);
            }
        });
        
        mX2ools.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = mPm.getLaunchIntentForPackage("org.x2ools");
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(i);
            }
        });
        
        sHandler.removeMessages(0);
        sHandler.sendEmptyMessageDelayed(0, DELAY_MILLS);
    }
    
    private CharSequence getRunningPackage() {
        return  mAm.getRunningTasks(1).get(0).topActivity.getPackageName();
    }
    
    private String getApplicationName(final String packageName) {
        try {
            return (String)mPm.getApplicationLabel(mPm.getApplicationInfo(packageName, 0));
        }
        catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    protected void onAttachedToWindow() {
        sHandler.setView(this);
        updateView();
        super.onAttachedToWindow();
    }

    static UpdateHandler sHandler = new UpdateHandler();

    static class UpdateHandler extends Handler {
        private WeakReference<ContextSettingsView> mView;
        
        public void setView(ContextSettingsView view) {
            mView = new WeakReference<ContextSettingsView>(view);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mView.get().updateView();
        }
    } 
    
    public static interface CallBack {
        public void onMoved(int dx, int dy);
        public void onModeChanged(boolean expand);
    }
}
