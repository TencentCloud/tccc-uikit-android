package com.tencent.qcloud.tccccallkit.internal;

import android.app.Activity;
import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.qcloud.tccccallkit.TCCCCallKitImpl;
import com.tencent.qcloud.tccccallkit.base.BaseCallActivity;
import com.tencent.qcloud.tccccallkit.base.TUICallingStatusManager;
import com.tencent.qcloud.tccccallkit.base.TUICommonDefine;
import com.tencent.qcloud.tccccallkit.utils.DeviceUtils;
import com.tencent.qcloud.tccccallkit.view.floatwindow.FloatWindowService;
import com.tencent.tccc.TCCCTypeDef;

import java.util.Objects;

public class ServiceInitializer extends ContentProvider {
    private static Context appContext;

    public void init(Context context) {
        ServiceInitializer.appContext = context;

        if (context instanceof Application) {
            ((Application) context).registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                private int foregroundActivities = 0;
                private boolean isChangingConfiguration;

                @Override
                public void onActivityCreated(@NonNull Activity activity, Bundle bundle) {
                }

                @Override
                public void onActivityStarted(@NonNull Activity activity) {
                    foregroundActivities++;
                    if (foregroundActivities == 1 && !isChangingConfiguration) {
                        // The Call page exits the background and re-enters without repeatedly pulling up the page.
                        TUICallingStatusManager manager = TUICallingStatusManager.sharedInstance(context);
                        TCCCTypeDef.TCCCLoginParams loginInfo = manager.getLoginInfo();
                        if (!TextUtils.isEmpty(loginInfo.userId) && !(activity instanceof BaseCallActivity)
                                && !DeviceUtils.isServiceRunning(context, FloatWindowService.class.getName())) {
                            TCCCCallKitImpl.createInstance(context).isUserLogin(new TUICommonDefine.Callback() {
                                @Override
                                public void onSuccess() {
                                    TCCCCallKitImpl.createInstance(context).queryOfflineCall();
                                }

                                @Override
                                public void onError(int errCode, String errMsg) {

                                }
                            });
                        }
                    }
                    isChangingConfiguration = false;
                }

                @Override
                public void onActivityResumed(@NonNull Activity activity) {

                }

                @Override
                public void onActivityPaused(@NonNull Activity activity) {

                }

                @Override
                public void onActivityStopped(@NonNull Activity activity) {
                    foregroundActivities--;
                    isChangingConfiguration = activity.isChangingConfigurations();
                }

                @Override
                public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(@NonNull Activity activity) {

                }
            });
        }

    }

    public static Context getAppContext() {
        return appContext;
    }

    @Override
    public boolean onCreate() {
        Context appContext = Objects.requireNonNull(getContext()).getApplicationContext();
        init(appContext);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings,
                        @Nullable String s, @Nullable String[] strings1,
                        @Nullable String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
