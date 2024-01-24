package com.tencent.qcloud.tccccallkit.base;


import android.content.Context;

import com.tencent.qcloud.tccccallkit.EventManager;
import com.tencent.qcloud.tccccallkit.TCCCCallKitImpl;
import com.tencent.tccc.TCCCTypeDef;

import java.util.HashMap;


public class TUICallingStatusManager {
    private static volatile TUICallingStatusManager sInstance;

    private boolean                             mIsMicMute;
    private TUICallDefine.Status                mCallStatus    = TUICallDefine.Status.None;
    private boolean                             mIsAccept;
    private TUICallDefine.Role                  mCallRole      = TUICallDefine.Role.None;
    private TUICallDefine.AudioPlaybackDevice mAudioDevice   = TUICallDefine.AudioPlaybackDevice.Speakerphone;
    private TCCCTypeDef.TCCCLoginParams loginParams = new TCCCTypeDef.TCCCLoginParams();

    public static TUICallingStatusManager sharedInstance(Context context) {
        if (null == sInstance) {
            synchronized (TCCCCallKitImpl.class) {
                if (null == sInstance) {
                    sInstance = new TUICallingStatusManager(context);
                }
            }
        }
        return sInstance;
    }

    private TUICallingStatusManager(Context context) {

    }


    public boolean isMicMute() {
        return mIsMicMute;
    }

    public void setCallRole(TUICallDefine.Role role) {
        mCallRole = role;
    }

    public TUICallDefine.Role getCallRole() {
        return mCallRole;
    }

    public TUICallDefine.Status getCallStatus() {
        return mCallStatus;
    }

    public TUICallDefine.AudioPlaybackDevice getAudioPlaybackDevice() {
        return mAudioDevice;
    }

    public void setLoginInfo(TCCCTypeDef.TCCCLoginParams params) {
        loginParams = params;
    }

    public TCCCTypeDef.TCCCLoginParams getLoginInfo() {
        return loginParams;
    }

    public void updateCallStatus(TUICallDefine.Status status) {
        if (mCallStatus.equals(status)) {
            return;
        }
        mCallStatus = status;
        HashMap<String, Object> map = new HashMap<>();
        map.put(Constants.CALL_STATUS, status);
        EventManager.getInstance().notifyEvent(Constants.EVENT_TUICALLING_CHANGED, Constants.EVENT_SUB_CALL_STATUS_CHANGED, map);
    }

    public void updateAcceptStatus(boolean isAcceptSuccess) {
        mIsAccept = isAcceptSuccess;
        HashMap<String, Object> map = new HashMap<>();
        map.put(Constants.ACCEPT_STATUS, isAcceptSuccess);
        EventManager.getInstance().notifyEvent(Constants.EVENT_TUICALLING_CHANGED, Constants.EVENT_SUB_ACCEPT_STATUS_CHANGED, map);
    }

    public void updateMicMuteStatus(boolean isMicMute) {
        mIsMicMute = isMicMute;
        HashMap<String, Object> map = new HashMap<>();
        map.put(Constants.MUTE_MIC, isMicMute);
        EventManager.getInstance().notifyEvent(Constants.EVENT_TUICALLING_CHANGED, Constants.EVENT_SUB_MIC_STATUS_CHANGED, map);
    }

    public void updateAudioPlaybackDevice(TUICallDefine.AudioPlaybackDevice audioPlaybackDevice) {
        mAudioDevice = audioPlaybackDevice;

        HashMap<String, Object> map = new HashMap<>();
        map.put(Constants.HANDS_FREE, audioPlaybackDevice);
        EventManager.getInstance().notifyEvent(Constants.EVENT_TUICALLING_CHANGED, Constants.EVENT_SUB_AUDIOPLAYDEVICE_CHANGED, map);
    }

    public void clear() {
        mIsMicMute = false;
        mAudioDevice = TUICallDefine.AudioPlaybackDevice.Speakerphone;
        mCallStatus = TUICallDefine.Status.None;
        mCallRole = TUICallDefine.Role.None;
    }
}
