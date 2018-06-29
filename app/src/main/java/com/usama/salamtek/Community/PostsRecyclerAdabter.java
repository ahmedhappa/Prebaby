package com.usama.salamtek.Community;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.usama.salamtek.Interfaces.ClickPostListener;
import com.usama.salamtek.Model.Post;
import com.usama.salamtek.Model.User;
import com.usama.salamtek.R;

import java.util.List;

public class PostsRecyclerAdabter extends RecyclerView.Adapter<PostsRecyclerAdabter.ViewHolder> {
    private List<Post> posts;
    private List<User> users;
    private List<Integer> likedPost;
    private Context context;
    private LayoutInflater layoutInflater;
    private ClickPostListener clickPostListener;

    public PostsRecyclerAdabter(List<Post> posts, List<User> users, List<Integer> likedPost, Context context) {
        this.posts = posts;
        this.users = users;
        this.likedPost = likedPost;
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setInterfaceObj(ClickPostListener interfaceObj) {
        this.clickPostListener = interfaceObj;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.posts_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(posts.get(position).getTitle());
        holder.desc.setText(posts.get(position).getDisciription());
        holder.liks.setText(posts.get(position).getNumberOfLikes());
        holder.date.setText(posts.get(position).getDate());
        holder.comments.setText(posts.get(position).getNumberOfComments());
        byte[] arr = Base64.decode(users.get(position).getImage(), Base64.DEFAULT);
        Bitmap proPic = BitmapFactory.decodeByteArray(arr, 0, arr.length);
        holder.profilePic.setImageBitmap(proPic);

        byte[] arr2 = Base64.decode(posts.get(position).getImg(), Base64.DEFAULT);
        Bitmap posPic = BitmapFactory.decodeByteArray(arr2, 0, arr2.length);
        holder.postPic.setImageBitmap(posPic);

        if (likedPost.contains(posts.get(position).getId())) {
            holder.likeImage.setBackgroundResource(R.drawable.like_red);
        } else {
            holder.likeImage.setBackgroundResource(R.drawable.like);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView postPic, profilePic, likeImage;
        TextView title, desc, liks, comments, date;
        CardView parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            postPic = itemView.findViewById(R.id.postImageView);
            profilePic = itemView.findViewById(R.id.authorImageView);
            title = itemView.findViewById(R.id.titleTextView);
            desc = itemView.findViewById(R.id.detailsTextView);
            liks = itemView.findViewById(R.id.likeText);
            comments = itemView.findViewById(R.id.commentsCountTextView);
            date = itemView.findViewById(R.id.dateTextView);
            parentLayout = itemView.findViewById(R.id.card_view);
            likeImage = itemView.findViewById(R.id.likeImg);

            parentLayout.setOnClickListener(view -> clickPostListener.onClickPost(getAdapterPosition()));

            likeImage.setOnClickListener(view -> {
                        if (!likedPost.contains(posts.get(getAdapterPosition()).getId())) {
                            likeImage.setBackgroundResource(0);
                            likeImage.setBackgroundResource(R.drawable.like_red);
                            posts.get(getAdapterPosition()).setNumberOfLikes(String.valueOf(Integer.parseInt(posts.get(getAdapterPosition()).getNumberOfLikes()) + 1));
                            liks.setText(posts.get(getAdapterPosition()).getNumberOfLikes());
                            likedPost.add(posts.get(getAdapterPosition()).getId());
                            clickPostListener.onClickLike(posts.get(getAdapterPosition()).getId(), getAdapterPosition());
                        } else {
                            Toast.makeText(context, "You Liked this post already", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

        }
    }
}
