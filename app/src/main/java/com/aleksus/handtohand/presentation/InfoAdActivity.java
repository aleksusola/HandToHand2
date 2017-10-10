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

    private static final String TAG = "MYAPP";

    private TextView mTitleInfo;
    private TextView mPriceInfo;
    private TextView mAuthorInfo;
    private TextView mCollectionInfo;
    private TextView mCreatedInfo;
    private TextView mDescInfo;
    private ImageView mIconInfo;

    private String mAdTitle;
    private String mAdCol;
    private String mAdCreated;
    private String mAdOwnerName;
    private String mAdImage;
    private String mAdImage2;
    private String mAdImage3;


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
        mTitleInfo = (TextView) findViewById(R.id.textview_title);
        mPriceInfo = (TextView) findViewById(R.id.textview_price);
        mAuthorInfo = (TextView) findViewById(R.id.textview_author);
        mCollectionInfo = (TextView) findViewById(R.id.textview_collection);
        mCreatedInfo = (TextView) findViewById(R.id.textview_created);
        mDescInfo = (TextView) findViewById(R.id.textview_desc);
        mIconInfo = (ImageView) findViewById(R.id.imageview_icon);
        mAdTitle = getIntent().getStringExtra("title");
        String mAdAuthor = getIntent().getStringExtra("author");
        mAdCol = getIntent().getStringExtra("collection");
        mAdCreated = getIntent().getStringExtra("created");
        mAdOwnerName = getIntent().getStringExtra("ownerName");
        if (mAdAuthor.equals("Вы")) mAdAuthor = Backendless.UserService.CurrentUser().getObjectId();

        final String whereClause = "ownerId = '" + mAdAuthor + "' and name = '" + mAdTitle + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        Backendless.Data.of("ads_users").find(queryBuilder, new AsyncCallback<List<Map>>() {
            @Override
            public void handleResponse(List<Map> ad) {
                String adPrice = ad.get(0).get("price").toString();
                String adDesc = ad.get(0).get("description").toString();
                mAdImage = ad.get(0).get("ads_icon").toString();
                mAdImage2 = ad.get(0).get("ads_icon2").toString();
                mAdImage3 = ad.get(0).get("ads_icon3").toString();
                Glide
                        .with(InfoAdActivity.this)
                        .load(mAdImage)
                        .placeholder(R.mipmap.ic_record_voice_over_black)
                        .error(R.drawable.ic_error)
                        .override(500, 500)
                        .crossFade(100)
                        .into(mIconInfo);
                mTitleInfo.setText(mAdTitle);
                mPriceInfo.setText("Цена: " + adPrice + " руб.");
                mAuthorInfo.setText("Автор: " + mAdOwnerName);
                mCollectionInfo.setText("Коллекция: " + mAdCol);
                mCreatedInfo.setText(mAdCreated);
                mDescInfo.setText(adDesc);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "server reported an error - " + fault.getMessage());
            }
        });

        mIconInfo.setOnClickListener(IViewClickListener);
    }

    View.OnClickListener IViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(InfoAdActivity.this, ViewPagerActivity.class);
            intent.putExtra("image1", mAdImage);
            intent.putExtra("image2", mAdImage2);
            intent.putExtra("image3", mAdImage3);
            startActivity(intent);
        }
    };
}