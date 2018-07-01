package com.usama.salamtek.Tabs;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.usama.salamtek.EditProfileActivity;
import com.usama.salamtek.LoginActivity;
import com.usama.salamtek.Model.User;
import com.usama.salamtek.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class MoreFragment extends Fragment {


    TextView name, mobile, email, country, city, age;
    ImageView proPic;
    Button editPro, logOut;
    User user;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_more, container, false);
        name = view.findViewById(R.id.username);
        mobile = view.findViewById(R.id.tvNumber1);
        email = view.findViewById(R.id.tvNumber3);
        country = view.findViewById(R.id.tvNumber5);
        city = view.findViewById(R.id.textView);
        age = view.findViewById(R.id.age);
        proPic = view.findViewById(R.id.profile_pic);
        editPro = view.findViewById(R.id.edit_profile);
        logOut = view.findViewById(R.id.log_out);

        logOut.setOnClickListener(v -> {
            SharedPreferences keepShared = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = keepShared.edit();
            editor.putBoolean("keep_login", false);
            editor.apply();
            Intent intent2 = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent2);
            getActivity().finish();
        });

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

        Intent intent = getActivity().getIntent();
        if (intent.hasExtra("user_data")) {
            user = intent.getParcelableExtra("user_data");
            name.setText(user.getName());
            mobile.setText(user.getMobile());
            email.setText(user.getEmail());
            country.setText(user.getCountry());
            city.setText(user.getCity());
            age.setText(user.getAge());

            byte[] arr = Base64.decode(user.getImage(), Base64.DEFAULT);
            proPic.setImageBitmap(BitmapFactory.decodeByteArray(arr, 0, arr.length));

            editPro.setOnClickListener(view1 -> {
                Intent intent1 = new Intent(getActivity(), EditProfileActivity.class);
                intent1.putExtra("user_data", user);
                startActivity(intent1);
            });
        }

        return view;
    }

}
