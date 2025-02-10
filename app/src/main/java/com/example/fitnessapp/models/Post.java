package com.example.fitnessapp.models;

public class Post {
    private int postID;
    private String firstName;
    private String lastName;
    private String username;
    private String activityTypeName;
    private int duration;
    private int caloriesBurned;
    private String content;
    private String timestamp;
    private int likesCount;
    private int commentsCount;

    public Post(int postID, String firstName, String lastName, String username, String activityTypeName, int duration,int caloriesBurned, String content, String timestamp, int likesCount, int commentsCount) {
        this.postID = postID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.activityTypeName = activityTypeName;
        this.duration = duration;
        this.caloriesBurned =caloriesBurned;
        this.content = content;
        this.timestamp = timestamp;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
    }

    public int getPostID() { return postID; }
    public String getFullName() { return firstName + " " + lastName; }
    public String getUsername() { return username; }
    public String getActivityTypeName() { return activityTypeName; }
    public int getDuration() { return duration; }
    public int getCaloriesBurned() { return caloriesBurned; }
    public String getContent() { return content; }
    public String getTimestamp() { return timestamp; }
    public int getLikesCount() { return likesCount; }
    public int getCommentsCount() { return commentsCount; }
}
