package com.example.bodify.Adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.bodify.TrackingDaysMacros.Friday;
import com.example.bodify.TrackingDaysMacros.Monday;
import com.example.bodify.TrackingDaysMacros.Remember;
import com.example.bodify.TrackingDaysMacros.Saturday;
import com.example.bodify.TrackingDaysMacros.Sunday;
import com.example.bodify.TrackingDaysMacros.Thursday;
import com.example.bodify.TrackingDaysMacros.Tuesday;
import com.example.bodify.TrackingDaysMacros.Wednesday;

import org.jetbrains.annotations.NotNull;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    Context context;
    int totalTabs;

    public ViewPagerAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                return new Remember();
        }
        return null;
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}