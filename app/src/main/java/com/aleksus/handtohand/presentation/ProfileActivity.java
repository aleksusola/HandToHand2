package com.aleksus.handtohand.presentation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
import com.aleksus.handtohand.RecyclerAdsAdapter;
import com.aleksus.handtohand.RecyclerAdsItem;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerViewAds;
    private RecyclerAdsAdapter adapter;
    private List<RecyclerAdsItem> listItems;

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        BackendlessUser user = Backendless.UserService.CurrentUser();
        if( user != null ) {
            View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_profile);
            TextView textView = (TextView) headerLayout.findViewById(R.id.userName);
            String name = (String) user.getProperty( "name" );
            textView.setText("Добро пожаловать, " + name);
            TextView textView1 = (TextView) headerLayout.findViewById(R.id.userMail);
            String email = (String) user.getProperty( "email" );
            textView1.setText(email);
            TextView textView2 = (TextView) headerLayout.findViewById(R.id.userPhone);
            String phone = (String) user.getProperty( "phone" );
            textView2.setText("+7" + phone);
            new DownloadImageTask((ImageView) headerLayout.findViewById(R.id.userIcon)).execute(user.getProperty( "avatar" ).toString());
        }
        else {
            Toast.makeText( ProfileActivity.this,
                    "User hasn't been logged",
                    Toast.LENGTH_SHORT ).show();
        }

        recyclerViewAds = (RecyclerView) findViewById(R.id.recyclerViewAds);
        recyclerViewAds.setHasFixedSize(true);
        recyclerViewAds.setLayoutManager(new LinearLayoutManager(this));

        listItems = new ArrayList<>();

        Backendless.Persistence.of( "ads_users" ).find( new AsyncCallback<List<Map>>(){
            @Override
            public void handleResponse(final List<Map> foundAds ) {

                Backendless.Data.of( "ads_users" ).getObjectCount( new AsyncCallback<Integer>() {
                    @Override
                    public void handleResponse( Integer cnt ) {

                        for (int i = 0; i<cnt; i++) {
                            listItems.add(new RecyclerAdsItem( foundAds.get(i).get( "name" ).toString(), foundAds.get(i).get( "description" ).toString(),  foundAds.get(i).get("ownerId").toString(), "Коллекция: " + foundAds.get(i).get("collection").toString(), "Цена: " + foundAds.get(i).get( "price" ).toString(), foundAds.get(i).get("ads_icon").toString() ));
                        }
                        //Set adapter
                        adapter = new RecyclerAdsAdapter(listItems, ProfileActivity.this);
                        recyclerViewAds.setAdapter(adapter);
                    }

                    @Override
                    public void handleFault( BackendlessFault backendlessFault )
                    {
                        Log.i( "MYAPP", "error - " + backendlessFault.getMessage() );
                    }
                } );
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.i( "MYAPP", "error");
            }
        });

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
            case R.id.action_settings:
                Toast.makeText(ProfileActivity.this, getString(R.string.action_settings), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_about:
                Toast.makeText(ProfileActivity.this, getString(R.string.action_about), Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(this, AboutActivity.class);
                startActivity(intent1);
                break;
            case R.id.action_exit:
                Backendless.UserService.logout( new DefaultCallback<Void>( this ) {
                    @Override
                    public void handleResponse( Void response ) {
                        super.handleResponse( response );
                        startActivity( new Intent( ProfileActivity.this, LoginActivity.class ) );
                        finish();
                    }

                    @Override
                    public void handleFault( BackendlessFault fault ) {
                        if( fault.getCode().equals( "3023" ) ) // Unable to logout: not logged in (session expired, etc.)
                            handleResponse( null );
                        else
                            super.handleFault( fault );
                    }
                } );
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_myads) {
            Toast.makeText(ProfileActivity.this, "Мои объявления", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MyAdsActivity.class));

        } else if (id == R.id.nav_new_ad) {
            Toast.makeText(ProfileActivity.this, "Новое объявление", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, NewAdActivity.class));
//        } else if (id == R.id.nav_slideshow) {
//            Toast.makeText(ProfileActivity.this, getString(R.string.action_settings), Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_manage) {
            Toast.makeText(ProfileActivity.this, getString(R.string.action_settings), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_share) {
            Toast.makeText(ProfileActivity.this, "Поделиться", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_about) {
            Toast.makeText(ProfileActivity.this, getString(R.string.action_about), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AboutActivity.class));
        } else if (id == R.id.nav_exit) {
            Backendless.UserService.logout( new DefaultCallback<Void>( this ) {
                @Override
                public void handleResponse( Void response ) {
                    super.handleResponse( response );
                    startActivity( new Intent( ProfileActivity.this, LoginActivity.class ) );
                    finish();
                }

                @Override
                public void handleFault( BackendlessFault fault ) {
                    if( fault.getCode().equals( "3023" ) ) // Unable to logout: not logged in (session expired, etc.)
                        handleResponse( null );
                    else
                        super.handleFault( fault );
                }
            } );
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
