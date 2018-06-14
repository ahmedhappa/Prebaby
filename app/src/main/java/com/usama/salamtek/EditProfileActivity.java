package com.usama.salamtek;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;


import java.util.Calendar;

public class EditProfileActivity extends AppCompatActivity {

    ToggleButton btn;
    private DatePickerDialog datePickerDialog;
    Calendar calendar = Calendar.getInstance();
    TextView dateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        btn = findViewById(R.id.apppintCalendar);
        dateView = findViewById(R.id.apppintment);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(EditProfileActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();
                dateView.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }


        });


    }

}



