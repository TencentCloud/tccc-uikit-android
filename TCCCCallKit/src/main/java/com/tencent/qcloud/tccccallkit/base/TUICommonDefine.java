package com.tencent.qcloud.tccccallkit.base;

import com.tencent.tccc.TCCCTypeDef;

public class TUICommonDefine {

    public interface Callback {
        public void onSuccess();
        public void onError(int errCode, String errMsg);
    }

    public interface UserStatusListener {
        public void onKickedOffline();
    }

    public static class VolumeInfo {
        public String userId;
        public int volume;

        public VolumeInfo(TCCCTypeDef.TCCCVolumeInfo info) {
            this.userId = info.userId;
            this.volume = info.volume;
        }
    }

    public static enum EndedReason {
        Error,
        Timeout,
        Replaced,
        LocalBye,
        RemoteBye,
        LocalCancel,
        RemoteCancel,
        Rejected,
        Referred;

        private EndedReason() {
        }
    }

    public static enum SessionDirection {
        CallIn,
        CallOut;

        private SessionDirection() {
        }
    }

    public static class SessionInfo {
        public String sessionId;
        public String toUserId;
        public String fromUserId;
        public SessionDirection sessionDirection;
        public String customHeaderJson;

        public SessionInfo(TCCCTypeDef.ITCCCSessionInfo info) {
            this.sessionDirection = SessionDirection.values()[info.sessionDirection.ordinal()];
            this.sessionId = info.sessionId;
            this.fromUserId = info.fromUserId;
            this.toUserId = info.toUserId;
            this.customHeaderJson = info.customHeaderJson;
        }
    }

    public static enum Quality {
        TCCCQuality_Unknown,
        TCCCQuality_Excellent,
        TCCCQuality_Good,
        TCCCQuality_Poor,
        TCCCQuality_Bad,
        TCCCQuality_Vbad,
        TCCCQuality_Down;

        private Quality() {
        }
    }

    public static class QualityInfo {
        public String userId;
        public Quality quality;

        public QualityInfo(TCCCTypeDef.TCCCQualityInfo info) {
            this.quality = Quality.values()[info.quality.ordinal()];
            this.userId = info.userId;
        }
    }

    public interface CallStatusListener{
        public void onNewSession(SessionInfo info);

        public void onEnded(TUICommonDefine.EndedReason reason, String reasonMessage, String sessionId);

        public void onAccepted(String sessionId);

        public void onAudioVolume(VolumeInfo volumeInfo);

        public void onNetworkQuality(QualityInfo localQuality, QualityInfo remoteQuality);
    }
}
