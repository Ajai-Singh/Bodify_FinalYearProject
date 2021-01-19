package com.example.bodify.Models;

public class Meal {
    String itemName,userID,id,mealType,dayOfWeek;
    int calories,itemTotalFat,itemSodium,itemTotalCarbohydrates,itemSugars,itemProtein,numberOfServings;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getNumberOfServings() {
        return numberOfServings;
    }

    public void setNumberOfServings(int numberOfServings) {
        this.numberOfServings = numberOfServings;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Meal() {

    }

    public Meal(String itemName, String userID, int calories, int itemTotalFat, int itemSodium, int itemTotalCarbohydrates, int itemSugars, int itemProtein, int numberOfServings, String mealType,String dayOfWeek) {
        this.itemName = itemName;
        this.userID = userID;
        this.calories = calories;
        this.itemTotalFat = itemTotalFat;
        this.itemSodium = itemSodium;
        this.itemTotalCarbohydrates = itemTotalCarbohydrates;
        this.itemSugars = itemSugars;
        this.itemProtein = itemProtein;
        this.numberOfServings = numberOfServings;
        this.mealType = mealType;
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public String toString() {
        return "Meal{" +
                "itemName='" + itemName + '\'' +
                ", userID='" + userID + '\'' +
                ", id='" + id + '\'' +
                ", mealType='" + mealType + '\'' +
                ", calories=" + calories +
                ", itemTotalFat=" + itemTotalFat +
                ", itemSodium=" + itemSodium +
                ", itemTotalCarbohydrates=" + itemTotalCarbohydrates +
                ", itemSugars=" + itemSugars +
                ", itemProtein=" + itemProtein +
                ", numberOfServings=" + numberOfServings +
                '}';
    }
}
