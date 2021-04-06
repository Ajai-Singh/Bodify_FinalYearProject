package com.example.bodify.Adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.bodify.TrackingDaysMeals.FridayMeals;
import com.example.bodify.TrackingDaysMeals.MondayMeals;
import com.example.bodify.TrackingDaysMeals.SaturdayMeals;
import com.example.bodify.TrackingDaysMeals.SundayMeals;
import com.example.bodify.TrackingDaysMeals.ThursdayMeals;
import com.example.bodify.TrackingDaysMeals.TuesdayMeals;
import com.example.bodify.TrackingDaysMeals.WednesdayMeals;

import org.jetbrains.annotations.NotNull;

public class ViewPagerAdapterMeals extends FragmentPagerAdapter {
    private final int totalTabs;

    public ViewPagerAdapterMeals(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        this.totalTabs = totalTabs;
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MondayMeals();
            case 1:
                return new TuesdayMeals();
            case 2:
                return new WednesdayMeals();
            case 3:
                return new ThursdayMeals();
            case 4:
                return new FridayMeals();
            case 5:
                return new SaturdayMeals();
            case 6:
                return new SundayMeals();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}