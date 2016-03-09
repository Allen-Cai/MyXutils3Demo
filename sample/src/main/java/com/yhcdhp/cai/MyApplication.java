package com.yhcdhp.cai;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.umeng.analytics.MobclickAgent;
import com.yhcdhp.cai.daydays.config.AppEnv;

import org.xutils.x;

/**
 * Created by wyouflf on 15/10/28.
 */
public class MyApplication extends Application {

    public static MyApplication mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        x.Ext.init(this);
        x.Ext.setDebug(AppEnv.DEBUG);

        /*
         * 初始化umeng
         */
        MobclickAgent.openActivityDurationTrack(true);
        //测试
        MobclickAgent.setDebugMode(AppEnv.DEBUG);

        /*百度地图初始化*/
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());

    }
}
