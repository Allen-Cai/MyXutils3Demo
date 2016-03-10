package com.yhcdhp.cai.daydays.fragment;

import android.app.Activity;
import android.media.MediaPlayer;
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
@ContentView(R.layout.fragment_fabu)
public class FabuFragment extends BaseFragment {

    private static final String TAG = "xutils";

    @ViewInject(R.id.tv_name)
    private TextView tv_name;
    @ViewInject(R.id.VideoView)
    private android.widget.VideoView VideoView;

    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtil.d(TAG + "FabuFragment==" + "onCreateView");
        activity = (Activity) FabuFragment.this.getContext();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtil.d(TAG + "FabuFragment==" + "onViewCreated");
        tv_name.setText("FabuFragment");

        VideoView.setZOrderOnTop(true);
        VideoView.setVideoPath("android.resource://" + activity.getPackageName() + "/" + "new1.3gp");
        VideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                VideoView.start();
            }
        });
    }

    @Override
    public void onDestroyView() {

        Log.i(TAG, "FabuFragment==" + "onDestroyView");
        super.onDestroyView();
    }
}
