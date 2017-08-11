package com.aleksus.handtohand.presentation;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.aleksus.handtohand.R;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.ArrayList;
import java.util.List;

public class FilterActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner spinnerCollection;
    private Spinner spinnerAuthor;
    private List<Object> authors;

    private static final String TAG = "MYAPP";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterActivity.super.onBackPressed();
            }
        });
        spinnerCollection = (Spinner) findViewById(R.id.collection_filter_select);
        spinnerAuthor = (Spinner) findViewById(R.id.author_filter_select);

        Button filterButton = (Button) findViewById(R.id.filter_button);
        filterButton.setOnClickListener(this);

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

        String collectionSelected = spinnerCollection.getSelectedItem().toString();
        String authorSelected = spinnerAuthor.getSelectedItem().toString();
        Intent intent = new Intent(FilterActivity.this, ProfileActivity.class);
        intent.putExtra("collection", collectionSelected);
        intent.putExtra("author", authorSelected);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(FilterActivity.this, ProfileActivity.class));

    }
}
