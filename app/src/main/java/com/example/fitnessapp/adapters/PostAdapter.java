package com.example.fitnessapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fitnessapp.R;
import com.example.fitnessapp.PostActivity;
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
        holder.fullName.setText(post.getFullName() + " - @" + post.getUsername());
        holder.activityType.setText("Activity: " + post.getActivityTypeName());
        holder.duration.setText("Duration: " + post.getDuration() + " mins");
        holder.caloriesBurned.setText("Calories Burned: " + post.getCaloriesBurned() + " kcal");
        holder.content.setText(post.getContent());
        holder.likesCount.setText(String.valueOf(post.getLikesCount()));
        holder.commentsCount.setText(String.valueOf(post.getCommentsCount()));

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, PostActivity.class);
            intent.putExtra("postID", post.getPostID());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView fullName, activityType, duration, content, likesCount, commentsCount, caloriesBurned;
        ImageView likeIcon, commentIcon;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.tvFullName);
            activityType = itemView.findViewById(R.id.tvActivityType);
            duration = itemView.findViewById(R.id.tvDuration);
            caloriesBurned = itemView.findViewById(R.id.tvCaloriesBurned);
            content = itemView.findViewById(R.id.tvContent);
            likesCount = itemView.findViewById(R.id.tvLikesCount);
            commentsCount = itemView.findViewById(R.id.tvCommentsCount);
            likeIcon = itemView.findViewById(R.id.ivLike);
            commentIcon = itemView.findViewById(R.id.ivComment);
        }
    }
}
