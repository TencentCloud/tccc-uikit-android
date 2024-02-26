package com.tencent.qcloud.tccccallkit.view.function;

import android.content.Context;
import android.widget.RelativeLayout;

import com.tencent.qcloud.tccccallkit.EventManager;
import com.tencent.qcloud.tccccallkit.base.Constants;
import com.tencent.qcloud.tccccallkit.base.TUICallDefine;
import com.tencent.qcloud.tccccallkit.base.TUICallingAction;
import com.tencent.qcloud.tccccallkit.interfaces.ITUINotification;

import java.util.Map;

public class BaseFunctionView extends RelativeLayout {
    protected Context          mContext;
    protected TUICallingAction mCallingAction;

    public BaseFunctionView(Context context) {
        super(context);
        mContext = context.getApplicationContext();
        mCallingAction = new TUICallingAction(context);
        registerEvent();
    }

    private void registerEvent() {
        EventManager mEventManager = EventManager.getInstance();
        mEventManager.registerEvent(Constants.EVENT_TUICALLING_CHANGED, Constants.EVENT_SUB_MIC_STATUS_CHANGED,
                mNotification);
        mEventManager.registerEvent(Constants.EVENT_TUICALLING_CHANGED, Constants.EVENT_SUB_AUDIOPLAYDEVICE_CHANGED,
                mNotification);
    }

    private final ITUINotification mNotification = new ITUINotification() {
        @Override
        public void onNotifyEvent(String key, String subKey, Map<String, Object> param) {
            if (Constants.EVENT_TUICALLING_CHANGED.equals(key) &&
                    param != null) {
                switch (subKey) {
                    case Constants.EVENT_SUB_MIC_STATUS_CHANGED:
                        updateMicMuteStatus((Boolean) param.get(Constants.MUTE_MIC));
                        break;
                    case Constants.EVENT_SUB_AUDIOPLAYDEVICE_CHANGED:
                        TUICallDefine.AudioPlaybackDevice device =
                                (TUICallDefine.AudioPlaybackDevice) param.get(Constants.HANDS_FREE);
                        updateAudioPlayDevice(TUICallDefine.AudioPlaybackDevice.Speakerphone.equals(device));
                        break;
                    default:
                        break;
                }
            }
        }
    };

    public void updateCameraOpenStatus(boolean isOpen) {
    }

    public void updateMicMuteStatus(boolean isMicMute) {
    }

    public void updateAudioPlayDevice(boolean isSpeaker) {

    }

    public void updateTextColor(int color) {
    }

}
