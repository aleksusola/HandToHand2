package com.aleksus.handtohand.presentation;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.aleksus.handtohand.R;
import com.aleksus.handtohand.RecyclerAdsAdapter;
import com.aleksus.handtohand.RecyclerAdsItem;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FilterActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner spinnerCollection;
    private Spinner spinnerAuthor;
    private RecyclerView recyclerViewFilterAds;
    private RecyclerAdsAdapter adapterFilter;
    private List<RecyclerAdsItem> listItemsFilter;
    private List<Object> authors;

    private String collectionSelected;
    private String authorSelected;

    private static final String TAG = "MYAPP";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        spinnerCollection = (Spinner) findViewById(R.id.collection_filter_select);
        spinnerAuthor = (Spinner) findViewById(R.id.author_filter_select);

        Button filterButton = (Button) findViewById(R.id.filter_button);
        filterButton.setOnClickListener(this);

        recyclerViewFilterAds = (RecyclerView) findViewById(R.id.recyclerViewFilterAds);
        recyclerViewFilterAds.setHasFixedSize(true);
        recyclerViewFilterAds.setLayoutManager(new LinearLayoutManager(this));

        Backendless.Persistence.of(BackendlessUser.class).find(new AsyncCallback<List<BackendlessUser>>() {
            @Override
            public void handleResponse(List<BackendlessUser> users) {
                authors = new ArrayList<>();
                authors.add(0, "Все авторы");
                for (int i = 0; i < users.size(); i++) {
                    authors.add(i + 1, users.get(i).getProperty("login"));
                }
                ArrayAdapter<Object> adapter = new ArrayAdapter<>(FilterActivity.this, android.R.layout.simple_spinner_item, authors);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerAuthor.setAdapter(adapter);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "server reported an error - " + fault.getMessage());
            }
        });
    }

    @Override
    public void onClick(View view) {

        collectionSelected = spinnerCollection.getSelectedItem().toString();
        authorSelected = spinnerAuthor.getSelectedItem().toString();
        Intent intent = new Intent(FilterActivity.this, ProfileActivity.class);
        intent.putExtra("collection", collectionSelected);
        intent.putExtra("author", authorSelected);
        startActivity(intent);
        recyclerViewFilterAds.removeAllViewsInLayout();


    }
}
