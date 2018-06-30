package com.usama.salamtek;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.usama.salamtek.TakeTour.TakeTourActivity;

public class FirstActivity extends AppCompatActivity {
    Button goToLogin, tour;
    TextView goToRegestiration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        goToLogin = findViewById(R.id.go_to_login_btn);
        goToRegestiration = findViewById(R.id.go_to_sign_up);
        tour=findViewById(R.id.tour);

        goToLogin.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

        goToRegestiration.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegistrationActivity.class);
            startActivity(intent);
        });
        tour.setOnClickListener(view -> {
            Intent intent=new Intent(this, TakeTourActivity.class);
            startActivity(intent);
        });
    }
}
