package com.usama.salamtek.Tabs;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.usama.salamtek.R;
import com.usama.salamtek.Reminder.ReminderMainActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    ImageView reminder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        reminder = view.findViewById(R.id.newReminder);
        reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ReminderMainActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

}
