package com.example.fitnessapp.models;

public class Friend {
    private int friendID;
    private String firstName;
    private String lastName;
    private String username;

    public Friend(int friendID, String firstName, String lastName, String username) {
        this.friendID = friendID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
    }

    public Friend(int friendID, String username){
        this.friendID = friendID;
        this.username =username;
    }

    public int getFriendID() {
        return friendID;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getUsername() {
        return username;
    }
}
