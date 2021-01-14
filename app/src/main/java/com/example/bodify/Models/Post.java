package com.example.bodify.Models;

import androidx.annotation.NonNull;

public class Post {
    private String date,postText,postID,id;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Post() {

    }

    public Post(String date, String postText, String postID) {
        this.date = date;
        this.postText = postText;
        this.postID = postID;
    }

    @NonNull
    @Override
    public String toString() {
        return "Post{" +
                "date='" + date + '\'' +
                ", postText='" + postText + '\'' +
                ", postID='" + postID + '\'' +
                '}';
    }
}
