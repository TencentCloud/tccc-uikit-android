package com.tencent.qcloud.tccccallkit.view.function;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.qcloud.tccccallkit.R;
import com.tencent.qcloud.tccccallkit.base.TUICommonDefine;


// 呼入等待接听界面的功能按键
public class TUICallingWaitFunctionView extends BaseFunctionView {
    private LinearLayout mLayoutReject;
    private LinearLayout mLayoutDialing;
    private TextView     mTextReject;
    private TextView     mTextDialing;

    public TUICallingWaitFunctionView(Context context) {
        super(context);
        initView();
        initListener();
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.tuicalling_funcation_view_audio_waiting, this);
        mLayoutReject = findViewById(R.id.ll_decline);
        mLayoutDialing = findViewById(R.id.ll_answer);
        mTextReject = findViewById(R.id.tv_reject);
        mTextDialing = findViewById(R.id.tv_dialing);
    }

    private void initListener() {
        mLayoutReject.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallingAction.reject();
            }
        });

        mLayoutDialing.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallingAction.accept(new TUICommonDefine.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(int errCode, String errMsg) {

                    }
                });
            }
        });
    }

    @Override
    public void updateTextColor(int color) {
        mTextReject.setTextColor(color);
        mTextDialing.setTextColor(color);
    }
}
