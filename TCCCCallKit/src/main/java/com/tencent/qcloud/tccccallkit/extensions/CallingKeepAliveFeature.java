package com.tencent.qcloud.tccccallkit.extensions;

import android.content.Context;

import com.tencent.qcloud.tccccallkit.service.TUICallService;
import com.tencent.qcloud.tccccallkit.utils.DeviceUtils;

public class CallingKeepAliveFeature {
    private final Context mContext;
    private boolean mEnableKeepAlive = true;

    public CallingKeepAliveFeature(Context context) {
        mContext = context;
    }

    public void enableKeepAlive(boolean enable) {
        mEnableKeepAlive = enable;
    }

    public void startKeepAlive() {
        if (!mEnableKeepAlive) {
            return;
        }
        TUICallService.start(mContext);
    }

    public void stopKeepAlive() {
        if (DeviceUtils.isServiceRunning(mContext, TUICallService.class.getName())) {
            TUICallService.stop(mContext);
        }
    }
}
