package com.example.bodify.Adapters;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.example.bodify.TrackingDaysMeals.MondayMeals;
import org.jetbrains.annotations.NotNull;

public class ViewPagerAdapterMeals extends FragmentPagerAdapter {
    Context context;
    int totalTabs;
    public ViewPagerAdapterMeals(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }


    //for some reason when a meal is being saved to the db only some are saving what meal it is
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
                return new MondayMeals();
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
}