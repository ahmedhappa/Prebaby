package com.usama.salamtek;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
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
    Response.ErrorListener errorListener;
    Response.Listener<String> serverResponse;
    StringRequest loginData;
    RequestQueue requestQueue;

    TextView userName, pass;
    Button login;
    ImageButton gMailLogin;

    private final String serverPageUrl = "http://192.168.1.7:8080/Graduation_Project/getUserData.php";

    private GoogleSignInClient mGoogleSignInClient;
    private static final int signInRequstCode = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userName = findViewById(R.id.username);
        pass = findViewById(R.id.login_pass);
        login = findViewById(R.id.login_btn);
        gMailLogin = findViewById(R.id.gmail_login);

        login.setOnClickListener(view -> {
            String sUserName = userName.getText().toString(), sPass = pass.getText().toString();
            if (!(sUserName.equals("") && sPass.equals(""))) {

                serverResponse = response -> {
                    if (!response.equals("null")) {
                        Log.i("Server Response", response);
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
                            intent.putExtra("user_data", user);
                            startActivity(intent);
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
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } catch (ApiException e) {
                // Google Sign In fail
                Toast.makeText(this, "Something went wrong please try again later", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
