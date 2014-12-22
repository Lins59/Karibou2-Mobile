package com.telnet.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.telnet.karibou.FlashmailFragment;
import com.telnet.karibou.MinichatFragment;
import com.telnet.karibou.UserListFragment;

import java.util.ArrayList;
import java.util.List;

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragments;

    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fragments = new ArrayList<Fragment>();
        fragments.add(new UserListFragment());
        fragments.add(new MinichatFragment());
        fragments.add(new FlashmailFragment());
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