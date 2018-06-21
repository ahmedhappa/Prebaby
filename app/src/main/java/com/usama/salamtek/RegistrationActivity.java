package com.usama.salamtek;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    final String serverPageUrl = LoginActivity.serverIP + "addUserData.php";

    Response.Listener<String> serverResponse;
    Response.ErrorListener errorListener;
    StringRequest userDataToServer;
    RequestQueue requestQueue;

    EditText eMail, userName, age, mobile, pass, confirmPass, country, city;
    ImageView profilePic;
    Button regBtn;

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
        country = findViewById(R.id.country);
        city = findViewById(R.id.city);
        profilePic = findViewById(R.id.profilePic);
        regBtn = findViewById(R.id.regs_bun);


        regBtn.setOnClickListener(view -> {
            String sMail = eMail.getText().toString(), sUserName = userName.getText().toString(), sAge = age.getText().toString(), sMobile = mobile.getText().toString(), sPass = pass.getText().toString(), sConfirmPass = confirmPass.getText().toString(), sCountry = country.getText().toString(), sCity = city.getText().toString();

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
}
