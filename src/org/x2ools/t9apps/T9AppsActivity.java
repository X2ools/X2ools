
package org.x2ools.t9apps;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import org.x2ools.R;
import org.x2ools.X2oolsApplication;

public class T9AppsActivity extends Activity {
    
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
        Bitmap background = BitmapFactory.decodeFile(X2oolsApplication.X2OOLS_DIR + "screenshot.png");
        if (background != null) {
            mWindow.setBackgroundDrawable(new BitmapDrawable(background));
        }
    }

    private void translateSystemUI() {
       mWindow.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
       mWindow.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

}
