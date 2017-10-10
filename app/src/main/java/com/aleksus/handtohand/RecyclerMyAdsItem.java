package com.aleksus.handtohand;

public class RecyclerMyAdsItem {

    private String mTitle;
    private String mCollection;
    private String mPrice;
    private String mPhoto;
    private String mCreated;

    public RecyclerMyAdsItem(String title, String collection, String price, String photo, String created) {
        this.mTitle = title;
        this.mCollection = collection;
        this.mPrice = price;
        this.mPhoto = photo;
        this.mCreated = created;
    }

    public String getTitle() {
        return mTitle;
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
