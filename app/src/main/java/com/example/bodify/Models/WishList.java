package com.example.bodify.Models;

public class WishList {
    private Grocery grocery;
    private String userID,id;

    public WishList() {

    }

    public Grocery getGrocery() {
        return grocery;
    }

    public void setGrocery(Grocery grocery) {
        this.grocery = grocery;
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

    public WishList(Grocery grocery, String userID) {
        this.grocery = grocery;
        this.userID = userID;
    }
}
