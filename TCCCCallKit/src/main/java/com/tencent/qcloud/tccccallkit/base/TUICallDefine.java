package com.tencent.qcloud.tccccallkit.base;

public class TUICallDefine {

    public TUICallDefine() {
    }

    public static int ERROR_PERMISSION_DENIED = -999;
    public static int ERROR_PARAM_INVALID = -998;

    public static enum Status {
        None,
        Waiting,
        Accept,
    }

    public static enum AudioPlaybackDevice {
        Earpiece,
        Speakerphone,
    }

    public static enum Role {
        None,
        Caller,
        Called,
    }

    public static final class TUICore {
        public static final String LANGUAGE_EVENT         = "TUIThemeManager";
        public static final String LANGUAGE_EVENT_SUB_KEY = "onInitLanguage";
    }

}
