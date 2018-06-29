package com.usama.salamtek.Community;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.usama.salamtek.LoginActivity;
import com.usama.salamtek.Model.Post;
import com.usama.salamtek.Model.User;
import com.usama.salamtek.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CreatePostActivity extends AppCompatActivity {
    Response.Listener<String> sererResponse;
    Response.ErrorListener errorListener;
    StringRequest createUserPost;
    RequestQueue requestQueue;
    private final String pageUrl = LoginActivity.serverIP + "createPost.php";

    ImageView postImg;
    EditText title, desc;
    Button createPost;
    Post post;
    User user;
    private final int reqCodeForIntentImg = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        postImg = findViewById(R.id.postImg);
        title = findViewById(R.id.create_post_title);
        desc = findViewById(R.id.create_post_description);
        createPost = findViewById(R.id.create_post_btn);
        post = new Post();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("user_data")) {
            user = intent.getParcelableExtra("user_data");

            sererResponse = response -> {
                if (response.equals("done")) {
                    Toast.makeText(this, "Post Added Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "There is an error please try again later", Toast.LENGTH_SHORT).show();
                }
                requestQueue.stop();
            };

            errorListener = error -> {
                error.printStackTrace();
                requestQueue.stop();
            };

            createUserPost = new StringRequest(Request.Method.POST, pageUrl, sererResponse, errorListener) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> data = new HashMap<>();
                    data.put("user_id", String.valueOf(user.getId()));
                    data.put("title", post.getTitle());
                    data.put("desc", post.getDisciription());
                    if (post.getImg() != null) {
                        data.put("img", post.getImg());
                    } else {
                        data.put("img", "");
                    }

                    return data;
                }
            };

            createPost.setOnClickListener(view -> {
                post.setTitle(title.getText().toString());
                post.setDisciription(desc.getText().toString());
                requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(createUserPost);
            });

            postImg.setOnClickListener(view -> {
                Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent1.setType("image/*");
                startActivityForResult(intent1, reqCodeForIntentImg);
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == reqCodeForIntentImg && resultCode == RESULT_OK && data != null) {
            Uri imgData = data.getData();
            try {
                Bitmap img = MediaStore.Images.Media.getBitmap(getContentResolver(), imgData);
                postImg.setImageBitmap(img);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                img.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                byte[] arr = byteArrayOutputStream.toByteArray();
                post.setImg(Base64.encodeToString(arr, Base64.DEFAULT));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
