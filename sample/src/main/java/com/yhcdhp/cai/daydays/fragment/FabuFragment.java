package com.yhcdhp.cai.daydays.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yhcdhp.cai.BaseFragment;
import com.yhcdhp.cai.R;
import com.yhcdhp.cai.daydays.PlayVideoActivity;
import com.yhcdhp.cai.daydays.view.TimeLineView;

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
    @ViewInject(R.id.timelineview)
    private TimeLineView timelineview;


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

        tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playEvent();
            }
        });


        timelineview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        timelineview.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        timelineview.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }

                return true;
            }
        });


    }

    private void playEvent() {
        Intent intent = new Intent(activity, PlayVideoActivity.class);
        activity.startActivity(intent);
    }


    @Override
    public void onDestroyView() {

        Log.i(TAG, "FabuFragment==" + "onDestroyView");
        super.onDestroyView();
    }


}
