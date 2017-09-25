package com.aleksus.handtohand.presentation;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.aleksus.handtohand.R;
import com.aleksus.handtohand.adapter.RecyclerMyAdsAdapter;
import com.aleksus.handtohand.RecyclerMyAdsItem;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MyAdsActivity extends AppCompatActivity {


    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView recyclerViewMyAds;
    private RecyclerMyAdsAdapter adapterMy;
    private List<RecyclerMyAdsItem> listItemsMy;

    private static final String TAG = "MYAPP";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myads);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAdsActivity.super.onBackPressed();
            }
        });

        recyclerViewMyAds = (RecyclerView) findViewById(R.id.recyclerViewMyAds);
        recyclerViewMyAds.setHasFixedSize(true);
        recyclerViewMyAds.setLayoutManager(new LinearLayoutManager(this));


        final BackendlessUser AdsOwner = Backendless.UserService.CurrentUser();
        final String whereClause = "ownerId = '" + AdsOwner.getObjectId() + "'";
        final DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        queryBuilder.setSortBy("created DESC");
        queryBuilder.setPageSize(25).setOffset(0);
        Backendless.Data.of("ads_users").find(queryBuilder, new AsyncCallback<List<Map>>() {
            @Override
            public void handleResponse(final List<Map> foundMyAds) {
                if (foundMyAds.size() == 0) {
                    Toast.makeText(MyAdsActivity.this, "Ничего не найдено", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MyAdsActivity.this, "Найдено объявлений " + foundMyAds.size(), Toast.LENGTH_SHORT).show();
                    listItemsMy = new ArrayList<>();
                    for (int i = 0; i < foundMyAds.size(); i++)
                        listItemsMy.add(new RecyclerMyAdsItem(foundMyAds.get(i).get("name").toString(), foundMyAds.get(i).get("description").toString(), foundMyAds.get(i).get("collection").toString(), foundMyAds.get(i).get("price").toString(), foundMyAds.get(i).get("ads_icon").toString(), foundMyAds.get(i).get("created").toString()));
                    adapterMy = new RecyclerMyAdsAdapter(listItemsMy, MyAdsActivity.this);
                    recyclerViewMyAds.setAdapter(adapterMy);
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "server reported an error - " + fault.getMessage());
                Toast.makeText(MyAdsActivity.this, "Ошибка связи с сервером, попробуйте еще раз", Toast.LENGTH_SHORT).show();

            }
        });

        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_my);
        recyclerViewMyAds.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                mSwipeRefresh.setEnabled(topRowVerticalPosition >= 0);

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DataQueryBuilder queryBuilder = DataQueryBuilder.create();

                        queryBuilder.setWhereClause(whereClause);
                        queryBuilder.setSortBy("created DESC");
                        queryBuilder.setPageSize(25).setOffset(0);
                        Backendless.Persistence.of("ads_users").find(queryBuilder, new AsyncCallback<List<Map>>() {
                            @Override
                            public void handleResponse(final List<Map> foundMyAds) {
                                if (foundMyAds.size() == 0) {
                                    Toast.makeText(MyAdsActivity.this, "Ничего не найдено", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MyAdsActivity.this, "Найдено объявлений " + foundMyAds.size(), Toast.LENGTH_SHORT).show();
                                    listItemsMy = new ArrayList<>();
                                    for (int i = 0; i < foundMyAds.size(); i++)
                                        listItemsMy.add(new RecyclerMyAdsItem(foundMyAds.get(i).get("name").toString(), foundMyAds.get(i).get("description").toString(), foundMyAds.get(i).get("collection").toString(), foundMyAds.get(i).get("price").toString(), foundMyAds.get(i).get("ads_icon").toString(), foundMyAds.get(i).get("created").toString()));
                                    adapterMy = new RecyclerMyAdsAdapter(listItemsMy, MyAdsActivity.this);
                                    recyclerViewMyAds.setAdapter(adapterMy);
                                }
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Log.e(TAG, "server reported an error - " + fault.getMessage());
                                Toast.makeText(MyAdsActivity.this, "Ошибка связи с сервером, попробуйте еще раз", Toast.LENGTH_SHORT).show();
                            }
                        });
                        mSwipeRefresh.setRefreshing(false)
                        ;
                    }
                }, 2500);
            }
        });
        mSwipeRefresh.setColorSchemeResources
                (R.color.light_blue, R.color.middle_blue, R.color.deep_blue);
    }
}
