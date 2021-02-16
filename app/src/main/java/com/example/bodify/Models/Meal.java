package com.example.bodify.Models;

import java.util.Objects;

public class Meal {
    String itemName,userID,id,mealType,dayOfWeek,date,UUID;
    int calories,itemTotalFat,itemSodium,itemTotalCarbohydrates,itemSugars,itemProtein,numberOfServings,originalServings;

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getOriginalServings() {
        return originalServings;
    }

    public void setOriginalServings(int originalServings) {
        this.originalServings = originalServings;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public Meal() {

    }

    public Meal(String itemName, String userID, int calories, int itemTotalFat, int itemSodium, int itemTotalCarbohydrates, int itemSugars, int itemProtein, int numberOfServings, String mealType,String dayOfWeek,String date,int originalServings,String UUID) {
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
        this.date = date;
        this.originalServings = originalServings;
        this.UUID = UUID;
    }

    @Override
    public String toString() {
        return "Meal{" +
                "itemName='" + itemName + '\'' +
                ", userID='" + userID + '\'' +
                ", id='" + id + '\'' +
                ", mealType='" + mealType + '\'' +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", date='" + date + '\'' +
                ", UUID='" + UUID + '\'' +
                ", calories=" + calories +
                ", itemTotalFat=" + itemTotalFat +
                ", itemSodium=" + itemSodium +
                ", itemTotalCarbohydrates=" + itemTotalCarbohydrates +
                ", itemSugars=" + itemSugars +
                ", itemProtein=" + itemProtein +
                ", numberOfServings=" + numberOfServings +
                ", originalServings=" + originalServings +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meal meal1 = (Meal) o;
        return calories == meal1.calories &&
                itemTotalFat == meal1.itemTotalFat &&
                itemSodium == meal1.itemSodium &&
                itemTotalCarbohydrates == meal1.itemTotalCarbohydrates &&
                itemSugars == meal1.itemSugars &&
                itemProtein == meal1.itemProtein &&
                numberOfServings == meal1.numberOfServings &&
                originalServings == meal1.originalServings &&
                itemName.equals(meal1.itemName) &&
                userID.equals(meal1.userID) &&
                id.equals(meal1.id) &&
                mealType.equals(meal1.mealType) &&
                dayOfWeek.equals(meal1.dayOfWeek) &&
                date.equals(meal1.date) &&
                UUID.equals(meal1.UUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemName, userID, id, mealType, dayOfWeek, date, UUID, calories, itemTotalFat, itemSodium, itemTotalCarbohydrates, itemSugars, itemProtein, numberOfServings, originalServings);
    }
}
