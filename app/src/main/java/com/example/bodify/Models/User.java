package com.example.bodify.Models;

public class User {
    private String userName,email,gender,activityLevel,fitnessGoal,bodyType,preferredMacroNutrient;
    private Double weight,bodyMassIndicator;
    private int height;
    private String mImageUrl;
    private String date;
    private String userID;
    private String id;

    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    public String getPreferredMacroNutrient() {
        return preferredMacroNutrient;
    }

    public void setPreferredMacroNutrient(String preferredMacroNutrient) {
        this.preferredMacroNutrient = preferredMacroNutrient;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public String getFitnessGoal() {
        return fitnessGoal;
    }

    public void setFitnessGoal(String fitnessGoal) {
        this.fitnessGoal = fitnessGoal;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public User() {
    }

    public User(String userName, String email, String gender, String activityLevel, String fitnessGoal, String bodyType, String preferredMacroNutrient, Double weight, Double bodyMassIndicator, int height,String mImageUrl,String date) {
        this.userName = userName;
        this.email = email;
        this.gender = gender;
        this.activityLevel = activityLevel;
        this.fitnessGoal = fitnessGoal;
        this.bodyType = bodyType;
        this.preferredMacroNutrient = preferredMacroNutrient;
        this.weight = weight;
        this.bodyMassIndicator = bodyMassIndicator;
        this.height = height;
        this.mImageUrl = mImageUrl;
        this.date = date;
    }
}
