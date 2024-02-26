package com.tencent.qcloud.tccccallkit.view.component;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.tencent.qcloud.tccccallkit.R;
import com.tencent.qcloud.tccccallkit.base.CallingUserModel;
import com.tencent.tccc.TCCCTypeDef;

public class TUICallingUserView extends BaseUserView {
    private final Context   mContext;
    private TextView mImageAvatar;
    private TextView  mTextUserName;

    public TUICallingUserView(Context context) {
        super(context);
        mContext = context.getApplicationContext();
        initView();
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.tuicalling_user_view, this);
        mImageAvatar = findViewById(R.id.tv_networktips);
        mTextUserName = findViewById(R.id.tv_name);
    }

    @Override
    public void updateUserInfo(CallingUserModel model) {
        super.updateUserInfo(model);
        mTextUserName.setText(TextUtils.isEmpty(model.displayNumber) ? model.phoneNumber : model.displayNumber);
    }

    @Override
    public void updateNetworkTip(TCCCTypeDef.TCCCQuality quality) {
        if (null == quality) {
            return;
        }
        String tips = "";
        switch (quality) {
            case TCCCQuality_Excellent:
                tips = getResources().getString(R.string.tuicalling_network_excellent_quality);
                break;
            case TCCCQuality_Good:
                tips = getResources().getString(R.string.tuicalling_network_good_quality);
                break;
            case TCCCQuality_Poor:
                tips = getResources().getString(R.string.tuicalling_network_poor_quality);
                break;
            case TCCCQuality_Bad:
                tips = getResources().getString(R.string.tuicalling_network_bad_quality);
                break;
            case TCCCQuality_Vbad:
                tips = getResources().getString(R.string.tuicalling_network_vbad_quality);
                break;
            case TCCCQuality_Down:
                tips = getResources().getString(R.string.tuicalling_network_offline_quality);
                break;
            default:
                tips = getResources().getString(R.string.tuicalling_network_excellent_quality);
                break;
        }
        mImageAvatar.setText(tips);
        mImageAvatar.setVisibility(TextUtils.isEmpty(mImageAvatar.getText()) ? GONE : VISIBLE);
    }

    @Override
    public void updateTextColor(int color) {
        super.updateTextColor(color);
        mTextUserName.setTextColor(color);
    }
}
