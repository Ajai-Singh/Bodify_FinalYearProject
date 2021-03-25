package com.example.bodify.Models;

import java.util.ArrayList;

public class ChatRoom {
    private ArrayList<Message> messages;
    private String theme;
    private ArrayList<String> userIds;

    public ChatRoom() {

    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public ArrayList<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(ArrayList<String> userIds) {
        this.userIds = userIds;
    }

    public ChatRoom(ArrayList<Message> messages, String theme, ArrayList<String> userIds) {
        this.messages = messages;
        this.theme = theme;
        this.userIds = userIds;
    }
}
