package com.aleksus.handtohand.presentation;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aleksus.handtohand.R;

public class InfoAdActivity extends AppCompatActivity {

    private TextView titleInfo;
    private TextView priceInfo;
    private TextView authorInfo;
    private TextView collectionInfo;
    private TextView createdInfo;
    private TextView descInfo;

    private ImageView iconInfo;
    private Bitmap infoImage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoAdActivity.super.onBackPressed();
            }
        });
        titleInfo = (TextView) findViewById(R.id.info_title);
        priceInfo = (TextView) findViewById(R.id.info_price);
        authorInfo = (TextView) findViewById(R.id.info_author);
        collectionInfo = (TextView) findViewById(R.id.info_collection);
        createdInfo = (TextView) findViewById(R.id.info_created);
        descInfo = (TextView) findViewById(R.id.info_desc);
        iconInfo = (ImageView) findViewById(R.id.info_icon);
        String adTitle = getIntent().getStringExtra("title");
        String adPrice = getIntent().getStringExtra("price");
        String adAuthor = getIntent().getStringExtra("author");
        String adCollection = getIntent().getStringExtra("collection");
        String adCreated = getIntent().getStringExtra("created");
        String adDesc = getIntent().getStringExtra("desc");
        Bundle extras = getIntent().getExtras();
        infoImage = extras.getParcelable("imagebitmap");
        iconInfo.setImageBitmap(infoImage);
        titleInfo.setText(adTitle);
        priceInfo.setText("Цена: " + adPrice + " руб.");
        authorInfo.setText("Автор: " + adAuthor);
        collectionInfo.setText("Коллекция: " + adCollection);
        createdInfo.setText(adCreated);
        descInfo.setText(adDesc);
    }
}
