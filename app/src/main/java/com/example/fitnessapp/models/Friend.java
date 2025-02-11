package com.example.fitnessapp.models;

public class Friend {
    private String firstName;
    private String lastName;
    private String username;

    public Friend(String firstName, String lastName, String username) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getUsername() {
        return username;
    }
}
