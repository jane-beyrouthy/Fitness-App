package com.example.fitnessapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fitnessapp.R;
import com.example.fitnessapp.models.Activity;
import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {
    private Context context;
    private List<Activity> activityList;

    public ActivityAdapter(Context context, List<Activity> activityList) {
        this.context = context;
        this.activityList = activityList;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_activity, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        Activity activity = activityList.get(position);
        holder.tvActivityType.setText(activity.getActivityType());
        holder.tvSessions.setText("Sessions: " + activity.getSessions());
        holder.tvTotalDuration.setText("Duration: " + activity.getTotalDuration() + " mins");
        holder.tvTotalCalories.setText("Calories: " + activity.getTotalCalories() + " kcal");
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView tvActivityType, tvSessions, tvTotalDuration, tvTotalCalories;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvActivityType = itemView.findViewById(R.id.tvActivityType);
            tvSessions = itemView.findViewById(R.id.tvSession);
            tvTotalDuration = itemView.findViewById(R.id.tvDuration);
            tvTotalCalories = itemView.findViewById(R.id.tvCalories);
        }
    }
}
