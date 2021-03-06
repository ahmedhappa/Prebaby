package com.usama.salamtek;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.usama.salamtek.Model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private Response.ErrorListener errorListener;
    private Response.Listener<String> serverResponse;
    private StringRequest loginData;
    private RequestQueue requestQueue;

    TextView userName, pass;
    Button login;
    ImageButton gMailLogin;
    CheckBox keepLogin;

    public static final String serverIP = "http://192.168.1.3:8085/Graduation_Project/";
    private final String serverPageUrl = serverIP + "getUserData.php";

    private GoogleSignInClient mGoogleSignInClient;
    private static final int signInRequstCode = 50;

    String sUserName, sPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userName = findViewById(R.id.username);
        pass = findViewById(R.id.login_pass);
        login = findViewById(R.id.login_btn);
        gMailLogin = findViewById(R.id.gmail_login);
        keepLogin = findViewById(R.id.keep_login);

        SharedPreferences checkSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        serverResponse = response -> {
            if (!response.equals("null")) {
                Log.i("Server Response", response);
                setWeekAndDay();
                Intent intent = new Intent(this, MainActivity.class);
                User user = new User();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    user.setName(jsonObject.getString("user_name"));
                    user.setAge(jsonObject.getString("age"));
                    user.setEmail(jsonObject.getString("email"));
                    user.setMobile(jsonObject.getString("mobile"));
                    user.setPass(jsonObject.getString("pass"));
                    user.setCountry(jsonObject.getString("country"));
                    user.setCity(jsonObject.getString("city"));
                    user.setImage(jsonObject.getString("image"));
                    user.setId(Integer.parseInt(jsonObject.getString("user_id")));
                    user.setChildDateOfBirth(jsonObject.getString("child_date_of_pregnancy"));
                    user.setCurrWeight(jsonObject.getString("wight"));
                    intent.putExtra("user_data", user);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Wrong User Name Or Password", Toast.LENGTH_SHORT).show();
            }
            requestQueue.stop();
        };

        errorListener = error -> {
            error.printStackTrace();
            requestQueue.stop();
        };

        loginData = new StringRequest(Request.Method.POST, serverPageUrl, serverResponse, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> data = new HashMap<>();
                data.put("user_name", checkSharedPreferences.getString("curr_user_name_k", ""));
                data.put("pass", checkSharedPreferences.getString("curr_user_pass_k", ""));
                return data;
            }
        };

        boolean keepMeLogin = checkSharedPreferences.getBoolean("keep_login", false);
        if (keepMeLogin) {
            requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(loginData);
        }

        login.setOnClickListener(view -> {
            String sUserName = userName.getText().toString(), sPass = pass.getText().toString();
            if (!(sUserName.equals("") && sPass.equals(""))) {

                serverResponse = response -> {
                    if (!response.equals("null")) {
                        Log.i("Server Response", response);
                        setWeekAndDay();
                        Intent intent = new Intent(this, MainActivity.class);
                        User user = new User();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            user.setName(jsonObject.getString("user_name"));
                            user.setAge(jsonObject.getString("age"));
                            user.setEmail(jsonObject.getString("email"));
                            user.setMobile(jsonObject.getString("mobile"));
                            user.setPass(jsonObject.getString("pass"));
                            user.setCountry(jsonObject.getString("country"));
                            user.setCity(jsonObject.getString("city"));
                            user.setImage(jsonObject.getString("image"));
                            user.setId(Integer.parseInt(jsonObject.getString("user_id")));
                            user.setChildDateOfBirth(jsonObject.getString("child_date_of_pregnancy"));
                            user.setCurrWeight(jsonObject.getString("wight"));
                            intent.putExtra("user_data", user);
                            if (keepLogin.isChecked()) {
                                SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                                SharedPreferences.Editor editor = mySharedPreferences.edit();
                                editor.putBoolean("keep_login", true);
                                editor.putString("curr_user_name_k", sUserName);
                                editor.putString("curr_user_pass_k", sPass);
                                editor.apply();
                            }
                            SharedPreferences sharedPre = PreferenceManager.getDefaultSharedPreferences(this);
                            int cuurUserUd = sharedPre.getInt("app_curr_user", 0);
                            if (cuurUserUd != user.getId()) {
                                SharedPreferences.Editor editor = sharedPre.edit();
                                editor.putInt("app_curr_user",user.getId());
                                editor.putInt("last_visited_week", 0);
                                editor.putInt("last_visited_dash", 0);
                                editor.putInt("last_visited_question", 0);
                                editor.putInt("last_visited_daily_question", 0);
                                editor.apply();
                            }
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(this, "Wrong User Name Or Password", Toast.LENGTH_SHORT).show();
                    }
                    requestQueue.stop();
                };

                loginData = new StringRequest(Request.Method.POST, serverPageUrl, serverResponse, errorListener) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> data = new HashMap<>();
                        data.put("user_name", sUserName);
                        data.put("pass", sPass);
                        return data;
                    }
                };


                requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(loginData);
            } else {
                Toast.makeText(this, "Please Enter UserName and Password", Toast.LENGTH_SHORT).show();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        gMailLogin.setOnClickListener(view -> {
            setWeekAndDay();
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, signInRequstCode);
        });

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == signInRequstCode) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.i("user email", account.getEmail());

                serverResponse = response -> {
                    if (!response.contains("null")) {
                        Log.i("Server Response", response);
                        setWeekAndDay();
                        Intent intent = new Intent(this, MainActivity.class);
                        User user = new User();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            user.setName(jsonObject.getString("user_name"));
                            user.setAge(jsonObject.getString("age"));
                            user.setEmail(jsonObject.getString("email"));
                            user.setMobile(jsonObject.getString("mobile"));
                            user.setPass(jsonObject.getString("pass"));
                            user.setCountry(jsonObject.getString("country"));
                            user.setCity(jsonObject.getString("city"));
                            user.setImage(jsonObject.getString("image"));
                            user.setId(Integer.parseInt(jsonObject.getString("user_id")));
                            user.setChildDateOfBirth(jsonObject.getString("child_date_of_pregnancy"));
                            user.setCurrWeight(jsonObject.getString("wight"));
                            intent.putExtra("user_data", user);
                            if (keepLogin.isChecked()) {
                                SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                                SharedPreferences.Editor editor = mySharedPreferences.edit();
                                editor.putBoolean("keep_login", true);
                                editor.putString("curr_user_name_k", sUserName);
                                editor.putString("curr_user_pass_k", sPass);
                                editor.apply();
                            }
                            SharedPreferences sharedPre = PreferenceManager.getDefaultSharedPreferences(this);
                            int cuurUserUd = sharedPre.getInt("app_curr_user", 0);
                            if (cuurUserUd != user.getId()) {
                                SharedPreferences.Editor editor = sharedPre.edit();
                                editor.putInt("app_curr_user",user.getId());
                                editor.putInt("last_visited_week", 0);
                                editor.putInt("last_visited_dash", 0);
                                editor.putInt("last_visited_question", 0);
                                editor.putInt("last_visited_daily_question", 0);
                                editor.apply();
                            }
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Intent intent = new Intent(this, RegistrationPlusActivity.class);
                        intent.putExtra("mail", account.getEmail());
                        startActivity(intent);
                    }
                    requestQueue.stop();
                };

                final String serverPageUrlOfGmail = serverIP + "loginWithGmail.php";
                loginData = new StringRequest(Request.Method.POST, serverPageUrlOfGmail, serverResponse, errorListener) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> data = new HashMap<>();
                        data.put("user_mail", account.getEmail());
                        return data;
                    }
                };
                requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(loginData);
            } catch (ApiException e) {
                // Google Sign In fail
                Toast.makeText(this, "Something went wrong please try again later", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    private void setWeekAndDay() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!sharedPreferences.contains("last_visited_week")) {
            editor.putInt("last_visited_week", 0);
            editor.putInt("last_visited_dash", 0);
            editor.putInt("last_visited_question", 0);
            editor.putInt("last_visited_daily_question", 0);
        }
        editor.apply();
    }
}
