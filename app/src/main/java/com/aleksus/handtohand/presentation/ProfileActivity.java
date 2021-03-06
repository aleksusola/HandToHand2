package com.aleksus.handtohand.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aleksus.handtohand.DefaultCallback;
import com.aleksus.handtohand.DownloadImageTask;
import com.aleksus.handtohand.R;
import com.aleksus.handtohand.adapter.RecyclerAdsAdapter;
import com.aleksus.handtohand.RecyclerAdsItem;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MYAPP";
    private static long back_pressed;

    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView mRecyclerViewAds;
    private RecyclerAdsAdapter mAdapter;
    private List<RecyclerAdsItem> mListItems;
    private List<RecyclerAdsItem> mListItemsPrice;
    private List<RecyclerAdsItem> mListItemsDate;
    private List<RecyclerAdsItem> mListItemsFilter;
    private TextView mSelectedFilter;

    private String mAdCollection;
    private String mOrder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mSelectedFilter = (TextView) findViewById(R.id.textview_selected_filter);
        mRecyclerViewAds = (RecyclerView) findViewById(R.id.recyclerview_ads);
        mRecyclerViewAds.setHasFixedSize(true);
        mRecyclerViewAds.setLayoutManager(new LinearLayoutManager(this));
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mRecyclerViewAds.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        queryBuilder.setSortBy("created DESC");
                        queryBuilder.setPageSize(25).setOffset(0);
                        Backendless.Persistence.of("ads_users").find(queryBuilder, new AsyncCallback<List<Map>>() {
                            @Override
                            public void handleResponse(final List<Map> foundAds) {
                                mSelectedFilter.setVisibility(View.GONE);
                                if (foundAds.size() == 0) {
                                    Toast.makeText(ProfileActivity.this, "Ничего не найдено", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ProfileActivity.this, "Найдено объявлений " + foundAds.size(), Toast.LENGTH_SHORT).show();
                                    mListItems = new ArrayList<>();
                                    for (int i = 0; i < foundAds.size(); i++)
                                        mListItems.add(new RecyclerAdsItem(foundAds.get(i).get("name").toString(), foundAds.get(i).get("ownerId").toString(), foundAds.get(i).get("collection").toString(), foundAds.get(i).get("price").toString(), foundAds.get(i).get("ads_icon").toString(), foundAds.get(i).get("created").toString()));
                                    mAdapter = new RecyclerAdsAdapter(mListItems, ProfileActivity.this);
                                    mRecyclerViewAds.setAdapter(mAdapter);
                                }
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Log.e(TAG, "server reported an error - " + fault.getMessage());
                                Toast.makeText(ProfileActivity.this, "Ошибка связи с сервером, попробуйте еще раз", Toast.LENGTH_SHORT).show();
                            }
                        });
                        mSwipeRefresh.setRefreshing(false);
                    }
                }, 2500);
            }
        });
        mSwipeRefresh.setColorSchemeResources
                (R.color.light_blue, R.color.middle_blue, R.color.deep_blue);


        BackendlessUser user = Backendless.UserService.CurrentUser();
        if (user != null) {
            View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_profile);
            TextView textView = (TextView) headerLayout.findViewById(R.id.textview_name);
            String name = (String) user.getProperty("firstName");
            textView.setText("Добро пожаловать, " + name);
            TextView textView1 = (TextView) headerLayout.findViewById(R.id.textview_email);
            String email = (String) user.getProperty("email");
            textView1.setText(email);
            TextView textView2 = (TextView) headerLayout.findViewById(R.id.textview_phone);
            String phone = (String) user.getProperty("phone");
            textView2.setText("+7" + phone);
            new DownloadImageTask((ImageView) headerLayout.findViewById(R.id.imageview_avatar)).execute(user.getProperty("avatar").toString());
        } else {
            Toast.makeText(ProfileActivity.this,
                    "User hasn't been logged",
                    Toast.LENGTH_SHORT).show();
        }

        mAdCollection = getIntent().getStringExtra("collection");
        String mAdAuthor = getIntent().getStringExtra("author");
        String mAdOrder = getIntent().getStringExtra("mOrder");
        if (mAdOrder == null) {
            mOrder = "created DESC";
        } else if (mAdOrder.equals("По имени ↑")) {
            mOrder = "name";
        } else if (mAdOrder.equals("По имени ↓")) {
            mOrder = "name DESC";
        } else if (mAdOrder.equals("По цене ↑")) {
            mOrder = "price";
        } else if (mAdOrder.equals("По цене ↓")) {
            mOrder = "price DESC";
        } else if (mAdOrder.equals("По дате ↑")) {
            mOrder = "created";
        } else {
            mOrder = "created DESC";
        }

        if (mAdCollection == null && mAdAuthor == null && mAdOrder == null || mAdCollection.equals("Все коллекции") && mAdAuthor.equals("Все авторы")) {
            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setSortBy(mOrder);
            queryBuilder.setPageSize(25).setOffset(0);
            Backendless.Persistence.of("ads_users").find(queryBuilder, new AsyncCallback<List<Map>>() {
                @Override
                public void handleResponse(final List<Map> foundAds) {
                    if (foundAds.size() == 0) {
                        Toast.makeText(ProfileActivity.this, "Ничего не найдено", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Найдено объявлений " + foundAds.size(), Toast.LENGTH_SHORT).show();
                        mListItems = new ArrayList<>();
                        for (int i = 0; i < foundAds.size(); i++) {
                            mListItems.add(new RecyclerAdsItem(foundAds.get(i).get("name").toString(), foundAds.get(i).get("ownerId").toString(), foundAds.get(i).get("collection").toString(), foundAds.get(i).get("price").toString(), foundAds.get(i).get("ads_icon").toString(), foundAds.get(i).get("created").toString()));
                        }
                        mAdapter = new RecyclerAdsAdapter(mListItems, ProfileActivity.this);
                        mRecyclerViewAds.setAdapter(mAdapter);
                    }
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Log.e(TAG, "server reported an error - " + fault.getMessage());
                    Toast.makeText(ProfileActivity.this, "Ошибка связи с сервером, попробуйте еще раз", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (mAdCollection.equals("Все коллекции") && !mAdAuthor.equals("Все авторы")) {
            mSelectedFilter.setText("Фильтр по всем коллекциям и автору " + mAdAuthor);
            mSelectedFilter.setVisibility(View.VISIBLE);
            String whereClause = "login = '" + mAdAuthor + "'";
            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setWhereClause(whereClause);
            queryBuilder.setPageSize(25).setOffset(0);
            Backendless.Data.of(BackendlessUser.class).find(queryBuilder, new AsyncCallback<List<BackendlessUser>>() {
                @Override
                public void handleResponse(List<BackendlessUser> author) {
                    String whereClause = "ownerId = '" + author.get(0).getObjectId() + "'";
                    final DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                    queryBuilder.setSortBy(mOrder);
                    queryBuilder.setWhereClause(whereClause);
                    Backendless.Data.of("ads_users").find(queryBuilder, new AsyncCallback<List<Map>>() {
                        @Override
                        public void handleResponse(final List<Map> authorFilter) {
                            if (authorFilter.size() == 0) {
                                Toast.makeText(ProfileActivity.this, "Ничего не найдено", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ProfileActivity.this, "Найдено объявлений " + authorFilter.size(), Toast.LENGTH_SHORT).show();
                                mListItemsFilter = new ArrayList<>();
                                for (int i = 0; i < authorFilter.size(); i++) {
                                    mListItemsFilter.add(new RecyclerAdsItem(authorFilter.get(i).get("name").toString(), authorFilter.get(i).get("ownerId").toString(), authorFilter.get(i).get("collection").toString(), authorFilter.get(i).get("price").toString(), authorFilter.get(i).get("ads_icon").toString(), authorFilter.get(i).get("created").toString()));
                                }
                                mAdapter = new RecyclerAdsAdapter(mListItemsFilter, ProfileActivity.this);
                                mRecyclerViewAds.setAdapter(mAdapter);
                            }
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Log.e(TAG, "server reported an error - " + fault.getMessage());
                            Toast.makeText(ProfileActivity.this, "Ошибка связи с сервером, попробуйте еще раз", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Log.e(TAG, "server reported an error - " + fault.getMessage());
                }
            });
        } else if (!mAdCollection.equals("Все коллекции") && mAdAuthor.equals("Все авторы")) {
            mSelectedFilter.setText("Фильтр по коллекции " + mAdCollection + " и всем авторам");
            mSelectedFilter.setVisibility(View.VISIBLE);
            String whereClause = "collection.type = '" + mAdCollection + "'";
            final DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setWhereClause(whereClause);
            queryBuilder.setSortBy(mOrder);
            queryBuilder.setPageSize(25).setOffset(0);
            Backendless.Data.of("ads_users").find(queryBuilder, new AsyncCallback<List<Map>>() {
                @Override
                public void handleResponse(final List<Map> сolFilter) {
                    if (сolFilter.size() == 0) {
                        Toast.makeText(ProfileActivity.this, "Ничего не найдено", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Найдено объявлений " + сolFilter.size(), Toast.LENGTH_SHORT).show();
                        mListItemsFilter = new ArrayList<>();
                        for (int i = 0; i < сolFilter.size(); i++) {
                            mListItemsFilter.add(new RecyclerAdsItem(сolFilter.get(i).get("name").toString(), сolFilter.get(i).get("ownerId").toString(), сolFilter.get(i).get("collection").toString(), сolFilter.get(i).get("price").toString(), сolFilter.get(i).get("ads_icon").toString(), сolFilter.get(i).get("created").toString()));
                        }
                        mAdapter = new RecyclerAdsAdapter(mListItemsFilter, ProfileActivity.this);
                        mRecyclerViewAds.setAdapter(mAdapter);
                    }
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Log.e(TAG, "server reported an error - " + fault.getMessage());
                    Toast.makeText(ProfileActivity.this, "Ошибка связи с сервером, попробуйте еще раз", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (!mAdCollection.equals("Все коллекции") && !mAdAuthor.equals("Все авторы")) {
            mSelectedFilter.setText("Фильтр по коллекции " + mAdCollection + " и автору " + mAdAuthor);
            mSelectedFilter.setVisibility(View.VISIBLE);
            String whereClause = "login = '" + mAdAuthor + "'";
            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setWhereClause(whereClause);
            Backendless.Data.of(BackendlessUser.class).find(queryBuilder, new AsyncCallback<List<BackendlessUser>>() {
                @Override
                public void handleResponse(List<BackendlessUser> author) {
                    String whereClause = "collection.type = '" + mAdCollection + "' and ownerId = '" + author.get(0).getObjectId() + "'";
                    final DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                    queryBuilder.setWhereClause(whereClause);
                    queryBuilder.setSortBy(mOrder);
                    queryBuilder.setPageSize(25).setOffset(0);
                    Backendless.Data.of("ads_users").find(queryBuilder, new AsyncCallback<List<Map>>() {
                        @Override
                        public void handleResponse(final List<Map> fullFilter) {
                            if (fullFilter.size() == 0) {
                                Toast.makeText(ProfileActivity.this, "Ничего не найдено", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ProfileActivity.this, "Найдено объявлений " + fullFilter.size(), Toast.LENGTH_SHORT).show();
                                mListItemsFilter = new ArrayList<>();
                                for (int i = 0; i < fullFilter.size(); i++) {
                                    mListItemsFilter.add(new RecyclerAdsItem(fullFilter.get(i).get("name").toString(), fullFilter.get(i).get("ownerId").toString(), fullFilter.get(i).get("collection").toString(), fullFilter.get(i).get("price").toString(), fullFilter.get(i).get("ads_icon").toString(), fullFilter.get(i).get("created").toString()));
                                }
                                mAdapter = new RecyclerAdsAdapter(mListItemsFilter, ProfileActivity.this);
                                mRecyclerViewAds.setAdapter(mAdapter);
                            }
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Log.e(TAG, "server reported an error - " + fault.getMessage());
                            Toast.makeText(ProfileActivity.this, "Ошибка связи с сервером, попробуйте еще раз", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Log.e(TAG, "server reported an error - " + fault.getMessage());
                    Toast.makeText(ProfileActivity.this, "Ошибка связи с сервером, попробуйте еще раз", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.mnu_item_settings:
                Toast.makeText(ProfileActivity.this, getString(R.string.action_settings), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.mnu_item_about:
                Toast.makeText(ProfileActivity.this, getString(R.string.action_about), Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(this, AboutActivity.class);
                startActivity(intent1);
                break;
            case R.id.mnu_item_sort_by_price:
                Toast.makeText(ProfileActivity.this, getString(R.string.action_sort_by_price), Toast.LENGTH_SHORT).show();
                mSelectedFilter.setVisibility(View.GONE);
                DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                queryBuilder.setSortBy("price");
                queryBuilder.setPageSize(25).setOffset(0);
                Backendless.Persistence.of("ads_users").find(queryBuilder, new AsyncCallback<List<Map>>() {
                    @Override
                    public void handleResponse(final List<Map> priceSortingAds) {
                        if (priceSortingAds.size() == 0) {
                            Toast.makeText(ProfileActivity.this, "Ничего не найдено", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Найдено объявлений " + priceSortingAds.size(), Toast.LENGTH_SHORT).show();
                            mListItemsPrice = new ArrayList<>();
                            for (int i = 0; i < priceSortingAds.size(); i++) {
                                mListItemsPrice.add(new RecyclerAdsItem(priceSortingAds.get(i).get("name").toString(), priceSortingAds.get(i).get("ownerId").toString(), priceSortingAds.get(i).get("collection").toString(), priceSortingAds.get(i).get("price").toString(), priceSortingAds.get(i).get("ads_icon").toString(), priceSortingAds.get(i).get("created").toString()));
                            }
                            mAdapter = new RecyclerAdsAdapter(mListItemsPrice, ProfileActivity.this);
                            mRecyclerViewAds.setAdapter(mAdapter);
                        }
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.e(TAG, "server reported an error - " + fault.getMessage());
                        Toast.makeText(ProfileActivity.this, "Ошибка связи с сервером, попробуйте еще раз", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.mnu_item_sort_by_date:
                Toast.makeText(ProfileActivity.this, getString(R.string.action_sort_by_date), Toast.LENGTH_SHORT).show();
                mSelectedFilter.setVisibility(View.GONE);
                DataQueryBuilder queryBuilder1 = DataQueryBuilder.create();
                queryBuilder1.setSortBy("created");
                queryBuilder1.setPageSize(25).setOffset(0);
                Backendless.Persistence.of("ads_users").find(queryBuilder1, new AsyncCallback<List<Map>>() {
                    @Override
                    public void handleResponse(final List<Map> dateSortingAds) {
                        if (dateSortingAds.size() == 0) {
                            Toast.makeText(ProfileActivity.this, "Ничего не найдено", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Найдено объявлений " + dateSortingAds.size(), Toast.LENGTH_LONG).show();
                            mListItemsDate = new ArrayList<>();
                            for (int i = 0; i < dateSortingAds.size(); i++) {
                                mListItemsDate.add(new RecyclerAdsItem(dateSortingAds.get(i).get("name").toString(), dateSortingAds.get(i).get("ownerId").toString(), dateSortingAds.get(i).get("collection").toString(), dateSortingAds.get(i).get("price").toString(), dateSortingAds.get(i).get("ads_icon").toString(), dateSortingAds.get(i).get("created").toString()));
                            }
                            mAdapter = new RecyclerAdsAdapter(mListItemsDate, ProfileActivity.this);
                            mRecyclerViewAds.setAdapter(mAdapter);
                        }
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.e(TAG, "server reported an error - " + fault.getMessage());
                        Toast.makeText(ProfileActivity.this, "Ошибка связи с сервером, попробуйте еще раз", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.mnu_item_filter:
                Toast.makeText(ProfileActivity.this, getString(R.string.action_filter), Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(this, FilterActivity.class);
                startActivity(intent2);
                break;
            case R.id.mnu_item_exit:
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.addCategory(Intent.CATEGORY_HOME);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (back_pressed + 2000 > System.currentTimeMillis()) {
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.addCategory(Intent.CATEGORY_HOME);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            } else
                Toast.makeText(ProfileActivity.this, "Для выхода нажмите 2 раза 'Назад'", Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.item_my_ads) {
            Toast.makeText(ProfileActivity.this, "Мои объявления", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MyAdsActivity.class));
        } else if (id == R.id.item_new_ad) {
            Toast.makeText(ProfileActivity.this, "Новое объявление", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, NewAdActivity.class));
        } else if (id == R.id.item_fav) {
            Toast.makeText(ProfileActivity.this, "Избранные", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, FavoritesActivity.class));
        } else if (id == R.id.item_settings) {
            Toast.makeText(ProfileActivity.this, getString(R.string.action_settings), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.item_share) {
            Toast.makeText(ProfileActivity.this, "Поделиться", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.item_about) {
            Toast.makeText(ProfileActivity.this, getString(R.string.action_about), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AboutActivity.class));
        } else if (id == R.id.item_user_change) {
            Toast.makeText(ProfileActivity.this, "Смена пользователя", Toast.LENGTH_SHORT).show();
            Backendless.UserService.logout(new DefaultCallback<Void>(this) {
                @Override
                public void handleResponse(Void response) {
                    super.handleResponse(response);
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                    finish();
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    if (fault.getCode().equals("3023")) // Unable to logout: not logged in (session expired, etc.)
                        handleResponse(null);
                    else
                        super.handleFault(fault);
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}