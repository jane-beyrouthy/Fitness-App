package com.example.fitnessapp.models;

import androidx.annotation.Nullable;

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Friend friend = (Friend) obj;
        return friendID == friend.friendID;
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
