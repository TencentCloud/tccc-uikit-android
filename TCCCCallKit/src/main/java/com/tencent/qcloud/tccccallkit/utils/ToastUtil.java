package com.tencent.qcloud.tccccallkit.utils;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.qcloud.tccccallkit.internal.ServiceInitializer;

public class ToastUtil {
        private static final Handler handler = new Handler(Looper.getMainLooper());
        private static Toast toast;

        public static void toastLongMessage(final String message) {
            toastMessage(message, true, Gravity.BOTTOM);
        }

        public static void toastLongMessageCenter(final String message) {
            toastMessage(message, true, Gravity.CENTER);
        }

        public static void toastShortMessage(final String message) {
            toastMessage(message, false, Gravity.BOTTOM);
        }

        public static void toastShortMessageCenter(final String message) {
            toastMessage(message, false, Gravity.CENTER);
        }

        public static void show(final String message, boolean isLong, int gravity) {
            toastMessage(message, isLong, gravity);
        }

        private static void toastMessage(final String message, boolean isLong, int gravity) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (toast != null) {
                        toast.cancel();
                        toast = null;
                    }
                    Context context = ServiceInitializer.getAppContext();
                    if (context == null) {
                        Log.e("ToastUtil","context is null,please call TCCCCallKit.createInstance(getApplicationContext()) first");
                        return;
                    }
                    toast = Toast.makeText(context, message, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
                    toast.setGravity(gravity, 0, 0);
                    View view = toast.getView();
                    if (view != null) {
                        TextView textView = view.findViewById(android.R.id.message);
                        if (textView != null) {
                            textView.setGravity(Gravity.CENTER);
                        }
                    }
                    toast.show();
                }
            });
        }
}
