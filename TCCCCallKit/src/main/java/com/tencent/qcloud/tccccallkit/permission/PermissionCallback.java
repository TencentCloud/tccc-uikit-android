package com.tencent.qcloud.tccccallkit.permission;

public abstract class PermissionCallback {
    public abstract void onGranted();

    public void onRequesting() {}

    public void onDenied() {}
}
