package com.tencent.qcloud.tccccallkit;

import android.content.Context;

import com.tencent.qcloud.tccccallkit.base.TUICommonDefine;
import com.tencent.tccc.TCCCListener;

public abstract class TCCCCallKit {

    public static TCCCCallKit createInstance(Context context) {
        return TCCCCallKitImpl.createInstance(context);
    }

    public void call(String to,String remark,TUICommonDefine.Callback callback) {

    }

    public void login(String userId,Long sdkAppId,String token, TUICommonDefine.Callback callback) {
    }

    public boolean isUserLogin() {
        return false;
    }

    public void logout(TUICommonDefine.Callback callback) {
    }

    /**
     * Enable the floating window
     */
    public void enableFloatWindow(boolean enable) {
    }

    /**
     * Set the ringtone (preferably shorter than 30s)
     *
     * @param filePath Callee ringtone path
     */
    public void setCallingBell(String filePath) {
    }

    /**
     * Enable the mute mode (the callee doesn't ring)
     */
    public void enableMuteMode(boolean enable) {
    }

    public void setCallStatusListener(TUICommonDefine.CallStatusListener callStatusListener) {

    }

    public void setUserStatusListener(TUICommonDefine.UserStatusListener userStatusListener) {

    }
}
