package com.usama.salamtek;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.usama.salamtek.Model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegistrationPlusActivity extends AppCompatActivity {
    EditText weightUserPlus;
    ImageView calendarregistPlus;
    TextView dateShowPlus;
    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;
    Button regs_bunplus;

    Response.Listener<String> serverResponse;
    Response.ErrorListener errorListener;
    StringRequest userDataToServer;
    RequestQueue requestQueue;
    final String serverPageUrl = LoginActivity.serverIP + "addUserDataWithGmail.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_plus);
        weightUserPlus = findViewById(R.id.weightuserplus);
        calendarregistPlus = findViewById(R.id.calendarregistPlus);
        dateShowPlus = findViewById(R.id.dataShowplus);
        dateShowPlus.setText("");
        regs_bunplus = findViewById(R.id.regs_bunplus);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);

        regs_bunplus.setOnClickListener(v -> {
            String wightV = weightUserPlus.getText().toString();
            String dateOFBirth = dateShowPlus.getText().toString();
            User user;
            if (!wightV.equals("") && !dateOFBirth.equals("")) {
                user = new User();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
                try {
                    Date date = simpleDateFormat.parse(dateOFBirth);
                    dateOFBirth = simpleDateFormat.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                user.setCurrWeight(wightV);
                user.setChildDateOfBirth(dateOFBirth);
                serverResponse = response -> {
                    if (response.contains("done")) {
                        Toast.makeText(this, "Thanks, You signed up successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Something went wrong please try again later", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    requestQueue.stop();
                };

                errorListener = error -> {
                    error.printStackTrace();
                    requestQueue.stop();
                };

                userDataToServer = new StringRequest(Request.Method.POST, serverPageUrl, serverResponse, errorListener) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> data = new HashMap<>();
                        data.put("wight", user.getCurrWeight());
                        data.put("date_of_birth", user.getChildDateOfBirth());
                        data.put("mail", getIntent().getStringExtra("mail"));
                        return data;
                    }
                };
                requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(userDataToServer);
            }
        });
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    showDate(arg1, arg2 + 1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        dateShowPlus.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }
}
