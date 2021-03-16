package com.example.bodify.Models;

public class Macro {
    private Double calorieConsumption,fats,carbohydrates,proteins;
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getCalorieConsumption() {
        return calorieConsumption;
    }

    public void setCalorieConsumption(double calorieConsumption) {
        this.calorieConsumption = calorieConsumption;
    }

    public double getFats() {
        return fats;
    }

    public void setFats(double fats) {
        this.fats = fats;
    }

    public double getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public double getProteins() {
        return proteins;
    }

    public void setProteins(double proteins) {
        this.proteins = proteins;
    }

    public Macro() {
    }

    public Macro(double calorieConsumption, double fats, double carbohydrates, double proteins, String userId) {
        this.calorieConsumption = calorieConsumption;
        this.fats = fats;
        this.carbohydrates = carbohydrates;
        this.proteins = proteins;
        this.userId = userId;
    }
}
