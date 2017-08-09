package com.aleksus.handtohand;

import java.util.Date;

public class RecyclerMyAdsItem {

    private String title;
    private String desc;
    private String collection;
    private String price;
    private String photo;
    private String created;

    public RecyclerMyAdsItem(String title, String desc, String collection, String price, String photo, String created) {
        this.title = title;
        this.desc = desc;
        this.collection = collection;
        this.price = price;
        this.photo = photo;
        this.created = created;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
