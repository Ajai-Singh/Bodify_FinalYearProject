package com.example.bodify.Models;

public class Message {
    private String messageText;
    private String messageUser;
    private String userId;
    private String dateTime;

    public Message(String messageText, String messageUser, String userId, String dateTime) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.userId = userId;
        this.dateTime = dateTime;
    }

    public Message() {

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}