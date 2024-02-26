package com.tencent.qcloud.tccccallkit.view.floatwindow;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tencent.qcloud.tccccallkit.R;
import com.tencent.qcloud.tccccallkit.utils.ImageLoader;


public class FloatCallView extends RelativeLayout {
    private static final int UPDATE_COUNT         = 3;
    private static final int UPDATE_INTERVAL      = 300;
    private static final int MESSAGE_VIEW_EMPTY   = 2;
    private              int mCount               = 0;

    private ImageView      mImageAudio;
    private TextView       mTextHint;
    private TextView       mTextTime;

    private OnClickListener   mOnClickListener;
    private String            mCurrentUser;

    public FloatCallView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.tuicalling_floatwindow_layout, this);
        mTextHint = findViewById(R.id.tv_float_hint);
        mImageAudio = findViewById(R.id.float_audioView);
        mTextTime = findViewById(R.id.tv_float_time);
        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnClickListener) {
                    mOnClickListener.onClick();
                }
            }
        });
    }

    public void updateView(String userId) {
        mCurrentUser = userId;
        mImageAudio.setVisibility(VISIBLE);
    }

    private final Handler mViewHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == MESSAGE_VIEW_EMPTY && mCount < UPDATE_COUNT) {
                sendEmptyMessageDelayed(MESSAGE_VIEW_EMPTY, UPDATE_INTERVAL);
                mCount++;
            }
        }
    };

    public void enableCallingHint(boolean enable) {
        mTextHint.setVisibility(enable ? VISIBLE : GONE);
    }

    public void updateCallTimeView(String time) {
        mTextTime.setText(time);
        mTextTime.setVisibility(TextUtils.isEmpty(time) ? INVISIBLE : VISIBLE);
    }

    public void setOnClickListener(OnClickListener callback) {
        mOnClickListener = callback;
    }

    public String getCurrentUser() {
        return mCurrentUser;
    }

    public interface OnClickListener {
        void onClick();
    }
}
