package com.aleksus.handtohand;

import java.net.URI;
import java.util.Objects;

public class RecyclerAdsItem {

    private String title;
    private String desc;
    private String author;
    private String collection;
    private String price;
    private String photo;

    public RecyclerAdsItem(String title, String desc, String author, String collection, String price, String photo) {
        this.title = title;
        this.desc = desc;
        this.author = author;
        this.collection = collection;
        this.price = price;
        this.photo = photo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

}
