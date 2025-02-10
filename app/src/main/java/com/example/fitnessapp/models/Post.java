package com.example.fitnessapp.models;

public class Post {
    private String fullName;
    private String username;
    private String content;
    private String timestamp;
    private int likesCount;
    private int commentsCount;

    public Post(String firstName, String lastName,String username, String content, String timestamp, int likesCount, int commentsCount) {
        this.fullName = firstName+" "+lastName;
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
    }

    public String getFullName() { return fullName;}
    public String getUsername() { return username; }
    public String getContent() { return content; }
    public String getTimestamp() { return timestamp; }
    public int getLikesCount() { return likesCount; }
    public int getCommentsCount() { return commentsCount; }
}
