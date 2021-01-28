package com.example.bodify.Models;

public class SuggestionMacros {
    private int calories,fats,proteins,carbohydrates;
    private String userID;
    
    public SuggestionMacros() {
        
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

    public int getProteins() {
        return proteins;
    }

    public void setProteins(int proteins) {
        this.proteins = proteins;
    }

    public int getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(int carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public SuggestionMacros(int calories, int fats, int proteins, int carbohydrates, String userID) {
        this.calories = calories;
        this.fats = fats;
        this.proteins = proteins;
        this.carbohydrates = carbohydrates;
        this.userID = userID;
    }
}
