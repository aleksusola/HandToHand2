package com.aleksus.handtohand.presentation;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.aleksus.handtohand.R;
import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.util.List;
import java.util.Map;

public class FilterActivity extends AppCompatActivity implements View.OnClickListener{

    private Spinner spinnerCollection;
    private Spinner spinnerAuthor;

    private Button filterButton;

    private String collectionSelected;
    private String authorSelected;

    private static final String TAG = "MYAPP";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        spinnerCollection = (Spinner) findViewById(R.id.collection_filter_select);
        spinnerAuthor = (Spinner) findViewById(R.id.author_filter_select);

        filterButton = (Button) findViewById(R.id.filter_button);
        filterButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        collectionSelected = spinnerCollection.getSelectedItem().toString().trim();
        authorSelected = spinnerAuthor.getSelectedItem().toString().trim();

        if (collectionSelected == "Все коллекции" ){
            Backendless.Data.of( "ads_users" ).find( new AsyncCallback<List<Map>>(){
                @Override
                public void handleResponse( List<Map> colFilter ) {
                }
                @Override
                public void handleFault( BackendlessFault fault ) {
                    Log.e( TAG, "server reported an error - " + fault.getMessage() );
                }
            });
        } else {
            String whereClause = "type = '"+ collectionSelected +"'";
            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
            queryBuilder.setWhereClause( whereClause );
            Backendless.Data.of( "ads_users" ).find( queryBuilder, new AsyncCallback<List<Map>>(){
                @Override
                public void handleResponse( List<Map> editAd ) {
                }
                @Override
                public void handleFault( BackendlessFault fault ) {
                    Log.e( TAG, "server reported an error - " + fault.getMessage() );
                }
            });
        }



    }
}
