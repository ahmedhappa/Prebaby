package com.usama.salamtek;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.usama.salamtek.Model.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    ImageView profilePic;
    ToggleButton editProPic;
    EditText name, mobile, email, country, city, age, hight, wight;
    User user;
    Button editProfileBtn;

    final int imgCode = 10;
    final String serverPageUrl = LoginActivity.serverIP + "updateUserData.php";

    Response.Listener<String> serverResponse;
    Response.ErrorListener errorListener;
    StringRequest updateUserData;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        name = findViewById(R.id.edit_username);
        mobile = findViewById(R.id.edit_mobile);
        email = findViewById(R.id.edit_email);
        country = findViewById(R.id.edit_country);
        city = findViewById(R.id.edit_city);
        age = findViewById(R.id.edit_age);
        hight = findViewById(R.id.edit_height);
        wight = findViewById(R.id.edit_weight);
        profilePic = findViewById(R.id.profile_pic_edit);
        editProPic = findViewById(R.id.editpic);
        editProfileBtn = findViewById(R.id.edit_pro_btn);

        if (getIntent() != null) {
            Intent intent = getIntent();
            if (intent.hasExtra("user_data")) {
                user = intent.getParcelableExtra("user_data");
                name.setText(user.getName());
                mobile.setText(user.getMobile());
                email.setText(user.getEmail());
                country.setText(user.getCountry());
                city.setText(user.getCity());
                age.setText(user.getAge());
                hight.setText(getString(R.string.not_available));
                wight.setText(getString(R.string.not_available));

                byte[] arr = Base64.decode(user.getImage(), Base64.DEFAULT);
                profilePic.setImageBitmap(BitmapFactory.decodeByteArray(arr, 0, arr.length));

                editProPic.setOnClickListener(view -> {
                    Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent1.setType("image/*");
                    startActivityForResult(intent1, imgCode);
                });

                serverResponse = response -> {
                    Log.i("server response", response);
                    if (!response.equals("null")) {
                        Toast.makeText(this, "Data Edited Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(this, MainActivity.class);
                        intent1.putExtra("user_data", user);
                        startActivity(intent1);
                    } else {
                        Toast.makeText(this, "There is an error pleas try again later", Toast.LENGTH_SHORT).show();
                    }
                    requestQueue.stop();
                };

                errorListener = error -> {
                    error.printStackTrace();
                    requestQueue.stop();
                };

                editProfileBtn.setOnClickListener(view -> {
                    user.setName(name.getText().toString());
                    user.setMobile(mobile.getText().toString());
                    user.setEmail(email.getText().toString());
                    user.setCountry(country.getText().toString());
                    user.setCity(city.getText().toString());
                    user.setAge(age.getText().toString());

                    updateUserData = new StringRequest(Request.Method.POST, serverPageUrl, serverResponse, errorListener) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> data = new HashMap<>();
                            data.put("email", user.getEmail());
                            data.put("user_name", user.getName());
                            data.put("age", user.getAge());
                            data.put("mobile", user.getMobile());
                            data.put("country", user.getCountry());
                            data.put("city", user.getCity());
                            data.put("image", user.getImage());
                            data.put("user_id", String.valueOf(user.getId()));
                            return data;
                        }
                    };

                    requestQueue = Volley.newRequestQueue(this);
                    requestQueue.add(updateUserData);
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == imgCode && resultCode == RESULT_OK) {
            Uri imgData = data.getData();
            Bitmap img;
            try {
                img = MediaStore.Images.Media.getBitmap(getContentResolver(), imgData);
                profilePic.setImageBitmap(img);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                img.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                byte[] arr = byteArrayOutputStream.toByteArray();
                user.setImage(Base64.encodeToString(arr, Base64.DEFAULT));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}



