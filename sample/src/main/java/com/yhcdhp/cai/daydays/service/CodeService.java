package com.yhcdhp.cai.daydays.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.xutils.common.util.LogUtil;

/**
 * Created by caishengyan on 2016/2/29.
 * 同一个进程
 */
public class CodeService extends Service {


    //当前秒数
    private int currentSecond = 120;

    /**
     * 获取当前秒数
     *
     * @return
     */
    public int getCurrentSecond() {
        return currentSecond;
    }


    private OnProgressListener onProgressListener;

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    public OnProgressListener getOnProgressListener() {
        return onProgressListener;
    }


    public class BestPractice extends Thread {
        private volatile boolean finished = false;   // ① volatile条件变量

        public void stopMe() {
            finished = true;    // ② 发出停止信号
        }

        @Override
        public void run() {
            while (!finished) {    // ③ 检测条件变量
                // do dirty work   // ④业务代码
                while (currentSecond > 0) {

                    currentSecond--;

                    if (null != onProgressListener) {
                        onProgressListener.onprogress(currentSecond);
                    }
                    LogUtil.d("倒计时=" + currentSecond);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    BestPractice thread;

    public void getCode() {
        currentSecond = 120;
        if (null == thread) {
            thread = new BestPractice();
        }
        thread.start();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new CodeBinder();
    }


    public class CodeBinder extends Binder {

        public CodeService getService() {
            return CodeService.this;
        }

    }

    /**
     * 进程接口，activity获取service之后注册监听，在回调中更新ui
     */
    public interface OnProgressListener {
        void onprogress(int second);
    }


    public void stopThread() {
        if (null != thread) {
            thread.stopMe();
            thread = null;
        }
    }

}
