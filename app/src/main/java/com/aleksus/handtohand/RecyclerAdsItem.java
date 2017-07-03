package com.aleksus.handtohand;

public class RecyclerAdsItem {

    private String title;
    private String author;
    private String collection;
    private String price;

    public RecyclerAdsItem(String title, String author, String collection, String price) {
        this.title = title;
        this.author = author;
        this.collection = collection;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() { return author; }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.title = price;
    }
}
