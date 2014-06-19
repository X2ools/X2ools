
package org.x2ools.t9apps;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import org.x2ools.Blur;
import org.x2ools.R;
import org.x2ools.X2oolsApplication;

public class T9AppsActivity extends Activity {

    private static final String TAG = "T9AppsActivity";
    private Window mWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWindow = getWindow();

        translateSystemUI();
        setBlurBackgroud();

        setContentView(R.layout.t9_apps_view);
    }

    private void setBlurBackgroud() {
        Bitmap orgBitmap = BitmapFactory
                .decodeFile(X2oolsApplication.X2OOLS_DIR + "screenshot.png");
        Bitmap blurBitmap = Bitmap.createBitmap(orgBitmap.getWidth(), orgBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        blurBitmap.eraseColor(0xFF000000);
        blurBitmap = Blur.fastblur(this, orgBitmap, 8);
        orgBitmap.recycle();
        if (blurBitmap != null) {
            mWindow.setBackgroundDrawable(new BitmapDrawable(blurBitmap));
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void translateSystemUI() {
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

}
