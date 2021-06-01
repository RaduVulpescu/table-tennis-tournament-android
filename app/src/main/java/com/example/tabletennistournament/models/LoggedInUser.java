package com.example.tabletennistournament.models;

public class LoggedInUser {

    private final String userEmail;

    public LoggedInUser(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserEmail() {
        return userEmail;
    }

}