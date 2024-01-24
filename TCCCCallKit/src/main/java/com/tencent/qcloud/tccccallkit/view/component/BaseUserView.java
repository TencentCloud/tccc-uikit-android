package com.tencent.qcloud.tccccallkit.view.component;

import android.content.Context;
import android.widget.RelativeLayout;

import com.tencent.qcloud.tccccallkit.base.CallingUserModel;
import com.tencent.tccc.TCCCTypeDef;

public class BaseUserView extends RelativeLayout {
    public BaseUserView(Context context) {
        super(context);
    }

    public void updateUserInfo(CallingUserModel userModel) {
    }

    public void updateNetworkTip(TCCCTypeDef.TCCCQuality quality) {

    }

    public void updateTextColor(int color) {
    }
}
