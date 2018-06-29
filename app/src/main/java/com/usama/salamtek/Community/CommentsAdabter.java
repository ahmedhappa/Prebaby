package com.usama.salamtek.Community;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.usama.salamtek.Model.Comment;
import com.usama.salamtek.Model.User;
import com.usama.salamtek.R;

import java.util.List;

public class CommentsAdabter extends RecyclerView.Adapter<CommentsAdabter.ViewHolder> {
    private List<Comment> comments;
    private List<User> users;
    private LayoutInflater layoutInflater;

    public CommentsAdabter(List<Comment> comments, List<User> users, Context context) {
        this.comments = comments;
        this.users = users;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.comments_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.userName.setText(users.get(position).getName());
        byte[] arr = Base64.decode(users.get(position).getImage(), Base64.DEFAULT);
        holder.userImg.setImageBitmap(BitmapFactory.decodeByteArray(arr, 0, arr.length));
        holder.commentContent.setText(comments.get(position).getCommentContent());
        holder.commentDate.setText(comments.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName, commentContent, commentDate;
        ImageView userImg;

        public ViewHolder(View itemView) {
            super(itemView);
            userImg = itemView.findViewById(R.id.profile_user);
            userName = itemView.findViewById(R.id.commentuser);
            commentContent = itemView.findViewById(R.id.commentdetails);
            commentDate = itemView.findViewById(R.id.commenttime);
        }
    }
}
