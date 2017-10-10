package com.aleksus.handtohand.presentation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.aleksus.handtohand.R;
import com.aleksus.handtohand.RecyclerAdsItem;

import com.aleksus.handtohand.adapter.RecyclerFavAdsAdapter;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FavoritesActivity extends AppCompatActivity {

    private static final String TAG = "MYAPP";

    private RecyclerView mRecyclerViewFavorites;
    private RecyclerFavAdsAdapter mAdapter;
    private List<RecyclerAdsItem> mListItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavoritesActivity.super.onBackPressed();
            }
        });

        mRecyclerViewFavorites = (RecyclerView) findViewById(R.id.recyclerview_favorites);
        mRecyclerViewFavorites.setHasFixedSize(true);
        mRecyclerViewFavorites.setLayoutManager(new LinearLayoutManager(this));

        BackendlessUser adsOwner = Backendless.UserService.CurrentUser();
        String whereClause = "Users[favorites].objectId='" + adsOwner.getObjectId() + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        Backendless.Data.of("ads_users").find(queryBuilder, new AsyncCallback<List<Map>>() {
            @Override
            public void handleResponse(List<Map> favorites) {
                if (favorites.size() == 0) {
                    Toast.makeText(FavoritesActivity.this, "Ничего не найдено", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(FavoritesActivity.this, "Найдено объявлений " + favorites.size(), Toast.LENGTH_LONG).show();
                    mListItems = new ArrayList<>();
                    for (int i = 0; i < favorites.size(); i++) {
                        mListItems.add(new RecyclerAdsItem(favorites.get(i).get("name").toString(), favorites.get(i).get("ownerId").toString(), favorites.get(i).get("collection").toString(), favorites.get(i).get("price").toString(), favorites.get(i).get("ads_icon").toString(), favorites.get(i).get("created").toString()));
                    }
                    mAdapter = new RecyclerFavAdsAdapter(mListItems, FavoritesActivity.this);
                    mRecyclerViewFavorites.setAdapter(mAdapter);
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "server reported an error - " + fault.getMessage());
            }
        });
    }
}
