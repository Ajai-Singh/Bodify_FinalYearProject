package com.example.bodify.Models;

public class FitnessGoalCalculator {
    private String activityLevel, fitnessGoal;
    private int calorieConsumption,fats,carbohydrates,proteins, calculatorID;

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

    public FitnessGoalCalculator(String activityLevel, String fitnessGoal, int calorieConsumption, int fats, int carbohydrates, int proteins, int calculatorID) {
        this.activityLevel = activityLevel;
        this.fitnessGoal = fitnessGoal;
        this.calorieConsumption = calorieConsumption;
        this.fats = fats;
        this.carbohydrates = carbohydrates;
        this.proteins = proteins;
        this.calculatorID = calculatorID;
    }

}
