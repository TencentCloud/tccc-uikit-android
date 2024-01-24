package com.tencent.qcloud.tccccallkit.demo;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.tencent.qcloud.debug.GenerateTestUserToken;
import com.tencent.qcloud.tccccallkit.TCCCCallKit;
import com.tencent.qcloud.tccccallkit.base.TUICommonDefine;
import com.tencent.qcloud.tccccallkit.utils.ToastUtil;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    private TextView mCallNumber;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_activity_main);
        initView();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        mCallNumber = findViewById(R.id.ed_edit_number);
        findViewById(R.id.btn_call).setOnClickListener(view -> {
            String callNumber = mCallNumber.getText().toString();
            if (TextUtils.isEmpty(callNumber)) {
                ToastUtil.toastShortMessageCenter(this.getString(R.string.app_hint_call_to));
                return;
            }
            TCCCCallKit.createInstance(getApplicationContext()).call(callNumber,null,null);
        });
        if (TCCCCallKit.createInstance(getApplicationContext()).isUserLogin()) {
            return;
        }
        GenerateTestUserToken.genTestUserSig(GenerateTestUserToken.SECRETID, GenerateTestUserToken.SECRETKEY, GenerateTestUserToken.SDKAPPID, GenerateTestUserToken.USERID, new GenerateTestUserToken.UserTokenCallBack() {
            @Override
            public void onSuccess(String token) {
               TCCCCallKit.createInstance(getApplicationContext()).login(GenerateTestUserToken.USERID, GenerateTestUserToken.SDKAPPID, token, new TUICommonDefine.Callback() {
                   @Override
                   public void onSuccess() {
                       TCCCCallKit.createInstance(getApplicationContext()).enableFloatWindow(true);
                       ToastUtil.toastShortMessage(getString(R.string.app_login_success));
                   }

                   @Override
                   public void onError(int errCode, String errMsg) {
                       String msg = String.format(getString(R.string.app_login_error),"["+errCode+"]"+errMsg);
                       ToastUtil.toastLongMessageCenter(msg);
                   }
               });
            }

            @Override
            public void onError(int code, String desc) {
                String msg = String.format(getString(R.string.app_get_token_error),"["+code+"]"+desc);
                ToastUtil.toastShortMessageCenter(msg);
            }
        });

    }

    private void ExampleCode() {
        TCCCCallKit.createInstance(getApplicationContext()).setUserStatusListener(() -> {

        });
        TCCCCallKit.createInstance(getApplicationContext()).setCallStatusListener(new TUICommonDefine.CallStatusListener() {
            @Override
            public void onNewSession(TUICommonDefine.SessionInfo info) {

            }

            @Override
            public void onEnded(TUICommonDefine.EndedReason reason, String reasonMessage, String sessionId) {

            }

            @Override
            public void onAccepted(String sessionId) {

            }

            @Override
            public void onAudioVolume(TUICommonDefine.VolumeInfo volumeInfo) {

            }

            @Override
            public void onNetworkQuality(TUICommonDefine.QualityInfo localQuality, TUICommonDefine.QualityInfo remoteQuality) {

            }
        });
    }

}
