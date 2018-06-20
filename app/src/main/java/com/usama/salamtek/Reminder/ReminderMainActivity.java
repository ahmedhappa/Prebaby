package com.usama.salamtek.Reminder;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.usama.salamtek.MainActivity;
import com.usama.salamtek.R;
import com.usama.salamtek.Tabs.HomeFragment;

public class ReminderMainActivity extends AppCompatActivity {
    private TextView mNavTitle;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private NavigationView mNavigationView;
    private View mNavHeader;
    private Toolbar mToolbar;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_main);
        mToolbar = findViewById(R.id.tool_bar);
        this.setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mFragmentManager = getSupportFragmentManager();
        Bundle args = new Bundle();
        args.putSerializable(ReminderParams.TYPE, ReminderType.ALL);
        Fragment reminderFragment = new ReminderFragment();
        reminderFragment.setArguments(args);
        mFragmentManager.beginTransaction().add(R.id.content_frame, reminderFragment).commit();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ReminderMainActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
