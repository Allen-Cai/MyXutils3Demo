package com.yhcdhp.cai.daydays.widget.statusBar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import com.yhcdhp.cai.R;


/**
 * Created by nan on 2015/11/30.
 */
public class ImmerseStatusBar {
    /**
     * 设置沉浸状态栏
     *
     * @param activity
     */
    public static void setImmerseStatusBar(Activity activity) {
        //当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(activity, true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
        // 激活状态栏
        tintManager.setStatusBarTintEnabled(true);
        // enable navigation bar tint 激活导航栏
        tintManager.setNavigationBarTintEnabled(true);
        //设置系统栏设置颜色
        tintManager.setTintColor(activity.getResources().getColor(R.color.title_green));
        //给状态栏设置颜色
        tintManager.setStatusBarTintResource(R.color.title_green);
        // 设置导航栏设置资源
        tintManager.setNavigationBarTintResource(R.color.title_green);
    }

    @TargetApi(19)
    private static void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 初始化状态栏 不设置
     *
     * @param activity
     */
    public static void initImmerseStatusBar(Activity activity) {
        Window win = activity.getWindow();
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  //全屏
        SystemBarTintManager mTintManager = new SystemBarTintManager(activity);
        mTintManager.setStatusBarTintEnabled(false);
        mTintManager.setNavigationBarTintEnabled(false);
        mTintManager.setTintColor(activity.getResources().getColor(R.color.c_00000000));
        mTintManager.setStatusBarTintResource(R.color.c_00000000);
        mTintManager.setNavigationBarTintResource(R.color.c_00000000);
    }
}
