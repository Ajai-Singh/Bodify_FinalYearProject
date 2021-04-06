package com.example.bodify.Models;

public class Grocery {
    private String name, imageUrl, price, pageLink;

    public Grocery(String name, String imageUrl, String price, String pageLink) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.pageLink = pageLink;
    }

    public Grocery() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPageLink() {
        return pageLink;
    }

    public void setPageLink(String pageLink) {
        this.pageLink = pageLink;
    }
}
