package com.usama.salamtek.Tabs;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.usama.salamtek.Community.CommunityMainActivity;
import com.usama.salamtek.DailyQuestionsActivity;
import com.usama.salamtek.Dashboard.DashboardFragment;
import com.usama.salamtek.Interfaces.ChangeMainTabListener;
import com.usama.salamtek.LoginActivity;
import com.usama.salamtek.Model.User;
import com.usama.salamtek.QuestionsActivity;
import com.usama.salamtek.R;
import com.usama.salamtek.Reminder.ReminderMainActivity;
import com.usama.salamtek.Services.MyJobServiceNotification;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    ImageView reminder;
    User user;
    TextView daysCount, weekNotiNum, dashNotiNum, qusNotiNum, weekNum, dayTip1, dailyquestionNotifi_num, art1, art2,weekly_reports;
    private Response.Listener<String> serverResponse;
    private Response.ErrorListener errorListener;
    private StringRequest getBabyInfo;
    private RequestQueue requestQueue;
    RelativeLayout weekNoti, dashNoti, qusNoti, my_dailyqus_notifi;
    ChangeMainTabListener changeMainTabListener;
    long elapsedWeeks, elapsedDays;
    ImageView communityIcon;

    ProgressDialog progressDialog;

    private final String serverPageUrl = LoginActivity.serverIP + "babyInfo.php";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        daysCount = view.findViewById(R.id.countDay);
        reminder = view.findViewById(R.id.newReminder);
        weekNoti = view.findViewById(R.id.my_week_notifi);
        dashNoti = view.findViewById(R.id.my_dash_norifi);
        qusNoti = view.findViewById(R.id.my_qus_notifi);
        weekNotiNum = view.findViewById(R.id.weekNotifi_num);
        dashNotiNum = view.findViewById(R.id.dashNotifi_num);
        qusNotiNum = view.findViewById(R.id.questionNotifi_num);
        weekNum = view.findViewById(R.id.weeknum);
        dayTip1 = view.findViewById(R.id.tip1);
        communityIcon = view.findViewById(R.id.communityIcon);
        my_dailyqus_notifi = view.findViewById(R.id.my_dailyqus_notifi);
        dailyquestionNotifi_num = view.findViewById(R.id.dailyquestionNotifi_num);
        art1 = view.findViewById(R.id.art1);
        art2 = view.findViewById(R.id.art2);
        weekly_reports = view.findViewById(R.id.weekly_reports);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading ... ");
        progressDialog.show();

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.toolbar_layout);
        AppBarLayout appBarLayout = (AppBarLayout) view.findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("PreBaby");
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });

        reminder.setOnClickListener(view1 -> {
            Intent intent3 = new Intent(getActivity(), ReminderMainActivity.class);
            startActivity(intent3);
        });

        art1.setOnClickListener(v -> {
            Intent intent4 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.1.3:8085/first"));
            startActivity(intent4);

        });

        art2.setOnClickListener(v -> {
            Intent intent5 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.1.3:8085/second"));
            startActivity(intent5);

        });

        if (getActivity().getIntent() != null) {
            Intent intent = getActivity().getIntent();
            if (intent.hasExtra("user_data")) {
                user = intent.getParcelableExtra("user_data");


                communityIcon.setOnClickListener(view1 -> {
                    Intent myIntent = new Intent(getActivity(), CommunityMainActivity.class);
                    myIntent.putExtra("user_data", user);
                    startActivity(myIntent);
                });

                Date currentDate = Calendar.getInstance().getTime();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
                try {
                    String mobileDateAsString = simpleDateFormat.format(currentDate);
                    Date mobileDate = simpleDateFormat.parse(mobileDateAsString);
                    Date childDateOfPregnancy = simpleDateFormat.parse(user.getChildDateOfBirth());
                    long dateDifference = mobileDate.getTime() - childDateOfPregnancy.getTime();
                    long daysInMilli = 1000 * 60 * 60 * 24;
                    elapsedDays = (dateDifference / daysInMilli) + 1;
                    Log.i("CurrentDay", elapsedDays + "");
                    long weeksInMilli = daysInMilli * 7;
                    elapsedWeeks = (dateDifference / weeksInMilli) + 1;
                    intent.putExtra("pregnancyCurrWeek", elapsedWeeks);
                    Log.i("CurrentWeek", elapsedWeeks + "");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(childDateOfPregnancy);
                    calendar.add(Calendar.MONTH, 9);
                    Date childPregnancyDateAfterNineMonthes = calendar.getTime();
                    long difference = childPregnancyDateAfterNineMonthes.getTime() - currentDate.getTime();
                    long elapsedRemainingPregnancyDays = difference / daysInMilli;
                    String expcetedDays = "Remaining days : " + String.valueOf(elapsedRemainingPregnancyDays);
                    daysCount.setText(expcetedDays);
                    String curWeekNumber = "Week Number : " + String.valueOf(elapsedWeeks);
                    weekNum.setText(curWeekNumber);


                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    int lastVisitedWeek = sharedPreferences.getInt("last_visited_week", 0);
                    int lastVisitedDash = sharedPreferences.getInt("last_visited_dash", 0);
                    int lastVisitedQuestion = sharedPreferences.getInt("last_visited_question", 0);
                    int lastVisitedDailyQuestion = sharedPreferences.getInt("last_visited_daily_question", 0);

                    int unAnsweredQuestiuns = ((int) elapsedWeeks - lastVisitedQuestion) * 15;
                    int unAnsweredDailyQuestiuns = (int) elapsedDays - lastVisitedDailyQuestion;
                    weekNotiNum.setText(String.valueOf((int) elapsedWeeks - lastVisitedWeek));
                    dashNotiNum.setText(String.valueOf((int) elapsedDays - lastVisitedDash));
                    qusNotiNum.setText(String.valueOf(unAnsweredQuestiuns));
                    dailyquestionNotifi_num.setText(String.valueOf(unAnsweredDailyQuestiuns));

//                    if (!sharedPreferences.contains("is_job_running")) {
//                        SharedPreferences.Editor notiEditor1 = sharedPreferences.edit();
//                        notiEditor1.putBoolean("is_job_running", true);
//                        notiEditor1.apply();
//                        int numberOfSecounds = 7 * 24 * 60 * 60;
//                        String jobTag = "notification_job";
//                        Driver driver = new GooglePlayDriver(getActivity());
//                        FirebaseJobDispatcher jobDispatcher = new FirebaseJobDispatcher(driver);
//                        Job job = jobDispatcher.newJobBuilder()
//                                .setService(MyJobServiceNotification.class)
//                                .setTag(jobTag)
//                                .setLifetime(Lifetime.FOREVER)
//                                .setRecurring(true)
//                                .setTrigger(Trigger.executionWindow(numberOfSecounds, numberOfSecounds + 10))
//                                .setReplaceCurrent(true)
//                                .build();

//                        int numberOfDailySecounds = 24 * 60 * 60;
//                        Job jobDailyQus = jobDispatcher.newJobBuilder()
//                                .setService(MyJobServiceNotification.class)
//                                .setTag(jobTag)
//                                .setLifetime(Lifetime.FOREVER)
//                                .setRecurring(true)
//                                .setTrigger(Trigger.executionWindow(numberOfDailySecounds, numberOfDailySecounds + 10))
//                                .setReplaceCurrent(true)
//                                .build();
//                        jobDispatcher.schedule(jobDailyQus);
//                    }


                    weekNoti.setOnClickListener(view1 -> {
                        changeMainTabListener.onClick(1);
                        editor.putInt("last_visited_week", (int) elapsedWeeks);
                        editor.apply();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.tabFrame, new MyweekFragment());
                        fragmentTransaction.commit();
                    });
                    dashNoti.setOnClickListener(view1 -> {
                        changeMainTabListener.onClick(2);
                        editor.putInt("last_visited_dash", (int) elapsedDays);
                        editor.apply();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.tabFrame, new DashboardFragment());
                        fragmentTransaction.commit();
                    });

                    qusNoti.setOnClickListener(view1 -> {
                        if (unAnsweredQuestiuns != 0) {
                            Intent qustionIntent = new Intent(getActivity(), QuestionsActivity.class);
                            qustionIntent.putExtra("user_data", user);
                            qustionIntent.putExtra("week_num", elapsedWeeks);
                            qustionIntent.putExtra("pregnancyMonth", (int) (elapsedDays / 30) + 1);
                            qustionIntent.putExtra("weeklyQuestions", true);
                            startActivity(qustionIntent);
                        } else {
                            Toast.makeText(getActivity(), "You Answered all questions please wait for the next week", Toast.LENGTH_SHORT).show();
                        }
                    });

                    my_dailyqus_notifi.setOnClickListener(view1 -> {
                        if (unAnsweredDailyQuestiuns != 0) {
                            Intent dailQqustionIntent = new Intent(getActivity(), DailyQuestionsActivity.class);
                            dailQqustionIntent.putExtra("user_data", user);
                            dailQqustionIntent.putExtra("week_num", elapsedWeeks);
                            dailQqustionIntent.putExtra("day_num", elapsedDays);
                            startActivity(dailQqustionIntent);
                        } else {
                            Toast.makeText(getActivity(), "You Answered all daily questions please wait for the next day", Toast.LENGTH_SHORT).show();
                        }
                    });

                    weekly_reports.setOnClickListener(v -> {
                        Intent qustionIntent = new Intent(getActivity(), QuestionsActivity.class);
                        qustionIntent.putExtra("user_data", user);
                        qustionIntent.putExtra("week_num", elapsedWeeks);
                        qustionIntent.putExtra("pregnancyMonth", (int) (elapsedDays / 30) + 1);
                        startActivity(qustionIntent);
                    });


                    serverResponse = response -> {
                        Log.i("server response", response);
                        progressDialog.dismiss();
                        if (!response.equals("null")) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject weekData = jsonObject.getJSONObject("week_data");
                                JSONObject dayData = jsonObject.getJSONObject("day_data");
                                dayTip1.setText(dayData.getString("tip_e1"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        requestQueue.stop();
                    };

                    errorListener = error -> {
                        error.printStackTrace();
                        requestQueue.stop();
                    };

                    getBabyInfo = new StringRequest(Request.Method.POST, serverPageUrl, serverResponse, errorListener) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> data = new HashMap<>();
                            data.put("week_num", String.valueOf(elapsedWeeks));
                            data.put("day_num", String.valueOf(elapsedDays));
                            return data;
                        }
                    };
                    requestQueue = Volley.newRequestQueue(getActivity());
                    requestQueue.add(getBabyInfo);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }

        return view;
    }

    public void setListener(ChangeMainTabListener listener) {
        this.changeMainTabListener = listener;
    }

    public long myElapsedDays() {
        return elapsedDays;
    }

    public long myElapsedWeeks() {
        return elapsedWeeks;
    }

}
