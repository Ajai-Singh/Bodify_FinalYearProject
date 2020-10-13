package com.example.bodify.Models;

import java.util.ArrayList;

public class User {
    private String userName,password,email,gender;
    private Double weight,height,bodyMassIndicator;
    //this will store comment ids as integers
//    private ArrayList<Integer> commentIDs = new ArrayList<>();
//    private ArrayList<Integer> recipeIDs = new ArrayList<>();
//    private Integer calculatorID;
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getBodyMassIndicator() {
        return bodyMassIndicator;
    }

    public void setBodyMassIndicator(Double bodyMassIndicator) {
        this.bodyMassIndicator = bodyMassIndicator;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public User() {
    }

    public User(String userName, String password, String gender, String email, Double weight, Double height, Double bodyMassIndicator) {
        this.userName = userName;
        this.password = password;
        this.gender = gender;
        this.email = email;
        this.weight = weight;
        this.height = height;
        this.bodyMassIndicator = bodyMassIndicator;
    }
}
