package com.aleksus.handtohand.presentation;


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

        collectionSelected = spinnerCollection.getSelectedItem().toString().trim();
        String authorSelected = spinnerAuthor.getSelectedItem().toString().trim();
        recyclerViewFilterAds.removeAllViewsInLayout();

        if (collectionSelected.equals("Все коллекции") && authorSelected.equals("Все авторы")) {
            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setPageSize(25).setOffset(0);
            Backendless.Data.of("ads_users").find(queryBuilder, new AsyncCallback<List<Map>>() {
                @Override
                public void handleResponse(final List<Map> noFilter) {
                    if (noFilter.size() == 0) {
                        Toast.makeText(FilterActivity.this, "Ничего не найдено", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(FilterActivity.this, "Найдено объявлений " + noFilter.size(), Toast.LENGTH_LONG).show();
                        listItemsFilter = new ArrayList<>();
                        for (int i = 0; i < noFilter.size(); i++) {
                            listItemsFilter.add(new RecyclerAdsItem(noFilter.get(i).get("name").toString(), noFilter.get(i).get("description").toString(), noFilter.get(i).get("ownerId").toString(), "Коллекция: " + noFilter.get(i).get("collection").toString(), "Цена: " + noFilter.get(i).get("price").toString(), noFilter.get(i).get("ads_icon").toString()));
                        }
                        adapterFilter = new RecyclerAdsAdapter(listItemsFilter, FilterActivity.this);
                        recyclerViewFilterAds.setAdapter(adapterFilter);
                    }
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Log.e(TAG, "server reported an error - " + fault.getMessage());
                }
            });
        } else if (collectionSelected.equals("Все коллекции") && !authorSelected.equals("Все авторы")) {
            String whereClause = "login = '" + authorSelected + "'";
            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setWhereClause(whereClause);
            queryBuilder.setPageSize(25).setOffset(0);
            Backendless.Data.of(BackendlessUser.class).find(queryBuilder, new AsyncCallback<List<BackendlessUser>>() {
                @Override
                public void handleResponse(List<BackendlessUser> author) {
                    String whereClause = "ownerId = '" + author.get(0).getObjectId() + "'";
                    final DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                    queryBuilder.setWhereClause(whereClause);
                    Backendless.Data.of("ads_users").find(queryBuilder, new AsyncCallback<List<Map>>() {
                        @Override
                        public void handleResponse(final List<Map> authorFilter) {
                            if (authorFilter.size() == 0) {
                                Toast.makeText(FilterActivity.this, "Ничего не найдено", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(FilterActivity.this, "Найдено объявлений " + authorFilter.size(), Toast.LENGTH_LONG).show();
                                listItemsFilter = new ArrayList<>();
                                for (int i = 0; i < authorFilter.size(); i++) {
                                    listItemsFilter.add(new RecyclerAdsItem(authorFilter.get(i).get("name").toString(), authorFilter.get(i).get("description").toString(), authorFilter.get(i).get("ownerId").toString(), "Коллекция: " + authorFilter.get(i).get("collection").toString(), "Цена: " + authorFilter.get(i).get("price").toString(), authorFilter.get(i).get("ads_icon").toString()));
                                }
                                adapterFilter = new RecyclerAdsAdapter(listItemsFilter, FilterActivity.this);
                                recyclerViewFilterAds.setAdapter(adapterFilter);
                            }
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Log.e(TAG, "server reported an error - " + fault.getMessage());
                        }
                    });
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Log.e(TAG, "server reported an error - " + fault.getMessage());
                }
            });
        } else if (!collectionSelected.equals("Все коллекции") && authorSelected.equals("Все авторы")) {
            String whereClause = "collection.type = '" + collectionSelected + "'";
            final DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setWhereClause(whereClause);
            queryBuilder.setPageSize(25).setOffset(0);
            Backendless.Data.of("ads_users").find(queryBuilder, new AsyncCallback<List<Map>>() {
                @Override
                public void handleResponse(final List<Map> сollectionFilter) {
                    if (сollectionFilter.size() == 0) {
                        Toast.makeText(FilterActivity.this, "Ничего не найдено", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(FilterActivity.this, "Найдено объявлений " + сollectionFilter.size(), Toast.LENGTH_LONG).show();
                        listItemsFilter = new ArrayList<>();
                        for (int i = 0; i < сollectionFilter.size(); i++) {
                            listItemsFilter.add(new RecyclerAdsItem(сollectionFilter.get(i).get("name").toString(), сollectionFilter.get(i).get("description").toString(), сollectionFilter.get(i).get("ownerId").toString(), "Коллекция: " + сollectionFilter.get(i).get("collection").toString(), "Цена: " + сollectionFilter.get(i).get("price").toString(), сollectionFilter.get(i).get("ads_icon").toString()));
                        }
                        adapterFilter = new RecyclerAdsAdapter(listItemsFilter, FilterActivity.this);
                        recyclerViewFilterAds.setAdapter(adapterFilter);
                    }
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Log.e(TAG, "server reported an error - " + fault.getMessage());
                }
            });
        } else if (!collectionSelected.equals("Все коллекции") && !authorSelected.equals("Все авторы")) {
            String whereClause = "login = '" + authorSelected + "'";
            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setWhereClause(whereClause);
            Backendless.Data.of(BackendlessUser.class).find(queryBuilder, new AsyncCallback<List<BackendlessUser>>() {
                @Override
                public void handleResponse(List<BackendlessUser> author) {
                    String whereClause = "collection.type = '" + collectionSelected + "' and ownerId = '" + author.get(0).getObjectId() + "'";
                    final DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                    queryBuilder.setWhereClause(whereClause);
                    queryBuilder.setPageSize(25).setOffset(0);
                    Backendless.Data.of("ads_users").find(queryBuilder, new AsyncCallback<List<Map>>() {
                        @Override
                        public void handleResponse(final List<Map> fullFilter) {
                            if (fullFilter.size() == 0) {
                                Toast.makeText(FilterActivity.this, "Ничего не найдено", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(FilterActivity.this, "Найдено объявлений " + fullFilter.size(), Toast.LENGTH_LONG).show();
                                listItemsFilter = new ArrayList<>();
                                for (int i = 0; i < fullFilter.size(); i++) {
                                    listItemsFilter.add(new RecyclerAdsItem(fullFilter.get(i).get("name").toString(), fullFilter.get(i).get("description").toString(), fullFilter.get(i).get("ownerId").toString(), "Коллекция: " + fullFilter.get(i).get("collection").toString(), "Цена: " + fullFilter.get(i).get("price").toString(), fullFilter.get(i).get("ads_icon").toString()));
                                }
                                adapterFilter = new RecyclerAdsAdapter(listItemsFilter, FilterActivity.this);
                                recyclerViewFilterAds.setAdapter(adapterFilter);
                            }
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Log.e(TAG, "server reported an error - " + fault.getMessage());
                        }
                    });
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Log.e(TAG, "server reported an error - " + fault.getMessage());
                }
            });
        }
    }
}
