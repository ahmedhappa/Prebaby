package com.usama.salamtek.Dashboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.usama.salamtek.LoginActivity;
import com.usama.salamtek.Model.User;
import com.usama.salamtek.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

public class ChartAction extends AppCompatActivity {
    Response.Listener<String> serverResponse;
    Response.ErrorListener errorListener;
    StringRequest getReportData;
    RequestQueue requestQueue;
    final String serverPageUrl = LoginActivity.serverIP + "reportData.php";

    User user;
    List<String> weeks;
    List<Double> weekValue;
    Map<Integer, List<Double>> allWeeks;
    String type = "";
    private PieChartView chart;
    private PieChartData data;
    private boolean hasLabels = false;
    private boolean hasLabelsOutside = false;
    private boolean hasCenterCircle = false;
    private boolean hasLabelForSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_action);

        chart = findViewById(R.id.pie_chart);
        chart.setOnValueTouchListener(new ValueTouchListener());
        chart.setCircleFillRatio(1.0f);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("pregnancyCurrWeek")) {
            int weekNumber = (int) intent.getLongExtra("pregnancyCurrWeek", 0);
            user = intent.getParcelableExtra("user_data");
            type = intent.getStringExtra("type");
            weeks = new ArrayList<>();
            weekValue = new ArrayList<>();

            serverResponse = response -> {
                Log.i("server response", response);
                try {
                    allWeeks = new HashMap<>();
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i <= jsonArray.length(); i=i+4) {
                        weeks.add("week" + jsonArray.getJSONObject(i).getString("weekNumber"));
                    }
                    Toolbar myToolbar = findViewById(R.id.chart_toolbar);
                    setSupportActionBar(myToolbar);
                    JSONObject jsonObject = jsonArray.getJSONObject(jsonArray.length() - 1);
                    JSONArray daysData = jsonObject.getJSONArray(weeks.get(weeks.size() - 1));
                    for (int i = 0; i < daysData.length(); i++) {
                        weekValue.add(daysData.getDouble(i));
                    }

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonAllObjects = jsonArray.getJSONObject(i);
                        JSONArray allWeeksData = jsonAllObjects.getJSONArray(weeks.get(i));
                        List<Double> weekData = new ArrayList<>();
                        for (int j = 0; j < allWeeksData.length(); j++) {
                            weekData.add(allWeeksData.getDouble(j));
                        }
                        allWeeks.put(i, weekData);
                    }

                    List<SliceValue> values = new ArrayList<>();
                    for (int i = 0; i < weekValue.size(); ++i) {
                        SliceValue sliceValue = new SliceValue((float) (weekValue.get(i) * 1), ChartUtils.pickColor());
                        values.add(sliceValue);
                    }
                    data = new PieChartData(values);
                    data.setHasLabels(hasLabels);
                    data.setHasLabelsOnlyForSelected(hasLabelForSelected);
                    data.setHasLabelsOutside(hasLabelsOutside);
                    data.setHasCenterCircle(hasCenterCircle);
                    chart.setPieChartData(data);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                requestQueue.stop();
            };
            errorListener = error -> {
                error.printStackTrace();
                requestQueue.stop();
            };

            getReportData = new StringRequest(Request.Method.POST, serverPageUrl, serverResponse, errorListener) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> data = new HashMap<>();
                    data.put("user_id", user.getId() + "");
                    data.put("type", type);
                    return data;
                }
            };

            requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(getReportData);


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chart_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        for (int i = 0; i < weeks.size(); i++) {
            menu.add(0, i, Menu.NONE, weeks.get(i));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        weekValue= allWeeks.get(item.getItemId());
        chart.setOnValueTouchListener(new ValueTouchListener());
        chart.setCircleFillRatio(1.0f);
        hasLabels = false;
        hasLabelsOutside = false;
        hasCenterCircle = false;
        hasLabelForSelected = false;


        List<SliceValue> values = new ArrayList<SliceValue>();
        for (int i = 0; i < weekValue.size(); ++i) {
            SliceValue sliceValue = new SliceValue((float) (weekValue.get(i) * 1), ChartUtils.pickColor());
            values.add(sliceValue);
        }
        data = new PieChartData(values);
        data.setHasLabels(hasLabels);
        data.setHasLabelsOnlyForSelected(hasLabelForSelected);
        data.setHasLabelsOutside(hasLabelsOutside);
        data.setHasCenterCircle(hasCenterCircle);
        chart.setPieChartData(data);


        return super.onOptionsItemSelected(item);
    }

    private class ValueTouchListener implements PieChartOnValueSelectListener {

        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
            if (type.equals("Water")) {
                Toast.makeText(getBaseContext(), "Day" + (arcIndex + 1) + ":  " + weekValue.get(arcIndex) + "Liters", Toast.LENGTH_SHORT).show();
            } else if (type.equals("sleep")) {
                Toast.makeText(getBaseContext(), "Day" + (arcIndex + 1) + ":  " + weekValue.get(arcIndex) + "Hours", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onValueDeselected() {
        }

    }

}
