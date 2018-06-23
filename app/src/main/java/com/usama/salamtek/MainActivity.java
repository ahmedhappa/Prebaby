package com.usama.salamtek;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.usama.salamtek.Dashboard.DashboardFragment;
import com.usama.salamtek.Interfaces.ChangeMainTabListener;
import com.usama.salamtek.Tabs.HomeFragment;
import com.usama.salamtek.Tabs.MoreFragment;
import com.usama.salamtek.Tabs.MyweekFragment;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ChangeMainTabListener {
    private TabLayout tabLayout;
    long elapsedWeeks, elapsedDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //setTitle("CompactCalendarViewToolbar");

        tabLayout = findViewById(R.id.tabslayout);

        HomeFragment homeFragment = new HomeFragment();
        MoreFragment moreFragment = new MoreFragment();
        MyweekFragment myweekFragment = new MyweekFragment();
        DashboardFragment dashboardFragment = new DashboardFragment();

        homeFragment.setListener(this);
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.tabFrame, homeFragment);
        fragmentTransaction.commit();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().toString().equals(getString(R.string.home))) {
                    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.tabFrame, homeFragment);
                    fragmentTransaction.commit();
                }
                if (tab.getText().toString().equals(getString(R.string.more))) {
                    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.tabFrame, moreFragment);
                    fragmentTransaction.commit();
                }
                if (tab.getText().toString().equals(getString(R.string.myweek))) {
                    elapsedWeeks = homeFragment.myElapsedWeeks();
                    editor.putInt("last_visited_week", (int) elapsedWeeks);
                    editor.apply();
                    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.tabFrame, myweekFragment);
                    fragmentTransaction.commit();
                }
                if (tab.getText().toString().equals(getString(R.string.dashboard))) {
                    elapsedDays = homeFragment.myElapsedDays();
                    editor.putInt("last_visited_dash", (int) elapsedDays);
                    editor.apply();
                    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.tabFrame, dashboardFragment);
                    fragmentTransaction.commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }


    @Override
    public void setTitle(CharSequence title) {
        TextView tvTitle = findViewById(R.id.title);

        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    @Override
    public void onClick(int position) {
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        if (tab != null) {
            tab.select();
        }
    }
}
