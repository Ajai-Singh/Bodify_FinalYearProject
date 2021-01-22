package com.example.bodify.Models;

public class Analysis {
    private double calories,fats,carbohydrates,proteins;

    public Analysis() {

    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
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

    public Analysis(double calories, double fats, double carbohydrates, double proteins) {
        this.calories = calories;
        this.fats = fats;
        this.carbohydrates = carbohydrates;
        this.proteins = proteins;
    }
}
