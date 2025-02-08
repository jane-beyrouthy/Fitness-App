package com.example.fitnessapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.fitnessapp.R;
import com.example.fitnessapp.models.Post;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private Context context;
    private List<Post> postList;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.fullName.setText(post.getFullName());
        holder.username.setText(post.getUsername());
        holder.content.setText(post.getContent());
        holder.timestamp.setText(post.getTimestamp());
        holder.likesCount.setText(String.valueOf(post.getLikesCount()));
        holder.commentsCount.setText(String.valueOf(post.getCommentsCount()));

        // Load image using Glide
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            Glide.with(context).load(post.getImageUrl()).into(holder.postImage);
        } else {
            holder.postImage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView username, content, timestamp, likesCount, commentsCount, fullName;
        ImageView postImage, likeIcon, commentIcon;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.tvUsername);
            content = itemView.findViewById(R.id.tvContent);
            timestamp = itemView.findViewById(R.id.tvTimestamp);
            likesCount = itemView.findViewById(R.id.tvLikesCount);
            commentsCount = itemView.findViewById(R.id.tvCommentsCount);
            postImage = itemView.findViewById(R.id.ivPostImage);
            likeIcon = itemView.findViewById(R.id.ivLike);
            commentIcon = itemView.findViewById(R.id.ivComment);
        }
    }
}