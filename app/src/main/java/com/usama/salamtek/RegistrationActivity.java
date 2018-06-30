package com.usama.salamtek;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    final String serverPageUrl = LoginActivity.serverIP + "addUserData.php";

    Response.Listener<String> serverResponse;
    Response.ErrorListener errorListener;
    StringRequest userDataToServer;
    RequestQueue requestQueue;

    EditText eMail, userName, age, mobile, pass, confirmPass, city, weightUser;
    ImageView profilePic, calendarRegist;
    Button regBtn;
    TextView dateShow;
    Spinner country_spinner;
    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;

    final int profileReqCod = 10;

    String imgAsString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        eMail = findViewById(R.id.email);
        userName = findViewById(R.id.username);
        age = findViewById(R.id.birth);
        mobile = findViewById(R.id.mobile);
        pass = findViewById(R.id.pass);
        confirmPass = findViewById(R.id.confirmPass);
        city = findViewById(R.id.city);
        profilePic = findViewById(R.id.profilePic);
        regBtn = findViewById(R.id.regs_bun);
        country_spinner = findViewById(R.id.country_spinner);
        weightUser = findViewById(R.id.weightuser);

        calendarRegist = findViewById(R.id.calendarregist);
        dateShow = findViewById(R.id.dataShow);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);
        System.out.println(dateShow.getText().toString());
        country_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ((TextView) parentView.getChildAt(0)).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        regBtn.setOnClickListener(view -> {
            String sMail = eMail.getText().toString(), sUserName = userName.getText().toString(), sAge = age.getText().toString(), sMobile = mobile.getText().toString(), sPass = pass.getText().toString(), sConfirmPass = confirmPass.getText().toString(), sCountry = country_spinner.getSelectedItem().toString(), sCity = city.getText().toString(), sDate = dateShow.getText().toString(), sWeight = weightUser.getText().toString();

            if (!(sMail.equals("") && sUserName.equals("") && sPass.equals("") && sConfirmPass.equals(""))) {
                if ((sPass.equals(sConfirmPass))) {
                    serverResponse = response -> {
                        Log.i("server response", response);
                        Toast.makeText(this, "You Signed up Successfully", Toast.LENGTH_SHORT).show();
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
                            data.put("email", sMail);
                            data.put("user_name", sUserName);
                            data.put("age", sAge);
                            data.put("mobile", sMobile);
                            data.put("pass", sPass);
                            data.put("country", sCountry);
                            data.put("city", sCity);
                            data.put("child_date_of_pregnancy", sDate);
                            data.put("weight",sWeight);
                            if (!imgAsString.equals("")) {
                                data.put("image", imgAsString);
                            }
                            return data;
                        }
                    };

                    requestQueue = Volley.newRequestQueue(RegistrationActivity.this);
                    requestQueue.add(userDataToServer);
                } else {
                    Toast.makeText(this, "Passwords Dose not match", Toast.LENGTH_SHORT).show();
                }
            }

        });

        profilePic.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, profileReqCod);
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == profileReqCod && resultCode == RESULT_OK && data != null) {
            Uri imgData = data.getData();
            Bitmap img;
            try {
                img = MediaStore.Images.Media.getBitmap(getContentResolver(), imgData);
                profilePic.setImageBitmap(img);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                img.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                byte[] arr = byteArrayOutputStream.toByteArray();
                imgAsString = Base64.encodeToString(arr, Base64.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        dateShow.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }
}
