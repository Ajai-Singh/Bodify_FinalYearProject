package com.example.bodify.Models;

public class User {
    private String userName,email,gender,activityLevel,fitnessGoal,key;
    private Double weight,bodyMassIndicator;
    private int height;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public User() {
    }

    public User(String userName, String email, String gender, String activityLevel, String fitnessGoal, String key, Double weight, Double bodyMassIndicator, int height) {
        this.userName = userName;
        this.email = email;
        this.gender = gender;
        this.activityLevel = activityLevel;
        this.fitnessGoal = fitnessGoal;
        this.key = key;
        this.weight = weight;
        this.bodyMassIndicator = bodyMassIndicator;
        this.height = height;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", activityLevel='" + activityLevel + '\'' +
                ", fitnessGoal='" + fitnessGoal + '\'' +
                ", weight=" + weight +
                ", bodyMassIndicator=" + bodyMassIndicator +
                ", height=" + height +
                '}';
    }
}
