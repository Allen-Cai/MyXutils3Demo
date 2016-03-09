package com.yhcdhp.cai.daydays.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caishengyan on 2016/2/22.
 */
public class MineFragmentPagerAdapter extends FragmentPagerAdapter {

    private String[] mData = {"互动提醒", "我发布的", "个人信息"};//tablayout的标题

    List<Fragment> fragments = new ArrayList<>();

    public MineFragmentPagerAdapter(FragmentManager fm, List<Fragment> fg) {
        super(fm);
        fragments = fg;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mData[position];
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
