package com.yhcdhp.cai.daydays.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yhcdhp.cai.BaseFragment;
import com.yhcdhp.cai.R;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by caishengyan on 2016/2/16.
 */
@ContentView(R.layout.fragment_channel)
public class GerenFragment extends BaseFragment {

    private static final String TAG = "xutils";

    @ViewInject(R.id.tv_name)
    private TextView tv_name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtil.d(TAG + "GerenFragment==" + "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtil.d(TAG + "GerenFragment==" + "onViewCreated");
        tv_name.setText("GerenFragment");
    }

    @Override
    public void onDestroyView() {

        Log.i(TAG, "GerenFragment==" + "onDestroyView");
        super.onDestroyView();
    }
}
