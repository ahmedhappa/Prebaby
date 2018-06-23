package com.usama.salamtek.Dashboard;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

public class ChartActionVE extends AppCompatActivity {
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

    private boolean hasLines = true;
    private boolean hasPoints = true;
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = false;
    private boolean hasLabels = true;
    private boolean isCubic = false;
    private LineChartView Chart;
    private LineChartData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_action_ve);
        Toolbar myToolbar = findViewById(R.id.chart_toolbar_ve);
        setSupportActionBar(myToolbar);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("pregnancyCurrWeek")) {
            int weekNumber = (int) intent.getLongExtra("pregnancyCurrWeek", 0);
            user = intent.getParcelableExtra("user_data");
            type = intent.getStringExtra("type");
            weeks = new ArrayList<>();
            for (int i = 1; i <= weekNumber; i++) {
                weeks.add("week" + i);
            }
            weekValue = new ArrayList<>();

            serverResponse = response -> {
                Log.i("server response", response);
                try {
                    allWeeks = new HashMap<>();
                    JSONArray jsonArray = new JSONArray(response);
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

                    Chart = findViewById(R.id.chart);
                    data = new LineChartData();


                    Chart.setInteractive(true);
                    Chart.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
                    Chart.setContainerScrollEnabled(false, ContainerScrollType.VERTICAL);

                    List<PointValue> values = new ArrayList<>();
                    float count = 0;
                    for (int i = 0; i < weekValue.size(); i++) {
                        count = (float) (count + weekValue.get(i));
                        values.add(new PointValue(i, count));
                    }

                    Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
                    line.setShape(shape);
                    line.setCubic(isCubic);
                    line.setFilled(isFilled);
                    line.setHasLabels(hasLabels);
                    line.setHasLines(hasLines);
                    line.setHasPoints(hasPoints);
                    List<Line> lines = new ArrayList<>();
                    lines.add(line);

                    Axis axisX = new Axis();
                    Axis axisY = new Axis().setHasLines(true);
                    axisX.setName("Week");
                    if (type.equals("food")) {
                        axisY.setName("Food");
                    } else if (type.equals("vitamins")) {
                        axisY.setName("vitamins");
                    }
                    data.setAxisXBottom(axisX);
                    data.setAxisYLeft(axisY);
                    data.setLines(lines);
                    Chart.setLineChartData(data);


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
        weekValue = allWeeks.get(item.getItemId());
        List<PointValue> values = new ArrayList<>();
        float count = 0;
        for (int i = 0; i < weekValue.size(); i++) {
            count = (float) (count + weekValue.get(i));
            values.add(new PointValue(i, count));
        }
        Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
        line.setShape(shape);
        line.setCubic(isCubic);
        line.setFilled(isFilled);
        line.setHasLabels(hasLabels);
        line.setHasLines(hasLines);
        line.setHasPoints(hasPoints);
        List<Line> lines = new ArrayList<>();
        lines.add(line);

        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName("Week");
        if (type.equals("food")) {
            axisY.setName("Food");
        } else if (type.equals("vitamins")) {
            axisY.setName("vitamins");
        }
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
        data.setLines(lines);
        Chart.setLineChartData(data);


        return super.onOptionsItemSelected(item);
    }
}