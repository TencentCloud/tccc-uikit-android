package com.tencent.qcloud.tccccallkit.base;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.tencent.qcloud.tccccallkit.R;
import com.tencent.qcloud.tccccallkit.utils.DeviceUtils;
import com.tencent.qcloud.tccccallkit.view.root.BaseCallView;

public class BaseCallActivity extends AppCompatActivity {
    private static final String TAG = "TCCCCallKit";
    private static AppCompatActivity mActivity;
    private static BaseCallView mBaseCallView;
    private static RelativeLayout    mLayoutContainer;

    public static void updateBaseView(BaseCallView view) {
        mBaseCallView = view;
        if (null != mLayoutContainer && null != mBaseCallView) {
            mLayoutContainer.removeAllViews();
            if (null != mBaseCallView.getParent()) {
                ((ViewGroup) mBaseCallView.getParent()).removeView(mBaseCallView);
            }
            mLayoutContainer.addView(mBaseCallView);
        }
    }

    public static void finishActivity() {
        if (null != mActivity) {
            Log.i(TAG,"finishActivity mActivity.finish");
            mActivity.finish();
        } else {
            Log.e(TAG,"finishActivity mActivity is null");
        }
        mActivity = null;
        if (null != mBaseCallView && null != mBaseCallView.getParent()) {
            ((ViewGroup) mBaseCallView.getParent()).removeView(mBaseCallView);
        }
        mBaseCallView = null;
        mLayoutContainer = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TUICallingStatusManager mInstance = TUICallingStatusManager.sharedInstance(this.getApplicationContext());
        TUICallDefine.Status status = mInstance.getCallStatus();
        if (TUICallDefine.Status.None.equals(status)) {
            Log.e(TAG,"BaseCallActivity onCreate,but CallStatus is None,close this activity");
            finish();
            return;
        }
        Log.i(TAG,"BaseCallActivity onCreate,mActivity create new");
        DeviceUtils.setScreenLockParams(getWindow());
        mActivity = this;
        setContentView(R.layout.tuicalling_base_activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initStatusBar();
        }
    }

    private void initStatusBar() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"BaseCallActivity onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"BaseCallActivity onResume");
        initView();
        // clear notifications after a call is processed
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    private void initView() {
        mLayoutContainer = findViewById(R.id.rl_container);
        mLayoutContainer.removeAllViews();
        if (null != mBaseCallView) {
            if (null != mBaseCallView.getParent()) {
                ((ViewGroup) mBaseCallView.getParent()).removeView(mBaseCallView);
            }
            mLayoutContainer.addView(mBaseCallView);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"BaseCallActivity onDestroy");
    }
}
