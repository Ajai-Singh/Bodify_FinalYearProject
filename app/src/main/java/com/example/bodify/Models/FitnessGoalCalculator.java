package com.example.bodify.Models;

public class FitnessGoalCalculator {
    private int calorieConsumption,fats,carbohydrates,proteins, calculatorID;

    public int getCalorieConsumption() {
        return calorieConsumption;
    }

    public void setCalorieConsumption(int calorieConsumption) {
        this.calorieConsumption = calorieConsumption;
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

    public int getCalculatorID() {
        return calculatorID;
    }

    public void setCalculatorID(int calculatorID) {
        this.calculatorID = calculatorID;
    }

    public FitnessGoalCalculator() {
    }

    public FitnessGoalCalculator(int calorieConsumption, int fats, int carbohydrates, int proteins, int calculatorID) {
        this.calorieConsumption = calorieConsumption;
        this.fats = fats;
        this.carbohydrates = carbohydrates;
        this.proteins = proteins;
        this.calculatorID = calculatorID;
    }

}
