package com.example.bodify.Models;

public class Room {
    private String theme, id, adminId;

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

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public Room(String theme, String adminId) {
        this.theme = theme;
        this.adminId = adminId;
    }
}
