package com.tencent.qcloud.tccccallkit;

import com.tencent.tccc.TCCCError;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.qcloud.tccccallkit.base.CallingUserModel;
import com.tencent.qcloud.tccccallkit.base.Constants;
import com.tencent.qcloud.tccccallkit.base.TUICallDefine;
import com.tencent.qcloud.tccccallkit.base.TUICallingStatusManager;
import com.tencent.qcloud.tccccallkit.base.TUICommonDefine;
import com.tencent.qcloud.tccccallkit.extensions.CallingBellFeature;
import com.tencent.qcloud.tccccallkit.extensions.CallingKeepAliveFeature;
import com.tencent.qcloud.tccccallkit.interfaces.ITUINotification;
import com.tencent.qcloud.tccccallkit.permission.PermissionCallback;
import com.tencent.qcloud.tccccallkit.permission.PermissionRequester;
import com.tencent.qcloud.tccccallkit.utils.DateTimeUtil;
import com.tencent.qcloud.tccccallkit.utils.DeviceUtils;
import com.tencent.qcloud.tccccallkit.utils.PermissionRequest;
import com.tencent.qcloud.tccccallkit.utils.SPUtils;
import com.tencent.qcloud.tccccallkit.utils.ToastUtil;
import com.tencent.qcloud.tccccallkit.view.TUICallingViewManager;
import com.tencent.tccc.TCCCListener;
import com.tencent.tccc.TCCCTypeDef;
import com.tencent.tccc.TCCCWorkstation;
import com.tencent.tccc.TXCallback;
import com.tencent.tccc.TXValueCallback;

import java.util.HashMap;
import java.util.Map;

public class TCCCCallKitImpl extends TCCCCallKit implements ITUINotification {
    private static final String TAG = "TCCCCallKit";

    private static volatile TCCCCallKitImpl sInstance;
    private final Context mContext;

    private TCCCWorkstation cccSDK;
    private TUICommonDefine.CallStatusListener mStatusListener;
    private TUICommonDefine.UserStatusListener mUserStatusListener;
    private final CallingKeepAliveFeature mCallingKeepAliveFeature;
    private final CallingBellFeature mCallingBellFeature;
    private final TUICallingViewManager mCallingViewManager;

    private CallingUserModel callInfo = new CallingUserModel();
    private CallingUserModel incomingCallInfo = new CallingUserModel();

    private final Handler                    mMainHandler = new Handler(Looper.getMainLooper());


    private Runnable      mTimeRunnable;
    private int           mTimeCount;
    private Handler mTimeHandler;
    private HandlerThread mTimeHandlerThread;

    private long mSelfLowQualityTime;
    private long mOtherUserLowQualityTime;
    public static TCCCCallKitImpl createInstance(Context context) {
        if (null == sInstance) {
            synchronized (TCCCCallKitImpl.class) {
                if (null == sInstance) {
                    sInstance = new TCCCCallKitImpl(context);
                }
            }
        }
        return sInstance;
    }

    private TCCCCallKitImpl(Context context) {
        mContext = context.getApplicationContext();
        mCallingKeepAliveFeature = new CallingKeepAliveFeature(mContext);
        mCallingBellFeature = new CallingBellFeature(mContext);
        mCallingViewManager = new TUICallingViewManager(mContext);
        createTimeHandler();
        registerCallingEvent();
        initCallEngine();
    }

    private void registerCallingEvent() {
        //TUICallkit Event
        EventManager.getInstance().registerEvent(Constants.EVENT_TUICALLING_CHANGED, Constants.EVENT_SUB_ACCEPT_STATUS_CHANGED, this);
        EventManager.getInstance().registerEvent(Constants.EVENT_TUICALLING_CHANGED, Constants.EVENT_SUB_CALL_STATUS_CHANGED, this);
    }

    @Override
    public void call(String to,String displayNumber,String remark, TUICommonDefine.Callback callback) {
        Log.i(TAG, "call, to: " + to+" ,remark="+remark);
        if (TextUtils.isEmpty(to)) {
            Log.e(TAG, "call failed, userId is empty");
            callbackError(callback, TUICallDefine.ERROR_PARAM_INVALID, "call failed, userId is empty");
            return;
        }
        internalCall(to,displayNumber,remark,callback);
    }

    @Override
    public void login(String userId,Long sdkAppId,String token, TUICommonDefine.Callback callback) {
        TCCCTypeDef.TCCCLoginParams params = new TCCCTypeDef.TCCCLoginParams();
        params.userId = userId;
        params.sdkAppId = sdkAppId;
        params.token = token;
        params.type = TCCCTypeDef.TCCCLoginType.Agent;
        Log.i(TAG, "login, params.userId="+userId);
        cccSDK.login(params, new TXValueCallback<TCCCTypeDef.TCCCLoginInfo>() {
            @Override
            public void onSuccess(TCCCTypeDef.TCCCLoginInfo tcccLoginInfo) {
                Log.i(TAG, "login success sipUserId="+tcccLoginInfo.userId);
                TUICallingStatusManager.sharedInstance(mContext).setLoginInfo(params);
                callbackSuccess(callback);
            }

            @Override
            public void onError(int i, String s) {
                Log.e(TAG, "login onError errorCode="+i);
                TUICallingStatusManager.sharedInstance(mContext).setLoginInfo(new TCCCTypeDef.TCCCLoginParams());
                callbackError(callback,i,s);
            }
        });
    }

    @Override
    public boolean isUserLogin() {
        TCCCTypeDef.TCCCLoginParams params =TUICallingStatusManager.sharedInstance(mContext).getLoginInfo();
        boolean isOK = !TextUtils.isEmpty(params.userId);
        Log.i(TAG, "isUserLogin, isOk="+isOK);
        return isOK;
    }

    @Override
    public void logout(TUICommonDefine.Callback callback) {
        Log.i(TAG, "logout");
        cccSDK.logout(new TXCallback() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "logout success");
                TUICallingStatusManager.sharedInstance(mContext).setLoginInfo(new TCCCTypeDef.TCCCLoginParams());
                callbackSuccess(callback);
            }

            @Override
            public void onError(int i, String s) {
                Log.e(TAG, "logout error,code="+i);
                callbackError(callback,i,s);
            }
        });
    }

    @Override
    public void setCallingBell(String filePath) {
        Log.i(TAG, "setCallingBell, filePath: " + filePath);
        SPUtils.getInstance(CallingBellFeature.PROFILE_TUICALLKIT).put(CallingBellFeature.PROFILE_CALL_BELL, filePath);
    }

    @Override
    public void enableMuteMode(boolean enable) {
        Log.i(TAG, "enableMuteMode, enable: " + enable);
        SPUtils.getInstance(CallingBellFeature.PROFILE_TUICALLKIT).put(CallingBellFeature.PROFILE_MUTE_MODE, enable);
    }

    @Override
    public void enableFloatWindow(boolean enable) {
        Log.i(TAG, "enableFloatWindow, enable: " + enable);
        mCallingViewManager.enableFloatWindow(enable);
    }


    public void queryOfflineCall() {
        TUICallDefine.Status currentStatus = TUICallingStatusManager.sharedInstance(mContext).getCallStatus();
        if (!TUICallDefine.Status.Accept.equals(currentStatus)) {
            TUICallDefine.Role role = TUICallingStatusManager.sharedInstance(mContext).getCallRole();

            //The received call has been processed in #onCallReceived
            if (TUICallDefine.Role.Called.equals(role)
                    && PermissionRequester.newInstance(PermissionRequester.BG_START_PERMISSION).has()) {
                return;
            }
            Log.i(TAG, "queryOfflineCall and PermissionRequest,role="+role);
            PermissionRequest.requestPermissions(mContext,new PermissionCallback() {
                @Override
                public void onGranted() {
                    if (TUICallDefine.Role.None.equals(role)) {
                        return;
                    }
                    if ((TextUtils.isEmpty(callInfo.phoneNumber) && TUICallDefine.Role.Caller.equals(role)) ||
                            (TextUtils.isEmpty(incomingCallInfo.phoneNumber) && TUICallDefine.Role.Called.equals(role))) {
                        return;
                    }
                    Log.i(TAG, "queryOfflineCall and showCallingView,callInfo.userId="+callInfo.phoneNumber +" ,incomingCallInfo.userId="+incomingCallInfo.phoneNumber);
                    showCallingView();
                }

                @Override
                public void onDenied() {
                    Log.e(TAG, "queryOfflineCall , PermissionRequest onDenied,role="+role);
                    if (TUICallDefine.Role.Called.equals(role)) {
                        Log.e(TAG, "queryOfflineCall, PermissionRequest Denied, terminate");
                        cccSDK.terminate();
                    }
                    resetCall();
                }
            });
        }
    }

    private void internalCall(String to,String displayNumber,String remark,TUICommonDefine.Callback callback) {
        callInfo.phoneNumber = to;
        callInfo.remark = remark;
        callInfo.displayNumber = displayNumber;
        PermissionRequest.requestPermissions(mContext, new PermissionCallback() {

            @Override
            public void onGranted() {
                Log.i(TAG, "call->PermissionRequest success");
                TCCCTypeDef.TCCCStartCallParams params = new TCCCTypeDef.TCCCStartCallParams();
                params.to = to;
                params.remark = remark;
                cccSDK.call(params, new TXCallback() {
                    @Override
                    public void onSuccess() {
                        Log.i(TAG, "call success");
                        TUICallingStatusManager.sharedInstance(mContext).setCallRole(TUICallDefine.Role.Caller);
                        showCallingView();
                        // 不需要调用
                        // mCallingBellFeature.startDialingMusic();
                        callbackSuccess(callback);
                    }

                    @Override
                    public void onError(int i, String s) {
                        Log.e(TAG, "call failed, errorCode="+i);
                        ToastUtil.toastLongMessage(s);
                        callbackError(callback, i, s);
                    }
                });

            }

            @Override
            public void onDenied() {
                Log.i(TAG, "call-> PermissionRequest onDenied");
                callbackError(callback, TUICallDefine.ERROR_PERMISSION_DENIED, "permission denied");
                resetCall();
            }
        });
    }

    private void resetCall() {
        runOnMainThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "resetCall and closeCallingView");
                stopTimeCount();
                mCallingBellFeature.stopMusic();
                mCallingKeepAliveFeature.stopKeepAlive();

                mCallingViewManager.closeCallingView();
                callInfo = new CallingUserModel();
                incomingCallInfo = new CallingUserModel();
                TUICallingStatusManager.sharedInstance(mContext).clear();
            }
        });
    }

    private void runOnMainThread(Runnable task) {
        if (null != task) {
            mMainHandler.post(task);
        }
    }

    private void showUserToast(CallingUserModel model, int msgId) {
        if (null == model || TextUtils.isEmpty(model.phoneNumber)) {
            return;
        }
        if (!TextUtils.isEmpty(model.remark)) {
            ToastUtil.toastLongMessage(mContext.getString(msgId, model.remark));
            return;
        }
        ToastUtil.toastLongMessage(mContext.getString(msgId, model.phoneNumber));
    }

    private void onCallReceived(String number) {
        Log.i(TAG, "onCallReceived number="+number);
        incomingCallInfo.phoneNumber = number;
        incomingCallInfo.remark = number;
        TUICallingStatusManager.sharedInstance(mContext).setCallRole(TUICallDefine.Role.Called);

        //when app comes back to foreground, start the call
        boolean hasBgPermission = PermissionRequester.newInstance(PermissionRequester.BG_START_PERMISSION).has();
        boolean isAppInBackground = !DeviceUtils.isAppRunningForeground(mContext);

        if (isAppInBackground && !hasBgPermission) {
            Log.i(TAG, "App is in background");
            mCallingBellFeature.startRing();
            return;
        }

        TUICallingStatusManager.sharedInstance(mContext).updateCallStatus(TUICallDefine.Status.Waiting);

        PermissionRequest.requestPermissions(mContext, new PermissionCallback() {
            @Override
            public void onGranted() {
                Log.i(TAG, "onCallReceived->PermissionRequest success,and showCallingView");
                showCallingView();
                mCallingBellFeature.startRing();
            }

            @Override
            public void onDenied() {
                Log.e(TAG, "onCallReceived->PermissionRequest onDenied,and terminate->resetCall");
                cccSDK.terminate();
                resetCall();
            }
        });
    }

    private void showCallingView() {
        Log.i(TAG, "showCallingView");
        mCallingViewManager.createCallingView(callInfo, incomingCallInfo);
        TUICallingStatusManager.sharedInstance(mContext).updateCallStatus(TUICallDefine.Status.Waiting);

        mCallingKeepAliveFeature.startKeepAlive();
        mCallingViewManager.showCallingView();
    }

    private void createTimeHandler() {
        mTimeHandlerThread = new HandlerThread("time-count-thread");
        mTimeHandlerThread.start();
        mTimeHandler = new Handler(mTimeHandlerThread.getLooper());
    }

    private void showTimeCount() {
        if (mTimeRunnable != null) {
            return;
        }
        mTimeCount = 0;
        mTimeRunnable = new Runnable() {
            @Override
            public void run() {
                mTimeCount++;
                mCallingViewManager.userCallingTimeStr(DateTimeUtil.formatSecondsTo00(mTimeCount));
                mTimeHandler.postDelayed(mTimeRunnable, 1000);
            }
        };
        mTimeHandler.post(mTimeRunnable);
    }

    private void stopTimeCount() {
        mTimeHandler.removeCallbacks(mTimeRunnable);
        mTimeRunnable = null;
        mTimeCount = 0;
    }

    private void updateNetworkQuality(TCCCTypeDef.TCCCQualityInfo localQuality) {
        if (null == localQuality)
            return;
        boolean isLocalLowQuality = false;
        isLocalLowQuality = isLowQuality(localQuality.quality);
        if (isLocalLowQuality) {
            updateLowQualityTip();
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put(Constants.NETWORK_STATUS, localQuality.quality);
        EventManager.getInstance().notifyEvent(Constants.EVENT_TUICALLING_CHANGED,Constants.EVENT_SUB_NETWORK_STATUS_CHANGED,map);
    }

    private boolean isLowQuality(TCCCTypeDef.TCCCQuality quality) {
        if (null == quality) {
            return false;
        }
        boolean lowQuality;
        switch (quality) {
            case TCCCQuality_Vbad:
            case TCCCQuality_Down:
                lowQuality = true;
                break;
            default:
                lowQuality = false;
        }
        return lowQuality;
    }

    private void updateLowQualityTip() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mSelfLowQualityTime > Constants.MIN_DURATION_SHOW_LOW_QUALITY) {
            ToastUtil.toastShortMessage(mContext.getString(R.string.tuicalling_network_poor_quality));
            mSelfLowQualityTime = currentTime;
        }
    }

    private final TCCCListener tcccListener = new TCCCListener() {
        @Override
        public void onError(int errCode, String errMsg, Bundle extraInfo) {
            super.onError(errCode, errMsg, extraInfo);
            Log.e(TAG,"CCC onError,errCode="+errCode+" ,errMsg="+errMsg);
            if (errCode == TCCCError.ERR_SIP_FORBIDDEN) {
                TUICallingStatusManager.sharedInstance(mContext).setLoginInfo(new TCCCTypeDef.TCCCLoginParams());
                ToastUtil.toastLongMessageCenter(mContext.getString(R.string.tuicalling_logged_elsewhere));
                if( null != mUserStatusListener){
                    mUserStatusListener.onKickedOffline();
                }
                return;
            }
        }

        @Override
        public void onWarning(int warningCode, String warningMsg, Bundle extraInfo) {
            super.onWarning(warningCode, warningMsg, extraInfo);
            Log.w(TAG,"CCC onError,warningCode="+warningCode+" ,warningMsg="+warningMsg);
        }

        @Override
        public void onNewSession(TCCCTypeDef.ITCCCSessionInfo info) {
            super.onNewSession(info);
            Log.i(TAG,"CCC onNewSession,sessionId="+info.sessionId+" ,sessionDirection="+info.sessionDirection+",customHeaderJson="+info.customHeaderJson);
            if (TCCCTypeDef.TCCCSessionDirection.CallIn.equals(info.sessionDirection)) {
                onCallReceived(info.fromUserId);
            }
            if (null != mStatusListener) {
                TUICommonDefine.SessionInfo mInfo = new TUICommonDefine.SessionInfo(info);
                mStatusListener.onNewSession(mInfo);
            }
        }

        @Override
        public void onEnded(EndedReason reason, String reasonMessage, String sessionId) {
            super.onEnded(reason, reasonMessage, sessionId);
            Log.i(TAG, "CCC onEnded,reason= "+reason+", reasonMessage="+reasonMessage+" ,sessionId="+sessionId);
            String msg = "";
            if (reason == EndedReason.Error) {
                // 外呼规则如下：
                // https://cloud.tencent.com/document/product/679/79155
                msg = String.format(mContext.getString(R.string.tuicalling_ended_reason_error),"["+reason.ordinal()+"]"+reasonMessage) ;

            } else if (reason == EndedReason.Timeout) {
                msg = mContext.getString(R.string.tuicalling_ended_reason_timeout);
            } else if (reason == EndedReason.LocalBye) {
                msg = mContext.getString(R.string.tuicalling_ended_reason_local_bye);
            } else if (reason == EndedReason.RemoteBye) {
                msg = mContext.getString(R.string.tuicalling_ended_reason_remote_bye);
            } else if (reason == EndedReason.Rejected) {
                msg = mContext.getString(R.string.tuicalling_ended_reason_rejected);
            } else if (reason == EndedReason.RemoteCancel) {
                msg = mContext.getString(R.string.tuicalling_ended_reason_remote_cancel);
            }
            if (!TextUtils.isEmpty(msg)) {
                ToastUtil.toastLongMessageCenter(msg);
            }
            resetCall();
            if (null != mStatusListener) {
                mStatusListener.onEnded( TUICommonDefine.EndedReason.values()[reason.ordinal()], reasonMessage, sessionId);
            }
        }

        @Override
        public void onAccepted(String sessionId) {
            super.onAccepted(sessionId);
            Log.i(TAG, "CCC onAccepted,sessionId="+sessionId);
            mCallingBellFeature.stopMusic();
            TUICallingStatusManager.sharedInstance(mContext).updateCallStatus(TUICallDefine.Status.Accept);
            showTimeCount();
            if (null != mStatusListener) {
                mStatusListener.onAccepted(sessionId);
            }
        }

        @Override
        public void onAudioVolume(TCCCTypeDef.TCCCVolumeInfo volumeInfo) {
            super.onAudioVolume(volumeInfo);
            if (null != mStatusListener) {
                TUICommonDefine.VolumeInfo tcccVolumeInfo = new TUICommonDefine.VolumeInfo(volumeInfo);
                mStatusListener.onAudioVolume(tcccVolumeInfo);
            }
        }

        @Override
        public void onNetworkQuality(TCCCTypeDef.TCCCQualityInfo localQuality, TCCCTypeDef.TCCCQualityInfo remoteQuality) {
            super.onNetworkQuality(localQuality, remoteQuality);
            updateNetworkQuality(localQuality);
            if (null != mStatusListener) {
                TUICommonDefine.QualityInfo mLocalQuality = new TUICommonDefine.QualityInfo(localQuality);
                TUICommonDefine.QualityInfo mRemoteQuality = new TUICommonDefine.QualityInfo(remoteQuality);
                mStatusListener.onNetworkQuality(mLocalQuality,mRemoteQuality);
            }
        }

        @Override
        public void onStatistics(TCCCTypeDef.TCCCStatistics statistics) {
            super.onStatistics(statistics);
        }

        @Override
        public void onConnectionLost(TCCCServerType serverType) {
            super.onConnectionLost(serverType);
            Log.e(TAG, "CCC onConnectionLost,serverType="+serverType);
        }

        @Override
        public void onTryToReconnect(TCCCServerType serverType) {
            super.onTryToReconnect(serverType);
            Log.w(TAG, "CCC onTryToReconnect,serverType="+serverType);
        }

        @Override
        public void onConnectionRecovery(TCCCServerType serverType) {
            super.onConnectionRecovery(serverType);
            Log.i(TAG, "CCC onConnectionRecovery,serverType="+serverType);
        }
    };

    private void initCallEngine() {
        if (cccSDK == null) {
            cccSDK = TCCCWorkstation.sharedInstance(mContext);
            cccSDK.callExperimentalAPI("setUserAgentStr","TCCCCallKit");
            cccSDK.setListener(tcccListener);
        }
    }

    @Override
    public void setCallStatusListener(TUICommonDefine.CallStatusListener callStatusListener) {
        mStatusListener = callStatusListener;
    }

    @Override
    public void setUserStatusListener(TUICommonDefine.UserStatusListener userStatusListener) {
        mUserStatusListener = userStatusListener;
    }

    @Override
    public void onNotifyEvent(String key, String subKey, Map<String, Object> param) {
        //TUICallkit Event
        if (Constants.EVENT_TUICALLING_CHANGED.equals(key)
                && Constants.EVENT_SUB_CALL_STATUS_CHANGED.equals(subKey) && param != null) {
            if (TUICallDefine.Status.None.equals(param.get(Constants.CALL_STATUS))) {
                Log.i(TAG, "TCCCCallKitImpl onNotifyEvent,resetCall");
                resetCall();
            }
        }
        if (Constants.EVENT_TUICALLING_CHANGED.equals(key) && Constants.EVENT_SUB_ACCEPT_STATUS_CHANGED.equals(subKey)) {
            mCallingBellFeature.stopMusic();
            if (!((boolean) param.get(Constants.ACCEPT_STATUS))) {
                stopTimeCount();
            } else {
                showTimeCount();
            }

        }

    }

    public void callbackSuccess(TUICommonDefine.Callback callback) {
        if (callback != null) {
            callback.onSuccess();
        }
    }

    public void callbackError(TUICommonDefine.Callback callback, int errCode, String errMsg) {
        if (callback != null) {
            callback.onError(errCode, errMsg);
        }
    }
}
