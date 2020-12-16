package com.example.bodify;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import com.anychart.AnyChartView;
import com.example.bodify.Adapters.MyAdapter;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Breakdown extends Fragment {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String userID = mAuth.getUid();

    private final Date currentWeekDay = new Date();
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");


    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabItem tab1, tab2, tab3;
    private MyAdapter myAdapter;
    private FragmentActivity fragmentActivity;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_breakdown, container, false);
        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Mon"));
        tabLayout.addTab(tabLayout.newTab().setText("Tue"));
        tabLayout.addTab(tabLayout.newTab().setText("Wed"));
        tabLayout.addTab(tabLayout.newTab().setText("Thurs"));
        tabLayout.addTab(tabLayout.newTab().setText("Fri"));
        tabLayout.addTab(tabLayout.newTab().setText("Sat"));
        tabLayout.addTab(tabLayout.newTab().setText("Sun"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        FragmentManager fragmentManager = getChildFragmentManager();
        myAdapter = new MyAdapter(getContext(), fragmentManager, tabLayout.getTabCount());
        viewPager.setAdapter(myAdapter);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        final MyAdapter adapter = new MyAdapter(getContext(),getParentFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    return view;
    }
    @Override
    public void onAttach(@NonNull Activity activity) {
        fragmentActivity = (FragmentActivity) activity;
        super.onAttach(activity);
    }

//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_breakdown);
//
//        anyChartView = findViewById(R.id.pieChart);
//        calories = findViewById(R.id.dailyCaloriesLeftTextView);
//        fats = findViewById(R.id.dailyFatsTV);
//        proteins = findViewById(R.id.dailyProteinsTV);
//        carbohydrates = findViewById(R.id.dailyCarbsTV);
//        viewPager = findViewById(R.id.viewPager);
//        tabLayout = findViewById(R.id.tabLayout);
//        tab1 = findViewById(R.id.tab1);
//        tab2 = findViewById(R.id.tab2);
//        tab3 = findViewById(R.id.tab3);
////        tabLayout.addTab(tabLayout.newTab().setText("Mon"));
////        tabLayout.addTab(tabLayout.newTab().setText("Tue"));
////        tabLayout.addTab(tabLayout.newTab().setText("Wed"));
////        tabLayout.addTab(tabLayout.newTab().setText("Thurs"));
////        tabLayout.addTab(tabLayout.newTab().setText("Fri"));
////        tabLayout.addTab(tabLayout.newTab().setText("Sat"));
////        tabLayout.addTab(tabLayout.newTab().setText("Sun"));
//        myAdapter = new MyAdapter(getContext(), fragmentActivity.getSupportFragmentManager(), tabLayout.getTabCount());
//        viewPager.setAdapter(myAdapter);
//        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//
//
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//                if (tab.getPosition() == 0) {
//                    myAdapter.notifyDataSetChanged();
//                } else if (tab.getPosition() == 1) {
//                    myAdapter.notifyDataSetChanged();
//                } else if (tab.getPosition() == 2) {
//                    myAdapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//
//
//        });
//        //viewPager.OnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//    }

//
//

}
