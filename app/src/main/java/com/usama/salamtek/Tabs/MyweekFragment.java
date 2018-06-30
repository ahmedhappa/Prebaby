package com.usama.salamtek.Tabs;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.usama.salamtek.LoginActivity;
import com.usama.salamtek.Model.User;
import com.usama.salamtek.R;

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
public class MyweekFragment extends Fragment {
    TextView baby, momToBe, tipForWeek;
    Response.Listener<String> serverResponse;
    Response.ErrorListener errorListener;
    StringRequest getTipsData;
    RequestQueue requestQueue;
    long elapsedWeeks;
    ImageView weekImage;

    private final String serverPageUrl = LoginActivity.serverIP + "getTipsData.php";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myweek, container, false);
        baby = view.findViewById(R.id.babyParagraph);
        momToBe = view.findViewById(R.id.MomParagraph);
        tipForWeek = view.findViewById(R.id.tipParagraph);
        weekImage = view.findViewById(R.id.myweek_pic);

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

        if (getActivity().getIntent() != null) {
            Intent intent = getActivity().getIntent();
            if (intent.hasExtra("user_data")) {
                elapsedWeeks = intent.getLongExtra("pregnancyCurrWeek", 1);
                Log.i("currentWeek", elapsedWeeks + "");
                serverResponse = response -> {
                    if (!response.equals("null")) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            baby.setText(jsonObject.getString("baby"));
                            momToBe.setText(jsonObject.getString("mom_to_be"));
                            tipForWeek.setText(jsonObject.getString("tip_for_week"));
                            byte[] arr = Base64.decode(jsonObject.getString("week_image"), Base64.DEFAULT);
                            Bitmap img = BitmapFactory.decodeByteArray(arr, 0, arr.length);
                            weekImage.setImageBitmap(img);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    requestQueue.stop();
                };

                errorListener = error -> {
                    error.printStackTrace();
                    requestQueue.stop();
                };

                getTipsData = new StringRequest(Request.Method.POST, serverPageUrl, serverResponse, errorListener) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> data = new HashMap<>();
                        data.put("week_num", String.valueOf(elapsedWeeks));
                        return data;
                    }
                };

                requestQueue = Volley.newRequestQueue(getActivity());
                requestQueue.add(getTipsData);
            }
        }

        return view;
    }

}
