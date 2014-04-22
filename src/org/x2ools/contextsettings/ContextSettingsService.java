package org.x2ools.contextsettings;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.x2ools.R;

import java.lang.ref.WeakReference;
import java.util.List;

public class ContextSettingsService extends Service {
    private static final String TAG = "ContextSettingsService";
    private TextView mTextCurrentPackage;
    private Button mButtonSettings;
    private View mLayoutSmall;
    private View mLayoutBig;
    private View mParentView;

    private ActivityManager mAm;
    private WindowManager mWindowManager; 
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    private LayoutParams mLayoutParams;
    @Override
    public void onCreate() {
        mAm = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        View v = getView();
        Log.d(TAG, "width : " + v.getWidth() + "x height: " + v.getHeight());
        
        mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new LayoutParams();
        mLayoutParams.y = 40;
        mLayoutParams.x = 500;
        mLayoutParams.width = LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = LayoutParams.WRAP_CONTENT;
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        
        mLayoutParams.flags = 
                   LayoutParams.FLAG_NOT_TOUCH_MODAL;
        mLayoutParams.format = PixelFormat.TRANSLUCENT;
        mWindowManager.addView(v, mLayoutParams);
        sHandler.setService(this);
        super.onCreate();
    }

    public View getView() {
        mParentView = LayoutInflater.from(this).inflate(R.layout.context_settings, null);
        mTextCurrentPackage = (TextView)mParentView.findViewById(R.id.currentPackage);
        mButtonSettings = (Button)mParentView.findViewById(R.id.gotoSettings);
        mLayoutSmall = mParentView.findViewById(R.id.layoutSmall);
        mLayoutBig = mParentView.findViewById(R.id.layoutBig);
        updateView();
        mParentView.setOnTouchListener(new OnTouchListener() {
            private boolean mMoved;
            private float mStartX;
            private float mStartY;
            private float mEndX;
            private float mEndY;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
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
                        updateViewPosition(mEndX - mStartX, mEndY - mStartY);
                        break;
                    case MotionEvent.ACTION_UP:
                        if(mMoved) {
                            mEndX = motionEvent.getX();
                            mEndY = motionEvent.getY();
                            Log.d(TAG, "mEndX : "+ mEndX + "  mEndY :" + mEndY);
                            updateViewPosition(mEndX - mStartX, mEndY - mStartY);
                        }
                        else {
                            Log.d(TAG, "not Moved" + view);
                            if(mLayoutSmall.getVisibility() == View.VISIBLE) {
                                mLayoutSmall.setVisibility(View.GONE);
                                mLayoutBig.setVisibility(View.VISIBLE);
                            }
                            else {
                                mLayoutSmall.setVisibility(View.VISIBLE);
                                mLayoutBig.setVisibility(View.GONE);
                            }
                        }
                        break;
                }
                return true;
            }
            
            
        });
        return mParentView;
    }

    private void updateViewPosition(float dx, float dy) {
        mLayoutParams.x = mLayoutParams.x + (int)dx;
        mLayoutParams.y = mLayoutParams.y + (int)dy;
        Log.d(TAG, "updateViewLayout : "+ mLayoutParams.x + " , " + mLayoutParams.y);
        mWindowManager.updateViewLayout(mParentView, mLayoutParams);
    }

    private void updateView() {
        final String currentPackage = getRunningPackage();
        
        mTextCurrentPackage.setText(currentPackage);
        
        mButtonSettings.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                i.setData(Uri.parse("package:"+ currentPackage));
                startActivity(i);
            }
        });
        sHandler.removeMessages(0);
        sHandler.sendEmptyMessageDelayed(0, DELAY_MILLS);
    }
    
    private String getRunningPackage() {
        List<RunningTaskInfo> list = mAm.getRunningTasks(1);
        if(list == null) {
            return "cao";
        }
        else {
            return list.get(0).topActivity.getPackageName(); 
        }
    }
    
    public static final int DELAY_MILLS = 1000;
    static UpdateHandler sHandler = new UpdateHandler();
    
    static class UpdateHandler extends Handler {
        private WeakReference<ContextSettingsService> mService;
        
        public void setService(ContextSettingsService service) {
            mService = new WeakReference<ContextSettingsService>(service);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mService.get().updateView();
        }
    } ;
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    
}
