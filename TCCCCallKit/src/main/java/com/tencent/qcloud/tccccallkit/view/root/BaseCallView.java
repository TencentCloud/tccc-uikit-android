package com.tencent.qcloud.tccccallkit.view.root;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

public class BaseCallView  extends RelativeLayout {
    protected Context          mContext;
    public BaseCallView(Context context) {
        super(context);
        mContext = context;
    }

    public void updateCallingHint(String hint) {
    }

    public void updateCallTimeView(String time) {
    }

    public void updateFunctionView(View view) {
    }

    public void updateBackgroundColor(int color) {
    }

    public void updateTextColor(int color) {
    }

    public void updateUserView(View view) {
    }

    public void enableFloatView(View view) {
    }

    public void finish() {
        clearAllViews();
        removeAllViews();
        detachAllViewsFromParent();
        onDetachedFromWindow();
    }

    public void clearAllViews() {
        updateCallingHint("");
        updateCallTimeView(null);
        updateUserView(null);
        updateFunctionView(null);
    }
}
