package com.example.bodify.Adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.bodify.Days.Friday;
import com.example.bodify.Days.Monday;
import com.example.bodify.Days.Saturday;
import com.example.bodify.Days.Sunday;
import com.example.bodify.Days.Thursday;
import com.example.bodify.Days.Tuesday;
import com.example.bodify.Days.Wednesday;

import org.jetbrains.annotations.NotNull;

public class MyAdapter extends FragmentPagerAdapter {
    Context context;
    int totalTabs;
    public MyAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Monday();
            case 1:
                return new Tuesday();
            case 2:
                return new Wednesday();
            case 3:
                return new Thursday();
            case 4:
                return new Friday();
            case 5:
                return new Saturday();
            case 6:
                return new Sunday();
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
}