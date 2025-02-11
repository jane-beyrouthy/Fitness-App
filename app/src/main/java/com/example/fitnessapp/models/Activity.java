package com.example.fitnessapp.models;

public class Activity {
    private String activityType;
    private int sessions;
    private int totalDuration;
    private int totalCalories;

    public Activity(String activityType, int sessions, int totalDuration, int totalCalories) {
        this.activityType = activityType;
        this.sessions = sessions;
        this.totalDuration = totalDuration;
        this.totalCalories = totalCalories;
    }

    public String getActivityType() { return activityType; }
    public int getSessions() { return sessions; }
    public int getTotalDuration() { return totalDuration; }
    public int getTotalCalories() { return totalCalories; }
}
