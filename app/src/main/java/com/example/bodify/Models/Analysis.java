package com.example.bodify.Models;

public class Analysis {
    private int calories,fats,carbohydrates,proteins;
    private String userID,weekStarting;
    private double weight;

    public Analysis() {

    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getFats() {
        return fats;
    }

    public void setFats(int fats) {
        this.fats = fats;
    }

    public int getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(int carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public int getProteins() {
        return proteins;
    }

    public void setProteins(int proteins) {
        this.proteins = proteins;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getWeekStarting() {
        return weekStarting;
    }

    public void setWeekStarting(String weekStarting) {
        this.weekStarting = weekStarting;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Analysis(int calories, int fats, int carbohydrates, int proteins, String userID, String weekStarting,double weight) {
        this.calories = calories;
        this.fats = fats;
        this.carbohydrates = carbohydrates;
        this.proteins = proteins;
        this.userID = userID;
        this.weekStarting = weekStarting;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Analysis{" +
                "calories=" + calories +
                ", fats=" + fats +
                ", carbohydrates=" + carbohydrates +
                ", proteins=" + proteins +
                ", userID='" + userID + '\'' +
                ", weekStarting='" + weekStarting + '\'' +
                '}';
    }
}
