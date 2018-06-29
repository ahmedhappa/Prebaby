package com.usama.salamtek;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.usama.salamtek.Model.User;

import java.util.HashMap;
import java.util.Map;

public class DailyQuestionsActivity extends AppCompatActivity {
    EditText water, sleep;
    RadioGroup food, vitamins;
    Button apply;
    User user;

    Response.Listener<String> serverResponse;
    Response.ErrorListener errorListener;
    StringRequest createUserDailyQuestions;
    RequestQueue requestQueue;
    private final String pageUrl = LoginActivity.serverIP + "createUserDailyQuestions.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_questions);

        water = findViewById(R.id.daily_water);
        sleep = findViewById(R.id.daily_sleep);
        food = findViewById(R.id.daily_food);
        vitamins = findViewById(R.id.daily_vitamins);
        apply = findViewById(R.id.daily_qus_apply_btn);

        Intent intent = getIntent();
        if (intent.hasExtra("user_data")) {
            user = intent.getParcelableExtra("user_data");
        }
        apply.setOnClickListener(view -> {
            String waterV = water.getText().toString();
            String sleepV = sleep.getText().toString();
            String foodV = ((RadioButton) findViewById(food.getCheckedRadioButtonId())).getText().toString();
            String foodVD = foodV.equals("Yes") ? "1" : "0";
            String vitaminsV = ((RadioButton) findViewById(vitamins.getCheckedRadioButtonId())).getText().toString();
            String vitaminsVD = vitaminsV.equals("Yes") ? "1" : "0";

            if (!waterV.equals("") && !sleepV.equals("")) {
                serverResponse = response -> {
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                    if (response.equals("done")) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt("last_visited_daily_question", (int) intent.getLongExtra("day_num", 0));
                        editor.apply();
                    }
                    requestQueue.stop();
                };

                errorListener = error -> {
                    error.printStackTrace();
                    requestQueue.stop();
                };

                createUserDailyQuestions = new StringRequest(Request.Method.POST, pageUrl, serverResponse, errorListener) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> data = new HashMap<>();
                        data.put("water", waterV);
                        data.put("sleep", sleepV);
                        data.put("food", foodVD);
                        data.put("vitamins", vitaminsVD);
                        data.put("userId", String.valueOf(user.getId()));
                        data.put("dayNum", "day" + (intent.getLongExtra("day_num", 0) - ((intent.getLongExtra("week_num", 0)-1) * 7)));
                        data.put("weekNum", String.valueOf((intent.getLongExtra("week_num", 0))));
                        return data;
                    }
                };

                requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(createUserDailyQuestions);

            } else {
                Toast.makeText(this, "Pleas enter all required data", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
