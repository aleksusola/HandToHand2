package com.aleksus.handtohand.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aleksus.handtohand.R;
import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;

public class InfoAdActivity extends AppCompatActivity {

    private TextView titleInfo;
    private TextView priceInfo;
    private TextView authorInfo;
    private TextView collectionInfo;
    private TextView createdInfo;
    private TextView descInfo;
    private ImageView iconInfo;

    private String adTitle;
    private String adAuthor;
    private String adCol;
    private String adCreated;
    private String adOwnerName;
    private String adImage;
    private String adImage2;
    private String adImage3;

    private static final String TAG = "MYAPP";

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
        adTitle = getIntent().getStringExtra("title");
        adAuthor = getIntent().getStringExtra("author");
        adCol = getIntent().getStringExtra("collection");
        adCreated = getIntent().getStringExtra("created");
        adOwnerName = getIntent().getStringExtra("ownerName");
        if (adAuthor.equals("Вы")) adAuthor = Backendless.UserService.CurrentUser().getObjectId();

        final String whereClause = "ownerId = '" + adAuthor + "' and name = '" + adTitle + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        Backendless.Data.of("ads_users").find(queryBuilder, new AsyncCallback<List<Map>>() {
            @Override
            public void handleResponse(List<Map> ad) {
                String adPrice = ad.get(0).get("price").toString();
                String adDesc = ad.get(0).get("description").toString();
                adImage = ad.get(0).get("ads_icon").toString();
                adImage2 = ad.get(0).get("ads_icon2").toString();
                adImage3 = ad.get(0).get("ads_icon3").toString();
                Glide
                        .with(InfoAdActivity.this)
                        .load(adImage)
                        .placeholder(R.mipmap.ic_record_voice_over_black)
                        .error(R.drawable.ic_error)
                        .override(500, 500)
                        .crossFade(100)
                        .into(iconInfo);
                titleInfo.setText(adTitle);
                priceInfo.setText("Цена: " + adPrice + " руб.");
                authorInfo.setText("Автор: " + adOwnerName);
                collectionInfo.setText("Коллекция: " + adCol);
                createdInfo.setText(adCreated);
                descInfo.setText(adDesc);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "server reported an error - " + fault.getMessage());}
        });

        iconInfo.setOnClickListener(IViewClickListener);
    }

    View.OnClickListener IViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(InfoAdActivity.this, ViewPagerActivity.class);
            intent.putExtra("image1", adImage);
            intent.putExtra("image2", adImage2);
            intent.putExtra("image3", adImage3);
            startActivity(intent);
        }
    };
}