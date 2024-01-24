package com.tencent.qcloud.tccccallkit.base;

import androidx.annotation.NonNull;

import java.util.Objects;

public class CallingUserModel {
    public String phoneNumber;
    public String remark;
    public boolean isEnter;
    public boolean isAudioAvailable;
    public int     volume;

    public CallingUserModel() {

    }
    public CallingUserModel(String id,String name) {
        this.phoneNumber = id;
        this.remark = name;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CallingUserModel model = (CallingUserModel) o;
        return Objects.equals(phoneNumber, model.phoneNumber);
    }

    @NonNull
    @Override
    public String toString() {
        return "CallingUserModel{"
                + "userId='" + phoneNumber
                + ", userName='" + remark
                + ", isEnter=" + isEnter
                + ", isAudioAvailable=" + isAudioAvailable
                + ", volume=" + volume
                + '}';
    }
}
