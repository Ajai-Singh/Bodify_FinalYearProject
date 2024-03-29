package com.example.bodify.Models;

public class Favourite {
    private String itemName, userID, id, sourceUrl;
    private int calories, itemTotalFat, itemSodium, itemTotalCarbohydrates, itemSugars, itemProtein, numberOfServings;

    public Favourite() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getItemTotalFat() {
        return itemTotalFat;
    }

    public void setItemTotalFat(int itemTotalFat) {
        this.itemTotalFat = itemTotalFat;
    }

    public int getItemSodium() {
        return itemSodium;
    }

    public void setItemSodium(int itemSodium) {
        this.itemSodium = itemSodium;
    }

    public int getItemTotalCarbohydrates() {
        return itemTotalCarbohydrates;
    }

    public void setItemTotalCarbohydrates(int itemTotalCarbohydrates) {
        this.itemTotalCarbohydrates = itemTotalCarbohydrates;
    }

    public int getItemSugars() {
        return itemSugars;
    }

    public void setItemSugars(int itemSugars) {
        this.itemSugars = itemSugars;
    }

    public int getItemProtein() {
        return itemProtein;
    }

    public void setItemProtein(int itemProtein) {
        this.itemProtein = itemProtein;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getNumberOfServings() {
        return numberOfServings;
    }

    public void setNumberOfServings(int numberOfServings) {
        this.numberOfServings = numberOfServings;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public Favourite(String itemName, int calories, int itemTotalFat,
                     int itemSodium, int itemTotalCarbohydrates, int itemSugars,
                     int itemProtein, String userID, int numberOfServings, String sourceUrl) {
        this.itemName = itemName;
        this.calories = calories;
        this.itemTotalFat = itemTotalFat;
        this.itemSodium = itemSodium;
        this.itemTotalCarbohydrates = itemTotalCarbohydrates;
        this.itemSugars = itemSugars;
        this.itemProtein = itemProtein;
        this.userID = userID;
        this.numberOfServings = numberOfServings;
        this.sourceUrl = sourceUrl;
    }
}
