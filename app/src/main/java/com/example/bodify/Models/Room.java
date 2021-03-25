package com.example.bodify.Models;

public class Room {
    private String theme,id;

    public Room() {

    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Room(String theme) {
        this.theme = theme;
    }
}
