package com.example.fitnessapp.models;

public class Post {
    private String username;
    private String content;
    private String imageUrl;
    private String timestamp;
    private int likesCount;
    private int commentsCount;

    public Post(String username, String content, String imageUrl, String timestamp, int likesCount, int commentsCount) {
        this.username = username;
        this.content = content;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
    }

    public String getUsername() { return username; }
    public String getContent() { return content; }
    public String getImageUrl() { return imageUrl; }
    public String getTimestamp() { return timestamp; }
    public int getLikesCount() { return likesCount; }
    public int getCommentsCount() { return commentsCount; }
}
