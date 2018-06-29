package com.usama.salamtek.Community;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.usama.salamtek.Interfaces.ClickPostListener;
import com.usama.salamtek.LoginActivity;
import com.usama.salamtek.Model.Post;
import com.usama.salamtek.Model.User;
import com.usama.salamtek.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CommunityMainActivity extends AppCompatActivity implements ClickPostListener {
    FloatingActionButton createPost;
    User user;
    RecyclerView recyclerView;

    Response.Listener<String> sererResponse;
    Response.ErrorListener errorListener;
    StringRequest getPosts;
    RequestQueue requestQueue;
    private final String pageUrl = LoginActivity.serverIP + "getPosts.php";

    Response.Listener<String> sererResponseFromLike;
    StringRequest makeALike;
    private final String likePageUrl = LoginActivity.serverIP + "makeALike.php";

    List<Post> posts = new ArrayList<>();
    List<User> users = new ArrayList<>();
    List<Integer> likedPost = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_main);
        createPost = findViewById(R.id.addNewPostFab);
        recyclerView = findViewById(R.id.recycler_view);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("user_data")) {
            user = intent.getParcelableExtra("user_data");
            createPost.setOnClickListener(view -> {
                Intent intent1 = new Intent(this, CreatePostActivity.class);
                intent1.putExtra("user_data", user);
                startActivity(intent1);
            });

            sererResponse = response -> {
                if (!response.equals("null")) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("post_data");
                        Post post;
                        User user;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            post = new Post();
                            user = new User();
                            post.setId(Integer.parseInt(jsonArray.getJSONObject(i).getString("post_id")));
                            post.setTitle(jsonArray.getJSONObject(i).getString("title"));
                            post.setDisciription(jsonArray.getJSONObject(i).getString("description"));
                            post.setImg(jsonArray.getJSONObject(i).getString("post_image"));
                            post.setNumberOfLikes(jsonArray.getJSONObject(i).getString("number_of_likes"));
                            post.setNumberOfComments(jsonArray.getJSONObject(i).getString("number_of_comments"));
                            post.setDate(jsonArray.getJSONObject(i).getString("post_date"));
                            user.setImage(jsonArray.getJSONObject(i).getString("user_image"));
                            user.setName(jsonArray.getJSONObject(i).getString("user_name"));
                            posts.add(post);
                            users.add(user);
                        }
                        JSONArray likedPostsArray = jsonObject.getJSONArray("like_data");
                        for (int i = 0; i < likedPostsArray.length(); i++) {
                            likedPost.add(Integer.parseInt(likedPostsArray.getString(i)));
                        }
                        PostsRecyclerAdabter postsRecyclerAdabter = new PostsRecyclerAdabter(posts, users, likedPost, this);
                        postsRecyclerAdabter.setInterfaceObj(this);
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));
                        recyclerView.setAdapter(postsRecyclerAdabter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            errorListener = error -> {
                error.printStackTrace();
                requestQueue.stop();
            };
            getPosts = new StringRequest(Request.Method.POST, pageUrl, sererResponse, errorListener) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> data = new HashMap<>();
                    data.put("user_id", String.valueOf(user.getId()));
                    return data;
                }
            };
            requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(getPosts);

        }

    }

    @Override
    public void onClickPost(int position) {
        Intent intent = new Intent(this, PostDetailes.class);
        intent.putExtra("post", posts.get(position));
        intent.putExtra("user", users.get(position));
        intent.putExtra("curr_user_data", user);
        if (likedPost.contains(posts.get(position).getId())) {
            intent.putExtra("liked_post", true);
        }
        startActivity(intent);
    }

    @Override
    public void onClickLike(int postId, int i) {
        sererResponseFromLike = response -> {
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
        };

        makeALike = new StringRequest(Request.Method.POST, likePageUrl, sererResponseFromLike, errorListener) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("post_id", String.valueOf(postId));
                data.put("user_id", String.valueOf(user.getId()));
                data.put("number_of_likes", posts.get(i).getNumberOfLikes());
                return data;
            }
        };
        requestQueue.add(makeALike);
    }
}
