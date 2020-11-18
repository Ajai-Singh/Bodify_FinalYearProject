package com.example.bodify.Models;

import java.io.Serializable;

public class Recipe implements Serializable {
    private int id; // this id corresponds to the id of the recipe
    private String title,sourceUrl;
    private String readyInMinutes,servings;
    private String recipeID;

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

    public Recipe() {

    }

    public Recipe(int id, String title, String sourceUrl, String readyInMinutes, String servings,String recipeID) {
        this.id = id;
        this.title = title;
        this.sourceUrl = sourceUrl;
        this.readyInMinutes = readyInMinutes;
        this.servings = servings;
        this.recipeID = recipeID;
    }
}
