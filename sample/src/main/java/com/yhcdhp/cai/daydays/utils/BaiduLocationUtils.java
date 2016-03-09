package com.yhcdhp.cai.daydays.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.xutils.common.util.LogUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 百度地图的简单封装工具类
 * 需要implements或者set回调IMyBdLocationListener
 * 定位成功之后设置mIsLocationSuccess值
 * <p/>
 * 实例化的时候用getApplicationContext
 * Created by caishengyan on 2016/2/23.
 */
public class BaiduLocationUtils {
    private static BaiduLocationUtils baiduLocationUtils = null;

    private final int DEFAULT_SECONDS = 10;//默认定位计时时间
    private final int UPDATE_LOCATION = 2;//更新位置
    private Timer mTimer;//定位的定时器
    private boolean isOpenTimer = false;//是否开启定位计时
    private LocationClientOption locationClientOption = null;
    private LocationClient locationClient = null;
    private BDLocationListener baidulocaListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            if (null == locationClient) {
                return;
            }
            if (bdLocation.getLocType() == 161) {//网络定位成功
                mIsLocationSuccess = true;//控制内部的计时器
            }
            if (iMyBdLocationListener != null) {
                //对外抛出的定位接口
                iMyBdLocationListener.locationResult(bdLocation);
            }
        }
    };
    private IMyBdLocationListener iMyBdLocationListener;//封装的定位工具类对外提供的回调
    private boolean mIsLocationSuccess = false;//是否定位成功
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_LOCATION:
                    if (iMyBdLocationListener != null) {
                        iMyBdLocationListener.updateLocation();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private BaiduLocationUtils(Context context) {
        locationClient = new LocationClient(context);
        locationClientOption = getMyDefaultOption();
        locationClient.setLocOption(locationClientOption);

    }

    public static BaiduLocationUtils getInstance(Context appContext) {//getApplicationContext
        synchronized (BaiduLocationUtils.class) {
            if (null == baiduLocationUtils) {
                baiduLocationUtils = new BaiduLocationUtils(appContext);
            }
            return baiduLocationUtils;
        }
    }


    public void startLocation() {
        isOpenTimer = false;
        synchronized (BaiduLocationUtils.class) {
            if (null != locationClient) {
                if (locationClient.isStarted()) {
                    // 请求定位
                    locationClient.requestLocation();
                } else {
                    locationClient.registerLocationListener(baidulocaListener);
                    locationClient.start();
                }
            }
        }
    }

    /**
     * 规定时间内定位失败将重新定位，只会抛出重新定位的更新回调
     *
     * @param seconds
     */
    public void startLocationAndTimer(int seconds) {
        synchronized (BaiduLocationUtils.class) {
            if (null != locationClient) {
                if (locationClient.isStarted()) {
                    // 请求定位
                    locationClient.requestLocation();
                } else {
                    locationClient.registerLocationListener(baidulocaListener);
                    locationClient.start();
                }
                if (seconds < 0) {
                    seconds = 1;
                } else if (seconds > DEFAULT_SECONDS) {
                    seconds = DEFAULT_SECONDS;
                }
                TimerTask task = new TimerTask() {
                    public void run() {
                        // 在此处添加执行的代码
                        if (!mIsLocationSuccess) {
                            if (locationClient.isStarted())
                                locationClient.stop();
                            LogUtil.d("log_定位线程检测到定位失败。");
                            mHandler.sendEmptyMessage(UPDATE_LOCATION);
                        } else {
                            LogUtil.d("log_定位线程检测到定位成功。");
                        }
                    }
                };
                mTimer = new Timer();
                mTimer.schedule(task, 1000 * seconds);
                isOpenTimer = true;
            }
        }
    }

    public void stop() {
        if (locationClient != null && locationClient.isStarted()) {
            locationClient.stop();
        }

    }

    public void stopAndDestroy() {
        synchronized (BaiduLocationUtils.class) {
            if (locationClient != null && locationClient.isStarted()) {
                locationClient.stop();
                locationClient.unRegisterLocationListener(baidulocaListener);
                locationClient = null;
            }

            if (isOpenTimer) {
                if (mTimer != null) {
                    mTimer.cancel();//销毁定时器
                    mTimer = null;
                }
            }

            if (mHandler != null) {
                if (mHandler.hasMessages(UPDATE_LOCATION)) {
                    mHandler.removeMessages(UPDATE_LOCATION);
                    mHandler = null;
                }
            }
            mIsLocationSuccess = false;
        }
    }


    /**
     * 初始默认的option
     *
     * @return
     */
    private LocationClientOption getMyDefaultOption() {
        if (locationClientOption == null) {
            locationClientOption = new LocationClientOption();
            locationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            locationClientOption.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
            locationClientOption.setScanSpan(3000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
            locationClientOption.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
            locationClientOption.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
            locationClientOption.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            locationClientOption.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
            locationClientOption.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
            locationClientOption.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        }
        return locationClientOption;
    }

    public boolean setLocationClientOption(LocationClientOption option) {
        boolean isSuccess = false;
        if (option != null) {
            if (locationClient.isStarted())
                locationClient.stop();
            locationClientOption = option;
            locationClient.setLocOption(option);
        }
        return isSuccess;
    }

    public LocationClientOption getLocationClientOption() {
        return locationClientOption;
    }

    /**
     * 封装的定位工具类对外提供的回调
     */
    public interface IMyBdLocationListener {
        //对外抛出的定位结果回调
        void locationResult(BDLocation bdLocation);

        //对外抛出的更新回调，有计时器时候才有
        void updateLocation();
    }


    public IMyBdLocationListener getiMyBdLocationListener() {
        return iMyBdLocationListener;
    }

    public void setiMyBdLocationListener(IMyBdLocationListener iMyBdLocationListener) {
        this.iMyBdLocationListener = iMyBdLocationListener;
    }

    public boolean ismIsLocationSuccess() {
        return mIsLocationSuccess;
    }

    public void setmIsLocationSuccess(boolean mIsLocationSuccess) {
        this.mIsLocationSuccess = mIsLocationSuccess;
    }
}
