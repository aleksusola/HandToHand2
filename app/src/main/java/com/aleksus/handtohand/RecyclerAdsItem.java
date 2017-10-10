package com.aleksus.handtohand;

public class RecyclerAdsItem {

    private String mTitle;
    private String mAuthor;
    private String mCollection;
    private String mPrice;
    private String mPhoto;
    private String mCreated;

    public RecyclerAdsItem(String title, String author, String collection, String price, String photo, String created) {
        this.mTitle = title;
        this.mAuthor = author;
        this.mCollection = collection;
        this.mPrice = price;
        this.mPhoto = photo;
        this.mCreated = created;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getCollection() {
        return mCollection;
    }

    public String getPrice() {
        return mPrice;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public String getCreated() {
        return mCreated;
    }

}
