package com.example.bodify.Models;

public class Room {
    private String theme,userID,id;

    public Room() {

    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Room(String theme, String userID) {
        this.theme = theme;
        this.userID = userID;
    }
}
