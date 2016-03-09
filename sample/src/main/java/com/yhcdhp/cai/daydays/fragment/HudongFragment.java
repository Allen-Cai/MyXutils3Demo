package com.yhcdhp.cai.daydays.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yhcdhp.cai.BaseFragment;
import com.yhcdhp.cai.R;
import com.yhcdhp.cai.daydays.aidl.Main;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by caishengyan on 2016/2/16.
 */
@ContentView(R.layout.fragment_channel)
public class HudongFragment extends BaseFragment {

    private static final String TAG = "xutils";

    @ViewInject(R.id.tv_name)
    private TextView tv_name;

    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtil.d(TAG + "HudongFragment==" + "onCreateView");
        context = this.getContext();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtil.d(TAG + "HudongFragment==" + "onViewCreated");
        tv_name.setText("ipc");
    }

    @Event(R.id.tv_name)
    private void TelePhoneEvent(View view) {

//        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:18731609876"));
//        startActivity(callIntent);

        try {
        /*此处为ipc通信*/
//            Intent ipcIntent = new Intent();
//            ipcIntent.setAction("net.blogjava.mobile.MYACTION");
//            ipcIntent.setData(Uri.parse("ipc://调用其他应用程序的Activity"));//格式为：ipc://
//            ipcIntent.putExtra("ipc", "==>传递成功");
//            startActivity(ipcIntent);

            Intent i = new Intent(context, Main.class);
            startActivity(i);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroyView() {

        Log.i(TAG, "onViewCreated==" + "onDestroyView");
        super.onDestroyView();
    }
}
