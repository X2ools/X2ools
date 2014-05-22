
package org.x2ools.quicksettings;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import org.x2ools.R;
import org.x2ools.wechat.WeChat;

public class WechatScanTile extends BasicTile {

    public WechatScanTile(Context context, Context gbContext, Object statusBar, Object panelBar) {
        super(context, gbContext, statusBar, panelBar);

        mOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setClassName(WeChat.PACKAGE_NAME, "com.tencent.mm.plugin.scanner.ui.BaseScanUI");
                startActivity(i);
            }
        };

        mOnLongClick = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent i = new Intent();
                i.setClassName(WeChat.PACKAGE_NAME, "com.tencent.mm.plugin.scanner.ui.BaseScanUI");
                startActivity(i);
                return true;
            }
        };

        mDrawableId = R.drawable.wechat_scan;
        mLabel = "二维码";
    }

    @Override
    protected int onGetLayoutId() {
        return R.layout.quick_settings_tile_wechat_scan;
    }
}
