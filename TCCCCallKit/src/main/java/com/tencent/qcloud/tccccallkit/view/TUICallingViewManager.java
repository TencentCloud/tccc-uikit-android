package com.tencent.qcloud.tccccallkit.view;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tencent.qcloud.tccccallkit.EventManager;
import com.tencent.qcloud.tccccallkit.R;
import com.tencent.qcloud.tccccallkit.base.BaseCallActivity;
import com.tencent.qcloud.tccccallkit.base.CallingUserModel;
import com.tencent.qcloud.tccccallkit.base.Constants;
import com.tencent.qcloud.tccccallkit.base.TUICallDefine;
import com.tencent.qcloud.tccccallkit.base.TUICallingAction;
import com.tencent.qcloud.tccccallkit.base.TUICallingStatusManager;
import com.tencent.qcloud.tccccallkit.interfaces.ITUINotification;
import com.tencent.qcloud.tccccallkit.permission.PermissionRequester;
import com.tencent.qcloud.tccccallkit.utils.PermissionRequest;
import com.tencent.qcloud.tccccallkit.view.component.BaseUserView;
import com.tencent.qcloud.tccccallkit.view.component.TUICallingUserView;
import com.tencent.qcloud.tccccallkit.view.floatwindow.FloatCallView;
import com.tencent.qcloud.tccccallkit.view.floatwindow.FloatWindowService;
import com.tencent.qcloud.tccccallkit.view.floatwindow.HomeWatcher;
import com.tencent.qcloud.tccccallkit.view.function.BaseFunctionView;
import com.tencent.qcloud.tccccallkit.view.function.TUICallingAudioFunctionView;
import com.tencent.qcloud.tccccallkit.view.function.TUICallingWaitFunctionView;
import com.tencent.qcloud.tccccallkit.view.root.BaseCallView;
import com.tencent.qcloud.tccccallkit.view.root.TUICallingImageView;
import com.tencent.tccc.TCCCTypeDef;

import java.util.Map;
import java.util.Objects;

public class TUICallingViewManager implements ITUINotification {
    private static final String TAG = "TCCCCallKit";
    private final Context           mContext;
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());


    private boolean                mEnableFloatView = false;
    private final TUICallingAction  mCallingAction;

    private BaseCallView mBaseCallView;
    private BaseFunctionView mFunctionView;
    private BaseUserView mUserView;
    private CallingUserModel       mCallInfo     = new CallingUserModel();
    private CallingUserModel mIncomingCallInfo = new CallingUserModel();

    private ImageView mImageFloatFunction;
    private FloatCallView mFloatCallView;
    private HomeWatcher mHomeWatcher;

    public TUICallingViewManager(Context context) {
        mContext = context.getApplicationContext();
        mCallingAction = new TUICallingAction(context);
        registerCallingEvent();
    }

    private void registerCallingEvent() {
        EventManager eventManager = EventManager.getInstance();
        eventManager.registerEvent(Constants.EVENT_TUICALLING_CHANGED, Constants.EVENT_SUB_MIC_STATUS_CHANGED, this);
        eventManager.registerEvent(Constants.EVENT_TUICALLING_CHANGED, Constants.EVENT_SUB_CALL_STATUS_CHANGED, this);
        eventManager.registerEvent(Constants.EVENT_TUICALLING_CHANGED, Constants.EVENT_SUB_NETWORK_STATUS_CHANGED,this);
    }

    public void createCallingView(CallingUserModel callInfo,CallingUserModel incomingCall) {
        this.mCallInfo = callInfo;
        this.mIncomingCallInfo = incomingCall;
        initHomeWatcher();
        initSingleWaitingView();
    }

    public void enableFloatWindow(boolean enable) {
        mEnableFloatView = enable;
    }

    public void showCallingView() {
        BaseCallActivity.updateBaseView(mBaseCallView);
        Intent intent = new Intent(mContext, BaseCallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public void userCallingTimeStr(String time) {
        if (null == mBaseCallView) {
            return;
        }
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (null != mFloatCallView) {
                    mFloatCallView.updateCallTimeView(time);
                }

                if (null != mBaseCallView) {
                    mBaseCallView.updateCallTimeView(time);
                }
            }
        });
    }

    private void initSingleAcceptCallView() {
        initSingleAudioAcceptCallView();
    }

    private void initSingleAudioAcceptCallView() {
        Log.i(TAG,"initSingleAudioAcceptCallView");
        if (mBaseCallView != null) {
            mBaseCallView.finish();
            mBaseCallView = null;
        }
        mBaseCallView = new TUICallingImageView(mContext);
        mFunctionView = new TUICallingAudioFunctionView(mContext);
        mUserView = new TUICallingUserView(mContext);


        TUICallDefine.Role role = TUICallingStatusManager.sharedInstance(mContext).getCallRole();
        if (TUICallDefine.Role.Caller.equals(role)) {
            mUserView.updateUserInfo(mCallInfo);
        } else {
            mUserView.updateUserInfo(mIncomingCallInfo);
        }

        mBaseCallView.updateFunctionView(mFunctionView);
        mBaseCallView.updateUserView(mUserView);
        mBaseCallView.updateCallingHint("");

        updateViewColor();
        updateFunctionStatus();
        initFloatingWindowBtn();
        BaseCallActivity.updateBaseView(mBaseCallView);
    }

    private void updateViewColor() {
        Log.i(TAG,"updateViewColor");
        int backgroundColor = mContext.getResources().getColor(R.color.tuicalling_color_audio_background);

        int textColor =  mContext.getResources().getColor(R.color.tuicalling_color_black);

        if (null != mBaseCallView) {
            mBaseCallView.updateBackgroundColor(backgroundColor);
            mBaseCallView.updateTextColor(textColor);
        }

        if (null != mFunctionView) {
            mFunctionView.updateTextColor(textColor);
        }
    }

    private void initFloatingWindowBtn() {
        if (mBaseCallView == null) {
            return;
        }

        if (mEnableFloatView) {
            mImageFloatFunction = new ImageView(mContext);
            int resId = R.drawable.tuicalling_ic_move_back_black;
            mImageFloatFunction.setBackgroundResource(resId);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            mImageFloatFunction.setLayoutParams(lp);
            mImageFloatFunction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startFloatService();
                }
            });
        }

        if (mImageFloatFunction != null && mImageFloatFunction.getParent() != null) {
            ((ViewGroup) mImageFloatFunction.getParent()).removeView(mImageFloatFunction);
        }
        mBaseCallView.enableFloatView(mImageFloatFunction);
    }

    private void initAudioPlayDevice() {
        TUICallDefine.AudioPlaybackDevice device = TUICallDefine.AudioPlaybackDevice.Earpiece;
        if (TUICallDefine.Role.Caller.equals(TUICallingStatusManager.sharedInstance(mContext).getCallRole())) {
            mCallingAction.selectAudioPlaybackDevice(device);
        } else {
            mCallingAction.selectAudioPlaybackDevice(TUICallDefine.AudioPlaybackDevice.Speakerphone);
        }
        TUICallingStatusManager.sharedInstance(mContext).updateAudioPlaybackDevice(device);
    }

    private void updateFunctionStatus() {
        TUICallingStatusManager statusManager = TUICallingStatusManager.sharedInstance(mContext);
        mCallingAction.selectAudioPlaybackDevice(statusManager.getAudioPlaybackDevice());
        if (statusManager.isMicMute()) {
            mCallingAction.closeMicrophone();
        } else {
            mCallingAction.openMicrophone(null);
        }
    }

    private void updateCallStatus(TUICallDefine.Status status) {
        Log.i(TAG,"TUICallingViewManager updateCallStatus,status=" + status.toString());
        if (TUICallDefine.Status.None.equals(status)) {
            closeCallingView();
            return;
        }
        if (!TUICallDefine.Status.Accept.equals(status)) {
            return;
        }
        // 接听来电
        if (null != mBaseCallView) {
            initSingleAcceptCallView();
        }
        if (null != mFloatCallView) {
            updateFloatView(TUICallDefine.Status.Accept);
        }
        
    }

    private void updateFloatView(TUICallDefine.Status status) {
        if (null == mFloatCallView) {
            return;
        }
        mFloatCallView.enableCallingHint(TUICallDefine.Status.Waiting.equals(status));
        mFloatCallView.updateView(null);
    }

    public void closeCallingView() {
        if (null != mBaseCallView) {
            mBaseCallView.finish();
        }
        mBaseCallView = null;
        BaseCallActivity.finishActivity();
        mFunctionView = null;
        mFloatCallView = null;

        if (null != mHomeWatcher) {
            mHomeWatcher.stopWatch();
            mHomeWatcher = null;
        }
        FloatWindowService.stopService(mContext);
    }

    private void initHomeWatcher() {
        if (null == mHomeWatcher) {
            mHomeWatcher = new HomeWatcher(mContext);
        }
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                if (PermissionRequester.newInstance(PermissionRequester.FLOAT_PERMISSION).has()) {
                    startFloatService();
                }
            }

            @Override
            public void onRecentAppsPressed() {
                if (PermissionRequester.newInstance(PermissionRequester.FLOAT_PERMISSION).has()) {
                    startFloatService();
                }
            }
        });
        mHomeWatcher.startWatch();
    }

    private void startFloatService() {
        if (!mEnableFloatView) {
            return;
        }

        if (null != mFloatCallView) {
            return;
        }
        if (PermissionRequester.newInstance(PermissionRequester.FLOAT_PERMISSION).has()) {
            mFloatCallView = createFloatView();
            updateFloatView(TUICallingStatusManager.sharedInstance(mContext).getCallStatus());
            FloatWindowService.startFloatService(mContext, mFloatCallView);
            BaseCallActivity.finishActivity();
        } else {
            PermissionRequest.requestFloatPermission(mContext);
        }

    }


    private FloatCallView createFloatView() {
        Log.i(TAG,"createFloatView");
        FloatCallView floatView = new FloatCallView(mContext);
        floatView.setOnClickListener(new FloatCallView.OnClickListener() {
            @Override
            public void onClick() {
                FloatWindowService.stopService(mContext);
                mFloatCallView = null;
                mImageFloatFunction = null;

                TUICallDefine.Status status = TUICallingStatusManager.sharedInstance(mContext).getCallStatus();

                if (!TUICallDefine.Status.None.equals(status)) {
                    showCallingView();
                } else {
                    Log.e(TAG, "The current call has ended");
                }
            }
        });
        return floatView;
    }

    private void initSingleWaitingView() {
        initAudioPlayDevice();
        initSingleAudioWaitingView();
    }

    private void initSingleAudioWaitingView() {
        Log.i(TAG,"initSingleAudioWaitingView");
        mBaseCallView = new TUICallingImageView(mContext);
        String hint;
        TUICallDefine.Role callRole = TUICallingStatusManager.sharedInstance(mContext).getCallRole();

        if (TUICallDefine.Role.Caller.equals(callRole)) {
            hint = mContext.getString(R.string.tuicalling_waiting_accept);
            mFunctionView = new TUICallingAudioFunctionView(mContext);
        } else {
            hint = mContext.getString(R.string.tuicalling_invite_audio_call);
            mFunctionView = new TUICallingWaitFunctionView(mContext);
        }

        mUserView = new TUICallingUserView(mContext);
        if (TUICallDefine.Role.Caller.equals(callRole)) {
            mUserView.updateUserInfo(mCallInfo);
        } else {
            mUserView.updateUserInfo(mIncomingCallInfo);
        }
        mBaseCallView.updateUserView(mUserView);

        mBaseCallView.updateCallingHint(hint);
        mBaseCallView.updateFunctionView(mFunctionView);
        updateViewColor();
        initFloatingWindowBtn();
        BaseCallActivity.updateBaseView(mBaseCallView);
    }

    @Override
    public void onNotifyEvent(String key, String subKey, Map<String, Object> param) {
        Log.d(TAG,"TUICallingViewManager onNotifyEvent subKey=" + subKey);
        if (param == null) {
            return;
        }
        if (!Constants.EVENT_TUICALLING_CHANGED.equals(key)) {
            return;
        }
        switch (subKey) {
            case Constants.EVENT_SUB_MIC_STATUS_CHANGED:
                break;
            case Constants.EVENT_SUB_CALL_STATUS_CHANGED:
                updateCallStatus((TUICallDefine.Status) Objects.requireNonNull(param.get(Constants.CALL_STATUS)));
                break;
            case Constants.EVENT_SUB_NETWORK_STATUS_CHANGED:
                if (null != mUserView) {
                    mUserView.updateNetworkTip((TCCCTypeDef.TCCCQuality) param.get(Constants.NETWORK_STATUS));
                }
                break;
            default:
                break;
        }
    }

}
