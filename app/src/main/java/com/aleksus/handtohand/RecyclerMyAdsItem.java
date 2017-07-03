package com.aleksus.handtohand;

public class RecyclerMyAdsItem {

    private String title;
    private String collection;
    private String price;

    public RecyclerMyAdsItem(String title, String collection, String price) {
        this.title = title;
        this.collection = collection;
        this.price = price;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
        this.price = price;
    }
}
