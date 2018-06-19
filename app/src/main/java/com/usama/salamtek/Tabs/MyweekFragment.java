package com.usama.salamtek.Tabs;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

    private final String serverPageUrl = "http://192.168.1.3:8080/Graduation_Project/getTipsData.php";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myweek, container, false);
        baby = view.findViewById(R.id.babyParagraph);
        momToBe = view.findViewById(R.id.MomParagraph);
        tipForWeek = view.findViewById(R.id.tipParagraph);
        weekImage = view.findViewById(R.id.myweek_pic);

        if (getActivity().getIntent() != null) {
            Intent intent = getActivity().getIntent();
            if (intent.hasExtra("user_data")) {
                User user = intent.getParcelableExtra("user_data");
                Date currentDate = Calendar.getInstance().getTime();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);

                try {
                    String mobileDateAsString = simpleDateFormat.format(currentDate);
                    Date mobileDate = simpleDateFormat.parse(mobileDateAsString);
                    Date childDateOfPregnancy = simpleDateFormat.parse(user.getChildDateOfBirth());
                    long dateDifference = mobileDate.getTime() - childDateOfPregnancy.getTime();
                    long daysInMilli = 1000 * 60 * 60 * 24;
                    long weeksInMilli = daysInMilli * 7;
                    elapsedWeeks = dateDifference / weeksInMilli;
                    Log.e("CurrentWeek", elapsedWeeks + "");


                } catch (ParseException e) {
                    e.printStackTrace();
                }

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
