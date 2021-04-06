package com.example.bodify.Models;

import java.util.ArrayList;

public class Habits {
    private ArrayList<String> breakfastNames, lunchNames, dinnerNames, otherNames;

    public Habits() {

    }

    public Habits(ArrayList<String> breakfastNames, ArrayList<String> lunchNames, ArrayList<String> dinnerNames, ArrayList<String> otherNames) {
        this.breakfastNames = breakfastNames;
        this.lunchNames = lunchNames;
        this.dinnerNames = dinnerNames;
        this.otherNames = otherNames;
    }

    public ArrayList<String> getBreakfastNames() {
        return breakfastNames;
    }

    public void setBreakfastNames(ArrayList<String> breakfastNames) {
        this.breakfastNames = breakfastNames;
    }

    public ArrayList<String> getLunchNames() {
        return lunchNames;
    }

    public void setLunchNames(ArrayList<String> lunchNames) {
        this.lunchNames = lunchNames;
    }

    public ArrayList<String> getDinnerNames() {
        return dinnerNames;
    }

    public void setDinnerNames(ArrayList<String> dinnerNames) {
        this.dinnerNames = dinnerNames;
    }

    public ArrayList<String> getOtherNames() {
        return otherNames;
    }

    public void setOtherNames(ArrayList<String> otherNames) {
        this.otherNames = otherNames;
    }
}
