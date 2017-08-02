package com.aleksus.handtohand.presentation;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.aleksus.handtohand.R;
import com.aleksus.handtohand.RecyclerAdsAdapter;
import com.aleksus.handtohand.RecyclerAdsItem;
import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FilterActivity extends AppCompatActivity implements View.OnClickListener{

    private Spinner spinnerCollection;
    private Spinner spinnerAuthor;
    private RecyclerView recyclerViewFilterAds;
    private RecyclerAdsAdapter adapterFilter;
    private List<RecyclerAdsItem> listItemsFilter;

    private Button filterButton;


    private String collectionSelected;
    private String authorSelected;
    private String authorId;

    private static final String TAG = "MYAPP";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        spinnerCollection = (Spinner) findViewById(R.id.collection_filter_select);
        spinnerAuthor = (Spinner) findViewById(R.id.author_filter_select);

        filterButton = (Button) findViewById(R.id.filter_button);
        filterButton.setOnClickListener(this);

        recyclerViewFilterAds = (RecyclerView) findViewById(R.id.recyclerViewFilterAds);
        recyclerViewFilterAds.setHasFixedSize(true);
        recyclerViewFilterAds.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onClick(View view) {

        collectionSelected = spinnerCollection.getSelectedItem().toString().trim();
        authorSelected = spinnerAuthor.getSelectedItem().toString().trim();
        if (authorSelected.equals("aleks")){
            authorId = "126B32D3-61C9-1C77-FF95-4E2C900A3400";
        } else if (authorSelected.equals("lexus")) {
            authorId = "70A29000-86C9-C8A5-FFA6-44C30BDCBA00";
        } else if (authorSelected.equals("pizza")) {
            authorId = "07BE4E48-F08B-7197-FF0C-AEC3B433AE00";
        } else if (authorSelected.equals("test")) {
            authorId = "07BE4E48-F08B-7197-FF0C-AEC3B433AE00";
        } else {authorId=null;}


        if (collectionSelected.equals("Все коллекции")  && authorSelected.equals("Все авторы")) {
            Backendless.Data.of( "ads_users" ).find( new AsyncCallback<List<Map>>(){
                @Override
                public void handleResponse(final List<Map> NoFilter ) {
                    Toast.makeText(FilterActivity.this, "Готово", Toast.LENGTH_LONG).show();
                    listItemsFilter = new ArrayList<>();
                    Backendless.Data.of( "ads_users" ).getObjectCount( new AsyncCallback<Integer>() {
                        @Override
                        public void handleResponse( Integer cnt ) {
                            listItemsFilter = new ArrayList<>();
                            for (int i = 0; i<cnt; i++) {
                                listItemsFilter.add(new RecyclerAdsItem( NoFilter.get(i).get( "name" ).toString(), NoFilter.get(i).get( "description" ).toString(),  NoFilter.get(i).get("ownerId").toString(), "Коллекция: " + NoFilter.get(i).get("collection").toString(), "Цена: " + NoFilter.get(i).get( "price" ).toString(), NoFilter.get(i).get("ads_icon").toString() ));
                            }
                            //Set adapter
                            adapterFilter = new RecyclerAdsAdapter(listItemsFilter, FilterActivity.this);
                            recyclerViewFilterAds.setAdapter(adapterFilter);
                        }
                        @Override
                        public void handleFault( BackendlessFault backendlessFault ) {
                            Log.i( TAG, "error - " + backendlessFault.getMessage() );
                        }
                    });
                }
                @Override
                public void handleFault( BackendlessFault fault ) {
                    Log.e( TAG, "server reported an error - " + fault.getMessage() );
                }
            });
        } else if (collectionSelected.equals("Все коллекции")  && !authorSelected.equals("Все авторы")) {
            String whereClause = "ownerId = '"+ authorId +"'";
            final DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setWhereClause( whereClause );
            Backendless.Data.of( "ads_users" ).find( queryBuilder, new AsyncCallback<List<Map>>(){
                @Override
                public void handleResponse(final List<Map> AuthorFilter ) {
                    Toast.makeText(FilterActivity.this, "Готово", Toast.LENGTH_LONG).show();
                    listItemsFilter = new ArrayList<>();
                    Backendless.Data.of( "ads_users" ).getObjectCount( queryBuilder, new AsyncCallback<Integer>() {
                        @Override
                        public void handleResponse( Integer cnt ) {
                            listItemsFilter = new ArrayList<>();
                            for (int i = 0; i<cnt; i++) {
                                listItemsFilter.add(new RecyclerAdsItem( AuthorFilter.get(i).get( "name" ).toString(), AuthorFilter.get(i).get( "description" ).toString(),  AuthorFilter.get(i).get("ownerId").toString(), "Коллекция: " + AuthorFilter.get(i).get("collection").toString(), "Цена: " + AuthorFilter.get(i).get( "price" ).toString(), AuthorFilter.get(i).get("ads_icon").toString() ));
                            }
                            //Set adapter
                            adapterFilter = new RecyclerAdsAdapter(listItemsFilter, FilterActivity.this);
                            recyclerViewFilterAds.setAdapter(adapterFilter);
                        }
                        @Override
                        public void handleFault( BackendlessFault backendlessFault ) {
                            Log.i( TAG, "error - " + backendlessFault.getMessage() );
                        }
                    });
                }
                @Override
                public void handleFault( BackendlessFault fault ) {
                    Log.e( TAG, "server reported an error - " + fault.getMessage() );
                }
            });
        } else if (!collectionSelected.equals("Все коллекции")  && authorSelected.equals("Все авторы")) {
            String whereClause = "collection.type = '"+ collectionSelected +"'";
            final DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setWhereClause( whereClause );
            Backendless.Data.of( "ads_users" ).find( queryBuilder, new AsyncCallback<List<Map>>(){
                @Override
                public void handleResponse(final List<Map> CollectionFilter ) {
                    Toast.makeText(FilterActivity.this, "Готово", Toast.LENGTH_LONG).show();
                    listItemsFilter = new ArrayList<>();
                    Backendless.Data.of( "ads_users" ).getObjectCount( queryBuilder, new AsyncCallback<Integer>() {
                        @Override
                        public void handleResponse( Integer cnt ) {
                            listItemsFilter = new ArrayList<>();
                            for (int i = 0; i<cnt; i++) {
                                listItemsFilter.add(new RecyclerAdsItem( CollectionFilter.get(i).get( "name" ).toString(), CollectionFilter.get(i).get( "description" ).toString(),  CollectionFilter.get(i).get("ownerId").toString(), "Коллекция: " + CollectionFilter.get(i).get("collection").toString(), "Цена: " + CollectionFilter.get(i).get( "price" ).toString(), CollectionFilter.get(i).get("ads_icon").toString() ));
                            }
                            //Set adapter
                            adapterFilter = new RecyclerAdsAdapter(listItemsFilter, FilterActivity.this);
                            recyclerViewFilterAds.setAdapter(adapterFilter);
                        }
                        @Override
                        public void handleFault( BackendlessFault backendlessFault ) {
                            Log.i( TAG, "error - " + backendlessFault.getMessage() );
                        }
                    });
                }
                @Override
                public void handleFault( BackendlessFault fault ) {
                    Log.e( TAG, "server reported an error - " + fault.getMessage() );
                }
            });
        } else if (!collectionSelected.equals("Все коллекции")  && !authorSelected.equals("Все авторы")) {
            String whereClause = "collection.type = '"+ collectionSelected +"' and ownerId = '"+ authorId +"'";
            final DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setWhereClause( whereClause );
            Backendless.Data.of( "ads_users" ).find( queryBuilder, new AsyncCallback<List<Map>>(){
                @Override
                public void handleResponse(final List<Map> FullFilter ) {
                    Toast.makeText(FilterActivity.this, "Готово", Toast.LENGTH_LONG).show();
                    listItemsFilter = new ArrayList<>();
                    Backendless.Data.of( "ads_users" ).getObjectCount( queryBuilder, new AsyncCallback<Integer>() {
                        @Override
                        public void handleResponse( Integer cnt ) {
                            listItemsFilter = new ArrayList<>();
                            for (int i = 0; i<cnt; i++) {
                                listItemsFilter.add(new RecyclerAdsItem( FullFilter.get(i).get( "name" ).toString(), FullFilter.get(i).get( "description" ).toString(),  FullFilter.get(i).get("ownerId").toString(), "Коллекция: " + FullFilter.get(i).get("collection").toString(), "Цена: " + FullFilter.get(i).get( "price" ).toString(), FullFilter.get(i).get("ads_icon").toString() ));
                            }
                            //Set adapter
                            adapterFilter = new RecyclerAdsAdapter(listItemsFilter, FilterActivity.this);
                            recyclerViewFilterAds.setAdapter(adapterFilter);
                        }
                        @Override
                        public void handleFault( BackendlessFault backendlessFault ) {
                            Log.i( TAG, "error - " + backendlessFault.getMessage() );
                        }
                    });
                }
                @Override
                public void handleFault( BackendlessFault fault ) {
                    Log.e( TAG, "server reported an error - " + fault.getMessage() );
                }
            });
        }



    }
}
