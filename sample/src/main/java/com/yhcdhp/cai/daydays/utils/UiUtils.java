package com.yhcdhp.cai.daydays.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yhcdhp.cai.R;

/**
 * Created by caishengyan on 2016/2/1.
 */
public class UiUtils {

    private static Toast toast;

    // 屏幕高度
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static int getScreenHeight(Activity context) {

        Display display = context.getWindowManager().getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            return size.y;
        }
        return display.getHeight();
    }

    // 屏幕宽度
    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static int getScreenWidth(Activity context) {

        Display display = context.getWindowManager().getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            return size.x;
        }
        return display.getWidth();
    }

    // dp to px
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    // px to dp
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 自定义toast(1500毫秒显示 传String)
     */
    public static void showShortCustomToast(Context context, String msg) {

        try {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.custom_toast, null);
            TextView toastContent = (TextView) view.findViewById(R.id.tvTextToast);
            if (!TextUtils.isEmpty(msg)) {
                toastContent.setText(msg);
            } else {
                return;
            }
            if (toast == null) {
                toast = new Toast(context);
            }
            int marginBotton = getScreenHeight((Activity) context) / 4;
            toast.setGravity(Gravity.BOTTOM, 0, marginBotton);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(view);
            toast.show();
        } catch (Exception e) {
            if (toast != null) {
                toast.cancel();
            }
        }
    }
}
