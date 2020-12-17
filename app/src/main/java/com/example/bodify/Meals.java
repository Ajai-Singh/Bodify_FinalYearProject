package com.example.bodify;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import com.example.bodify.Adapters.ViewPagerAdapter;
import com.example.bodify.Adapters.ViewPagerAdapterMeals;
import com.google.android.material.tabs.TabLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Meals extends Fragment {
    private ViewPager viewPager;
    private String a;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_meals,container,false);
        viewPager = view.findViewById(R.id.vp);
        TabLayout tabLayout = view.findViewById(R.id.tl);
        Date currentWeekDay = new Date();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
        Log.i("TEST",simpleDateformat.format(currentWeekDay));
        ArrayList<String> days = new ArrayList<>();
        days.add("Mon");
        days.add("Tue");
        days.add("Wed");
        days.add("Thurs");
        days.add("Fri");
        days.add("Sat");
        days.add("Sun");

        tabLayout.addTab(tabLayout.newTab().setText("Mon"));
        tabLayout.addTab(tabLayout.newTab().setText("Tue"));
        tabLayout.addTab(tabLayout.newTab().setText("Wed"));
        tabLayout.addTab(tabLayout.newTab().setText("Thurs"));
        tabLayout.addTab(tabLayout.newTab().setText("Fri"));
        tabLayout.addTab(tabLayout.newTab().setText("Sat"));
        tabLayout.addTab(tabLayout.newTab().setText("Sun"));
        FragmentManager fragmentManager = getChildFragmentManager();
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getContext(), fragmentManager, tabLayout.getTabCount());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        final ViewPagerAdapterMeals viewPagerAdapterMeals = new ViewPagerAdapterMeals(getContext(), getParentFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(viewPagerAdapterMeals);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        String b = null;
        for(int i = 0; i < days.size(); i ++) {
            if(simpleDateformat.format(currentWeekDay).contains(days.get(i))) {
                a = days.get(i);
                Log.i("TEST1", String.valueOf(days.indexOf(a)));
                b = String.valueOf(days.indexOf(a));
                break;
            }
        }
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        Log.i("TEST",a);
        assert b != null;
        viewPager.setCurrentItem(Integer.parseInt(b));
        return view;
    }

}