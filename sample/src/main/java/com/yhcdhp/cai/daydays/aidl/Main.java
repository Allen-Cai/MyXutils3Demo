package com.yhcdhp.cai.daydays.aidl;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.yhcdhp.cai.R;

/**
 * aidl的测试页
 * Created by caishengyan on 2016/3/1.
 */
public class Main extends Activity implements View.OnClickListener {
    private Button btn_aidl;
    private Button btn_aidl_unbind;
    private Button btn_date_bind;
    private Button btn_get_date;

    private Button btn_date_unbind;
    private Activity act;

    private IMyService myService = null;

    private IDateService dateService = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;
        setContentView(R.layout.activity_main_aidl);
        btn_aidl = (Button) findViewById(R.id.btn_aidl);
        btn_aidl_unbind = (Button) findViewById(R.id.btn_aidl_unbind);
        btn_date_bind = (Button) findViewById(R.id.btn_date_bind);
        btn_get_date = (Button) findViewById(R.id.btn_get_date);
        btn_date_unbind = (Button) findViewById(R.id.btn_date_unbind);
        btn_aidl.setOnClickListener(this);
        btn_aidl_unbind.setOnClickListener(this);
        btn_date_bind.setOnClickListener(this);
        btn_get_date.setOnClickListener(this);
        btn_date_unbind.setOnClickListener(this);
    }

    //  创建ServiceConnection对象
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 获得AIDL服务对象
            myService = IMyService.Stub.asInterface(service);

            try {
                //  调用AIDL服务对象中的getValue方法，并以对话框中显示该方法的返回值
                new AlertDialog.Builder(Main.this).setMessage(
                        myService.getValue()).setPositiveButton("确定", null)
                        .show();
            } catch (Exception e) {
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //调用时机是当Service服务被异外销毁时，例如内存的资源不足时这个...
        }
    };


    private ServiceConnection serviceConnection222 = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            dateService = IDateService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //调用时机是当Service服务被异外销毁时，例如内存的资源不足时这个...
        }
    };


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_aidl:
                //  绑定AIDL服务
                Intent intent = new Intent();
                intent.setAction("net.blogjava.mobile.aidlservice.IMyService");
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
                break;
            case R.id.btn_aidl_unbind:
                unbindService(serviceConnection);
                Toast.makeText(act, "已经解绑", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_date_bind:
                bindService(new Intent("com.date.aidl.service"), serviceConnection222, Context.BIND_AUTO_CREATE);
                break;
            case R.id.btn_get_date:
                if (null != dateService) {
                    try {
                        String date = dateService.getDate();
                        Toast.makeText(act, "现在时间：" + date, Toast.LENGTH_SHORT).show();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_date_unbind:
                unbindService(serviceConnection222);
                Toast.makeText(act, "已经解绑", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

    }


}
