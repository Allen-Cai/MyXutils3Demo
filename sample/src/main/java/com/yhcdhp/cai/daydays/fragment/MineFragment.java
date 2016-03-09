package com.yhcdhp.cai.daydays.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yhcdhp.cai.BaseFragment;
import com.yhcdhp.cai.R;
import com.yhcdhp.cai.daydays.adapter.MineFragmentPagerAdapter;
import com.yhcdhp.cai.daydays.config.UMConstants;
import com.yhcdhp.cai.daydays.service.CodeService;
import com.yhcdhp.cai.daydays.utils.Utils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caishengyan on 2016/2/16.
 */
@ContentView(R.layout.fragment_mine)
public class MineFragment extends BaseFragment {

    private Activity mContext;

    private static CodeService mCodeService;

    private static final String TAG = "xutils";

    String[] titles = {"互动提醒", "我发布的", "个人信息"};


//    private String[] titles = new String[number];
//    private String[] mData = new String[number];


    @ViewInject(R.id.tablayout)
    private TabLayout mTabLayout;
    @ViewInject(R.id.viewpager)
    private ViewPager mViewPager;
    @ViewInject(R.id.mine_settings)
    private TextView mine_settings;

    private HudongFragment hudongFragment;
    private FabuFragment fabuFragment;
    private GerenFragment gerenFragment;
    private List<Fragment> mFragments = new ArrayList<>();
    private MineFragmentPagerAdapter mAdapter;
    private boolean isBind;


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = MineFragment.this.getActivity();
        Toast.makeText(mContext, "onViewCreated", Toast.LENGTH_SHORT).show();
        initFragment();

        setView();
        mine_settings.setClickable(true);
        bindMyService(mContext);
    }

    CustomServiceConnection connection = new CustomServiceConnection();

    private void bindMyService(Context con) {

        //从Lollipop开始，service服务必须采用显示方式启动。
        Intent mIntent = new Intent();
        mIntent.setAction("com.yhcdhp.cai.daydays.service.CodeService.MSG_ACTION");//你定义的service的action
        mIntent.setPackage(con.getPackageName());//这里你需要设置你应用的包名
        ComponentName name = con.startService(mIntent);

        isBind = ((Activity) con).bindService(mIntent, connection, Context.BIND_AUTO_CREATE);


    }

    private void initFragment() {
        hudongFragment = new HudongFragment();
        fabuFragment = new FabuFragment();
        gerenFragment = new GerenFragment();
        mFragments.add(hudongFragment);
        mFragments.add(fabuFragment);
        mFragments.add(gerenFragment);
    }

    private void setView() {


        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition(), false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mAdapter = new MineFragmentPagerAdapter(MineFragment.this.getFragmentManager(), mFragments);
        mTabLayout.setTabsFromPagerAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    Utils.setUMengEvent(Utils.getAppContext(), UMConstants.HUDONG_MINE_CLICK);
                } else if (position == 1) {
                    Utils.setUMengEvent(Utils.getAppContext(), UMConstants.FABU_MINE_CLICK);
                } else if (position == 2) {
                    Utils.setUMengEvent(Utils.getAppContext(), UMConstants.INFO_MINE_CLICK);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setAdapter(mAdapter);
    }

    @Event(R.id.mine_settings)
    private void getCodeEvent(View view) {
        switch (view.getId()) {
            case R.id.mine_settings:
                mine_settings.setText(120 + "");
                mCodeService.getCode();
                mine_settings.setClickable(false);
                break;
            default:
                break;

        }
    }

    private class CustomServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //获得service对象
            mCodeService = ((CodeService.CodeBinder) service).getService();
            //设置监听
            mCodeService.setOnProgressListener(new CodeService.OnProgressListener() {
                @Override
                public void onprogress(int second) {
                    Message msg = new Message();
                    msg.what = 0;
                    msg.obj = second;
                    mHandler.sendMessage(msg);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    mine_settings.setText(msg.obj + "");
                    break;
                default:
                    break;
            }

            return false;
        }
    });


    public void reSetCode() {
        mine_settings.setText("设置");
        if (null != mCodeService) {
            mCodeService.stopThread();
        }
    }


    @Override
    public void onDestroyView() {

        Log.i(TAG, "onDestroyView_4");
        super.onDestroyView();
        mContext.unbindService(connection);
    }

    public static void stopGetCode() {
        if (null != mCodeService) {
            mCodeService.stopThread();
        }
    }


}
