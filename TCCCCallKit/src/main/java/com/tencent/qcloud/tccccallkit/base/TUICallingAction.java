package com.tencent.qcloud.tccccallkit.base;

import android.content.Context;

import com.tencent.tccc.TCCCDeviceManager;
import com.tencent.tccc.TCCCWorkstation;
import com.tencent.tccc.TXCallback;

public class TUICallingAction {
    private final Context mContext;
    private final TCCCWorkstation cccSDK;

    public TUICallingAction(Context context) {
        mContext = context;
        cccSDK = TCCCWorkstation.sharedInstance(mContext);
    }

    public void hangup() {
        cccSDK.terminate();
        TUICallingStatusManager.sharedInstance(mContext).updateCallStatus(TUICallDefine.Status.None);
    }

    public void accept(TUICommonDefine.Callback callback) {
        cccSDK.answer(new TXCallback() {
            @Override
            public void onSuccess() {
                TUICallingStatusManager.sharedInstance(mContext).updateAcceptStatus(true);
                TUICallingStatusManager.sharedInstance(mContext).updateCallStatus(TUICallDefine.Status.Accept);
                if (callback != null) {
                    callback.onSuccess();
                }
            }

            @Override
            public void onError(int i, String s) {
                TUICallingStatusManager.sharedInstance(mContext).updateAcceptStatus(false);
                TUICallingStatusManager.sharedInstance(mContext).updateCallStatus(TUICallDefine.Status.None);
                if (callback != null) {
                    callback.onError(i, s);
                }
            }
        });

    }

    public void reject() {
        cccSDK.terminate();
        TUICallingStatusManager.sharedInstance(mContext).updateCallStatus(TUICallDefine.Status.None);
    }

    public void openMicrophone(TUICommonDefine.Callback callback) {
        cccSDK.unmute();
        TUICallingStatusManager.sharedInstance(mContext).updateMicMuteStatus(false);
    }

    public void selectAudioPlaybackDevice(TUICallDefine.AudioPlaybackDevice device) {
        if (device.equals(TUICallDefine.AudioPlaybackDevice.Speakerphone)) {
            cccSDK.getDeviceManager().setAudioRoute(TCCCDeviceManager.TCCCAudioRoute.TCCCAudioRouteSpeakerphone);
        } else {
            cccSDK.getDeviceManager().setAudioRoute(TCCCDeviceManager.TCCCAudioRoute.TCCCAudioRouteEarpiece);
        }
        TUICallingStatusManager.sharedInstance(mContext).updateAudioPlaybackDevice(device);
    }

    public void closeMicrophone() {
        cccSDK.mute();
        TUICallingStatusManager.sharedInstance(mContext).updateMicMuteStatus(true);
    }
}
