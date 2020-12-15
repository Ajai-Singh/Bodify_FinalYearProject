package com.example.bodify.Adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.bodify.Breakdown;

public class MyAdapter extends FragmentPagerAdapter {
    Context context;
    int totalTabs;
    public MyAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Breakdown();
            case 1:
                return new Breakdown();
            case 2:
                return new Breakdown();
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
}