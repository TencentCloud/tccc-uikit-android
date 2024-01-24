package com.tencent.qcloud.tccccallkit.demo;
import android.app.Application;
import android.os.StrictMode;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        closeAndroidPDialog();
    }

    private void closeAndroidPDialog() {
        try {
            Class<?> aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor<?> declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class<?> cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
