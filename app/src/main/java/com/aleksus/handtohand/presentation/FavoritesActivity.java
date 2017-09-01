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

    private RecyclerView recyclerViewFavorites;
    private RecyclerFavAdsAdapter adapter;
    private List<RecyclerAdsItem> listItems;

    private static final String TAG = "MYAPP";

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

        recyclerViewFavorites = (RecyclerView) findViewById(R.id.recyclerViewFavorites);
        recyclerViewFavorites.setHasFixedSize(true);
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(this));

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
                    listItems = new ArrayList<>();
                    for (int i = 0; i < favorites.size(); i++) {
                        listItems.add(new RecyclerAdsItem(favorites.get(i).get("name").toString(), favorites.get(i).get("description").toString(), favorites.get(i).get("ownerId").toString(), favorites.get(i).get("collection").toString(), favorites.get(i).get("price").toString(), favorites.get(i).get("ads_icon").toString(), favorites.get(i).get("created").toString()));
                    }
                    adapter = new RecyclerFavAdsAdapter(listItems, FavoritesActivity.this);
                    recyclerViewFavorites.setAdapter(adapter);
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "server reported an error - " + fault.getMessage());
            }
        });
    }
}
