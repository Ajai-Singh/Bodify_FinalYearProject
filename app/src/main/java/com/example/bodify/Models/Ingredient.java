package com.example.bodify.Models;

public class Ingredient {
    private String name, unit;
    private Double amount;

    public Ingredient() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Ingredient(String name, Double amount, String unit) {
        this.name = name;
        this.amount = amount;
        this.unit = unit;
    }
}
