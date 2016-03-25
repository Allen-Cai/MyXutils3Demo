package com.yhcdhp.cai.daydays;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UpdateStatus;
import com.yhcdhp.cai.BaseActivity;
import com.yhcdhp.cai.R;
import com.yhcdhp.cai.daydays.config.UMConstants;
import com.yhcdhp.cai.daydays.fragment.ChannelFragment;
import com.yhcdhp.cai.daydays.fragment.DiscoverFragment;
import com.yhcdhp.cai.daydays.fragment.MainFragment;
import com.yhcdhp.cai.daydays.fragment.MineFragment;
import com.yhcdhp.cai.daydays.utils.Utils;
import com.yhcdhp.cai.daydays.widget.FragmentTabHost;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by caishengyan on 2016/2/16.
 */
@ContentView(R.layout.activity_main_tab)
public class MainTabActivity extends BaseActivity implements FragmentTabHost.OnTabChangeListener {

    @ViewInject(android.R.id.tabhost)
    private FragmentTabHost mFragmentTabHost;

    private LayoutInflater mLayoutInflater;

    /**
     * fragments
     */
    private Class mFragmentArray[] = {MainFragment.class, ChannelFragment.class, DiscoverFragment.class, MineFragment.class};
    /**
     * 导航标题
     */
    private String titles[] = {"主页", "频道", "发现", "我的"};
    /**
     * 导航图片
     */
    private int[] mImageArray = {
            R.drawable.tab_home_btn,
            R.drawable.tab_channel_btn,
            R.drawable.tab_discover_btn,
            R.drawable.tab_mine_btn
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //任意网络环境都可以检测更新
//        UmengUpdateAgent.setUpdateOnlyWifi(false);
//重置
        UmengUpdateAgent.setDefault();
        //设置更新样式，通知或者dialog
        UmengUpdateAgent.setUpdateUIStyle(UpdateStatus.STYLE_NOTIFICATION);
        //自动更新
        UmengUpdateAgent.update(this);
        //手动更新
//        UmengUpdateAgent.forceUpdate(this);
        //静默更新
//        UmengUpdateAgent.silentUpdate(this);

        initView();

    }

    private void initView() {
        mLayoutInflater = LayoutInflater.from(this);
        mFragmentTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

// 得到fragment的个数
        int count = mFragmentArray.length;
        for (int i = 0; i < count; i++) {
            // 给每个Tab按钮设置图标、文字和内容
            TabSpec tabSpec = mFragmentTabHost.newTabSpec(titles[i])
                    .setIndicator(getTabItemView(i));
            // 将Tab按钮添加进Tab选项卡中
            mFragmentTabHost.addTab(tabSpec, mFragmentArray[i], null);
            // 设置Tab按钮的背景
//            mFragmentTabHost.getTabWidget().getChildAt(i)
//                    .setBackgroundResource(R.drawable.selector_tab_background);

            //设置Tab按钮的分割线
            mFragmentTabHost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
            mFragmentTabHost.setOnTabChangedListener(this);
        }
    }


    /**
     * 给每个Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {
        View view = mLayoutInflater.inflate(R.layout.tab_item_view, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageArray[index]);
        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(titles[index]);

        return view;
    }

    @Override
    public void onTabChanged(String tabId) {
        if (titles[0].equals(tabId)) {
            Utils.setUMengEvent(getApplicationContext(), UMConstants.APP_MAIN_CLICK);
            MineFragment.stopGetCode();
        } else if (titles[1].equals(tabId)) {
            Utils.setUMengEvent(getApplicationContext(), UMConstants.APP_CHANNEL_CLICK);
            MineFragment.stopGetCode();
        } else if (titles[2].equals(tabId)) {
            Utils.setUMengEvent(getApplicationContext(), UMConstants.APP_DISCOVER_CLICK);
            MineFragment.stopGetCode();
        } else if (titles[3].equals(tabId)) {
            Utils.setUMengEvent(getApplicationContext(), UMConstants.APP_MINE_CLICK);

        }

        Toast.makeText(getApplicationContext(), tabId, Toast.LENGTH_SHORT).show();
    }
}
