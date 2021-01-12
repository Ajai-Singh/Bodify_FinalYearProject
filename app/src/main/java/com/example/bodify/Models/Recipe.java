package com.example.bodify.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Recipe implements Serializable {

    private int id;
    private String title;
    private String sourceUrl;
    private String readyInMinutes;
    private String servings;
    private String recipeID;
    private double calories,fats,carbohydrates,proteins;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getReadyInMinutes() {
        return readyInMinutes;
    }

    public void setReadyInMinutes(String readyInMinutes) {
        this.readyInMinutes = readyInMinutes;
    }

    public String getServings() {
        return servings;
    }

    public void setServings(String servings) {
        this.servings = servings;
    }

    public String getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(String recipeID) {
        this.recipeID = recipeID;
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

    public Recipe() {

    }

    public Recipe(int id, String title, String sourceUrl, String readyInMinutes, String servings, String recipeID, double calories, double fats, double carbohydrates, double proteins) {
        this.id = id;
        this.title = title;
        this.sourceUrl = sourceUrl;
        this.readyInMinutes = readyInMinutes;
        this.servings = servings;
        this.recipeID = recipeID;
        this.calories = calories;
        this.fats = fats;
        this.carbohydrates = carbohydrates;
        this.proteins = proteins;
    }
}
