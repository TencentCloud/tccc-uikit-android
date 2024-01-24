package com.tencent.qcloud.tccccallkit.utils;

public class DateTimeUtil {
    private static final long minute = 60 * 1000;
    private static final long hour = 60 * minute;
    private static final long day = 24 * hour;
    private static final long week = 7 * day;
    private static final long month = 31 * day;
    private static final long year = 12 * month;

    public static String formatSecondsTo00(int timeSeconds) {
        int second = timeSeconds % 60;
        int minuteTemp = timeSeconds / 60;
        if (minuteTemp > 0) {
            int minute = minuteTemp % 60;
            int hour = minuteTemp / 60;
            if (hour > 0) {
                return (hour >= 10 ? (hour + "") : ("0" + hour)) + ":" + (minute >= 10 ? (minute + "") : ("0" + minute)) + ":"
                        + (second >= 10 ? (second + "") : ("0" + second));
            } else {
                return (minute >= 10 ? (minute + "") : ("0" + minute)) + ":" + (second >= 10 ? (second + "") : ("0" + second));
            }
        } else {
            return "00:" + (second >= 10 ? (second + "") : ("0" + second));
        }
    }
}
