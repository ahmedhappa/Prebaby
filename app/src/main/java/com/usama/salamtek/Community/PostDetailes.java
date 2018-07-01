package com.usama.salamtek.Community;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.usama.salamtek.LoginActivity;
import com.usama.salamtek.Model.Comment;
import com.usama.salamtek.Model.Post;
import com.usama.salamtek.Model.User;
import com.usama.salamtek.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PostDetailes extends AppCompatActivity {
    Response.Listener<String> sendCommentServerResponse, getCommentsServerResponse;
    Response.ErrorListener errorListener;
    StringRequest sendComment, getComments;
    RequestQueue requestQueue;
    private final String pageUrl = LoginActivity.serverIP + "createComment.php";
    private final String getCommentsPageUrl = LoginActivity.serverIP + "getComments.php";

    ImageView postPic, profilePic, likePic;
    TextView title, desc, likes, comments, date, postedBy;
    EditText comment;
    Button createComment;
    RecyclerView commentsRecycler;

    Response.Listener<String> sererResponseFromLike;
    StringRequest makeALike;
    private final String likePageUrl = LoginActivity.serverIP + "makeALike.php";

    List<Comment> postComments = new ArrayList<>();
    List<User> commentUsers = new ArrayList<>();

    String commentContent;

    Boolean likedPost = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_details);
        postPic = findViewById(R.id.postImg);
        profilePic = findViewById(R.id.profile_pic);
        title = findViewById(R.id.post_title);
        desc = findViewById(R.id.post_desc);
        likes = findViewById(R.id.likeText);
        comments = findViewById(R.id.commentsCountTextView);
        date = findViewById(R.id.dateTextView);
        postedBy = findViewById(R.id.posted_by_name);
        comment = findViewById(R.id.write_comment);
        createComment = findViewById(R.id.create_comment);
        commentsRecycler = findViewById(R.id.commentsRecyclerView);
        likePic = findViewById(R.id.likeImg);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("post")) {
            Post post = intent.getParcelableExtra("post");
            User user = intent.getParcelableExtra("user");
            User currentUser = intent.getParcelableExtra("curr_user_data");
            User currUser = intent.getParcelableExtra("curr_user_data");

            if (intent.hasExtra("liked_post")) {
                likePic.setBackgroundResource(R.drawable.like_red);
                likedPost = true;
            } else {
                likePic.setBackgroundResource(R.drawable.like);
            }

            likePic.setOnClickListener(view -> {
                if (!likedPost) {
                    sererResponseFromLike = response -> {
                    };

                    makeALike = new StringRequest(Request.Method.POST, likePageUrl, sererResponseFromLike, errorListener) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> data = new HashMap<>();
                            data.put("post_id", String.valueOf(post.getId()));
                            data.put("user_id", String.valueOf(currentUser.getId()));
                            data.put("number_of_likes", post.getNumberOfLikes());
                            return data;
                        }
                    };
                    post.setNumberOfLikes(String.valueOf(Integer.parseInt(post.getNumberOfLikes()) + 1));
                    requestQueue.add(makeALike);
                    likedPost = true;
                    likePic.setBackgroundResource(0);
                    likePic.setBackgroundResource(R.drawable.like_red);
                    likes.setText(post.getNumberOfLikes());
                } else {
                    Toast.makeText(this, "You Liked this post already", Toast.LENGTH_SHORT).show();
                }

            });

            byte[] arrPost = Base64.decode(post.getImg(), Base64.DEFAULT);
            postPic.setImageBitmap(BitmapFactory.decodeByteArray(arrPost, 0, arrPost.length));
            byte[] arrProfile = Base64.decode(user.getImage(), Base64.DEFAULT);
            profilePic.setImageBitmap(BitmapFactory.decodeByteArray(arrProfile, 0, arrProfile.length));
            title.setText(post.getTitle());
            desc.setText(post.getDisciription());
            likes.setText(post.getNumberOfLikes());
            date.setText(post.getDate());
            postedBy.setText(user.getName());
            comments.setText(post.getNumberOfComments());

            errorListener = error -> {
                error.printStackTrace();
                requestQueue.stop();
            };

            getCommentsServerResponse = response -> {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    Comment postComment;
                    User commentUser;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        postComment = new Comment();
                        commentUser = new User();
                        postComment.setCommentContent(jsonArray.getJSONObject(i).getString("comment_content"));
                        postComment.setDate(jsonArray.getJSONObject(i).getString("comment_time"));
                        commentUser.setName(jsonArray.getJSONObject(i).getString("user_name"));
                        commentUser.setImage(jsonArray.getJSONObject(i).getString("user_image"));
                        postComments.add(postComment);
                        commentUsers.add(commentUser);
                    }
                    CommentsAdabter commentsAdabter = new CommentsAdabter(postComments, commentUsers, this);
                    commentsRecycler.setLayoutManager(new LinearLayoutManager(this));
                    commentsRecycler.setAdapter(commentsAdabter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            };

            getComments = new StringRequest(Request.Method.POST, getCommentsPageUrl, getCommentsServerResponse, errorListener) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> data = new HashMap<>();
                    data.put("post_id", String.valueOf(post.getId()));
                    return data;
                }
            };
            requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(getComments);

            sendCommentServerResponse = response -> {
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                comments.setText(post.getNumberOfComments());
                Comment comment = new Comment();
                User user1 = new User();
                user1.setName(user.getName());
                user1.setImage(user.getImage());
                comment.setCommentContent(commentContent);
                Date date = Calendar.getInstance().getTime();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
                comment.setDate(simpleDateFormat.format(date));
                if (postComments.size() == 0) {
                    postComments.add(comment);
                    commentUsers.add(user1);
                    CommentsAdabter commentsAdabter = new CommentsAdabter(postComments, commentUsers, this);
                    commentsRecycler.setLayoutManager(new LinearLayoutManager(this));
                    commentsRecycler.setAdapter(commentsAdabter);
                } else {
                    postComments.add(comment);
                    commentUsers.add(user1);
                    commentsRecycler.getAdapter().notifyDataSetChanged();
                }

            };


            sendComment = new StringRequest(Request.Method.POST, pageUrl, sendCommentServerResponse, errorListener) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> data = new HashMap<>();
                    data.put("post_id", String.valueOf(post.getId()));
                    data.put("comment_content", String.valueOf(commentContent));
                    data.put("curr_user_id", String.valueOf(currUser.getId()));
                    data.put("number_of_comments", post.getNumberOfComments());
                    return data;
                }
            };

            createComment.setOnClickListener(view -> {
                commentContent = comment.getText().toString();
                if (!commentContent.equals("")) {
                    post.setNumberOfComments(String.valueOf(Integer.valueOf(post.getNumberOfComments()) + 1));
                    requestQueue.add(sendComment);
                } else {
                    Toast.makeText(this, "Please write a comment", Toast.LENGTH_SHORT).show();
                }

            });
        }
    }
}
