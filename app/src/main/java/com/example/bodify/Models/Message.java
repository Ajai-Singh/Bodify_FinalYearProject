package com.example.bodify.Models;

public class Message {
    private String messageText, userId, dateTime;

    public Message(String messageText, String userId, String dateTime) {
        this.messageText = messageText;
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