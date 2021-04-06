package com.example.bodify.Adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.bodify.TrackingDaysMacros.Friday;
import com.example.bodify.TrackingDaysMacros.Monday;
import com.example.bodify.TrackingDaysMacros.Saturday;
import com.example.bodify.TrackingDaysMacros.Sunday;
import com.example.bodify.TrackingDaysMacros.Thursday;
import com.example.bodify.TrackingDaysMacros.Tuesday;
import com.example.bodify.TrackingDaysMacros.Wednesday;

import org.jetbrains.annotations.NotNull;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final int totalTabs;

    public ViewPagerAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
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
        }
        return null;
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}