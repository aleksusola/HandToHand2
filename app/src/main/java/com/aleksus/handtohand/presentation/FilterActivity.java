package com.aleksus.handtohand.presentation;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.aleksus.handtohand.R;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.ArrayList;
import java.util.List;

public class FilterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MYAPP";

    private Spinner mSpinnerCollection;
    private Spinner mSpinnerAuthor;
    private Spinner mSpinnerOrder;
    private List<Object> mAuthors;

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
        mSpinnerCollection = (Spinner) findViewById(R.id.spinner_collection_filter);
        mSpinnerAuthor = (Spinner) findViewById(R.id.spinner_author_filter);
        mSpinnerOrder = (Spinner) findViewById(R.id.spinner_order);

        Button filterButton = (Button) findViewById(R.id.button_filter);
        filterButton.setOnClickListener(this);

        Backendless.Persistence.of(BackendlessUser.class).find(new AsyncCallback<List<BackendlessUser>>() {
            @Override
            public void handleResponse(List<BackendlessUser> users) {
                mAuthors = new ArrayList<>();
                mAuthors.add(0, "Все авторы");
                for (int i = 0; i < users.size(); i++) {
                    mAuthors.add(i + 1, users.get(i).getProperty("login"));
                }
                ArrayAdapter<Object> adapter = new ArrayAdapter<>(FilterActivity.this, android.R.layout.simple_spinner_item, mAuthors);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinnerAuthor.setAdapter(adapter);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "server reported an error - " + fault.getMessage());
            }
        });
    }

    @Override
    public void onClick(View view) {

        String collectionSelected = mSpinnerCollection.getSelectedItem().toString();
        String authorSelected = mSpinnerAuthor.getSelectedItem().toString();
        String orderSelected = mSpinnerOrder.getSelectedItem().toString();
        Intent intent = new Intent(FilterActivity.this, ProfileActivity.class);
        intent.putExtra("collection", collectionSelected);
        intent.putExtra("author", authorSelected);
        intent.putExtra("order", orderSelected);
        startActivity(intent);
        finish();
    }
}
