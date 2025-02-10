package com.example.fitnessapp.models;

public class Comment {
    private String username;
    private String content;

    public Comment(String username, String content) {
        this.username = username;
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }
}

