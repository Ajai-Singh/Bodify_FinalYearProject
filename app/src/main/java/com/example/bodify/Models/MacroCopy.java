package com.example.bodify.Models;

public class MacroCopy {
    private double calorieConsumption,carbohydrateConsumption,fatConsumption,proteinConsumption;
    private String userID;

    public MacroCopy() {

    }

    public double getCalorieConsumption() {
        return calorieConsumption;
    }

    public void setCalorieConsumption(double calorieConsumption) {
        this.calorieConsumption = calorieConsumption;
    }

    public double getCarbohydrateConsumption() {
        return carbohydrateConsumption;
    }

    public void setCarbohydrateConsumption(double carbohydrateConsumption) {
        this.carbohydrateConsumption = carbohydrateConsumption;
    }

    public double getFatConsumption() {
        return fatConsumption;
    }

    public void setFatConsumption(double fatConsumption) {
        this.fatConsumption = fatConsumption;
    }

    public double getProteinConsumption() {
        return proteinConsumption;
    }

    public void setProteinConsumption(double proteinConsumption) {
        this.proteinConsumption = proteinConsumption;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public MacroCopy(double calorieConsumption, double carbohydrateConsumption, double fatConsumption,double proteinConsumption, String userID) {
        this.calorieConsumption = calorieConsumption;
        this.carbohydrateConsumption = carbohydrateConsumption;
        this.fatConsumption = fatConsumption;
        this.proteinConsumption = proteinConsumption;
        this.userID = userID;
    }
}
