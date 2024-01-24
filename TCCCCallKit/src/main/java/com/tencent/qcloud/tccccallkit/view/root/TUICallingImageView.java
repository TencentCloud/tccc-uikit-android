package com.tencent.qcloud.tccccallkit.view.root;

import android.content.Context;
import android.nfc.Tag;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.qcloud.tccccallkit.R;

public class TUICallingImageView extends BaseCallView{
    private static final String TAG = "TCCCCallKit";
    private final Context        mContext;
    private LinearLayout   mLayoutUserView;
    private RelativeLayout mLayoutFunction;
    private TextView       mTextTime;
    private TextView       mTextCallHint;
    private View           mRootView;
    private RelativeLayout mLayoutFloatView;

    public TUICallingImageView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public void initView() {
        Log.i(TAG,"TUICallingImageView initView");
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.tuicalling_background_image_view, this);
        mLayoutFloatView = findViewById(R.id.rl_float_view);
        mLayoutUserView = findViewById(R.id.rl_single_audio_view);
        mTextCallHint = findViewById(R.id.tv_call_hint);
        mTextTime = findViewById(R.id.tv_image_time);
        mLayoutFunction = findViewById(R.id.rl_image_function);
    }

    @Override
    public void updateUserView(View view) {
        super.updateUserView(view);
        mLayoutUserView.removeAllViews();
        if (null != view) {
            mLayoutUserView.addView(view);
        }
    }

    @Override
    public void updateCallingHint(String hint) {
        super.updateCallingHint(hint);
        mTextCallHint.setText(hint);
        mTextCallHint.setVisibility(TextUtils.isEmpty(hint) ? GONE : VISIBLE);
    }

    @Override
    public void updateCallTimeView(String time) {
        mTextTime.setText(time);
        mTextTime.setVisibility(TextUtils.isEmpty(time) ? GONE : VISIBLE);
    }

    @Override
    public void updateFunctionView(View view) {
        mLayoutFunction.removeAllViews();
        if (null != view) {
            mLayoutFunction.addView(view);
        }
    }

    @Override
    public void updateBackgroundColor(int color) {
        mRootView.setBackgroundColor(color);
    }

    @Override
    public void updateTextColor(int color) {
        super.updateTextColor(color);
        mTextCallHint.setTextColor(color);
        mTextTime.setTextColor(color);
    }

    @Override
    public void enableFloatView(View view) {
        mLayoutFloatView.removeAllViews();
        if (null != view) {
            mLayoutFloatView.addView(view);
        }
    }

}
