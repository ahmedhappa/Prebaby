package com.usama.salamtek.Tabs;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.usama.salamtek.Dashboard.DashboardFragment;
import com.usama.salamtek.R;

public class TabsActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ViewPagerAdapter adapter;
    HomeFragment homeFragment;
    MyweekFragment myweekFragment;
    DashboardFragment dashboardFragment;
    MoreFragment moreFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        setupViewPager(viewPager);

        //Initializing the tablayout

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.getTabAt(position).select();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        homeFragment = new HomeFragment();
        myweekFragment = new MyweekFragment();
        dashboardFragment = new DashboardFragment();
        moreFragment = new MoreFragment();

        adapter.addFragment(homeFragment, "Home");
        adapter.addFragment(myweekFragment, "Myweek");
        adapter.addFragment(dashboardFragment, "Dashboard");
        adapter.addFragment(moreFragment, "More");
        viewPager.setAdapter(adapter);
    }


}
