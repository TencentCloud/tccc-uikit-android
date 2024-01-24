package com.tencent.qcloud.tccccallkit.interfaces;

import java.util.Map;

public interface ITUINotification {
    void onNotifyEvent(String key, String subKey, Map<String, Object> param);
}
