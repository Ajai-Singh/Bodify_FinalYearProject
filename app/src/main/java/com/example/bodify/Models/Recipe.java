package com.example.bodify.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Recipe implements Serializable {

    private int id;
    private String title;
    private String sourceUrl;
    private String readyInMinutes;
    private int servings;
    private String recipeID;
    private String dayOfWeek;
    private int calories,fats,carbohydrates,proteins,sugar,sodium;

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

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(String recipeID) {
        this.recipeID = recipeID;
    }

    public int getSugar() {
        return sugar;
    }

    public void setSugar(int sugar) {
        this.sugar = sugar;
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

    public int getSodium() {
        return sodium;
    }

    public void setSodium(int sodium) {
        this.sodium = sodium;
    }

    public Recipe() {

    }

    public Recipe(int id, String title, String sourceUrl, String readyInMinutes, int servings, String recipeID, int calories, int fats, int carbohydrates, int proteins,int sugar,int sodium) {
        this.id = id; // realistically I dont need the Recipe id but will keep for now
        this.title = title;
        this.sourceUrl = sourceUrl;
        this.readyInMinutes = readyInMinutes;
        this.servings = servings;
        this.recipeID = recipeID;
        this.calories = calories;
        this.fats = fats;
        this.carbohydrates = carbohydrates;
        this.proteins = proteins;
        this.sugar = sugar;
        this.sodium = sodium;
    }
}
