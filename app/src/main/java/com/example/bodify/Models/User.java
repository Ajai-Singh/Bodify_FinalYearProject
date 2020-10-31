package com.example.bodify.Models;

public class User {
    private String userName,email,gender,activityLevel,fitnessGoal,imageUrl,bodyType,likefattyFoods;
    private Double weight,bodyMassIndicator;
    private int height;

    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    public String getLikefattyFoods() {
        return likefattyFoods;
    }

    public void setLikefattyFoods(String likefattyFoods) {
        this.likefattyFoods = likefattyFoods;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public User(String userName, String email, String gender, String activityLevel, String fitnessGoal,Double weight, Double bodyMassIndicator, int height,String imageUrl,
                String bodyType,String likefattyFoods) {
        this.userName = userName;
        this.email = email;
        this.gender = gender;
        this.activityLevel = activityLevel;
        this.fitnessGoal = fitnessGoal;
        this.weight = weight;
        this.bodyMassIndicator = bodyMassIndicator;
        this.height = height;
        this.imageUrl = imageUrl;
        this.bodyType = bodyType;
        this.likefattyFoods = likefattyFoods;
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
