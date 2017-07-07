package com.aleksus.handtohand.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.aleksus.handtohand.R;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NewAdActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText nameSelect;
    private EditText priceSelect;
    private Spinner spinner;
    private HashMap<String,Object> parentObject;

    private Button newAdButton;

    private String nameSelected;
    private String priceSelected;
    private String collectionSelected;
    private String relationColumnName;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ad);
        spinner = (Spinner)findViewById(R.id.collection_select);

        nameSelect = (EditText) findViewById(R.id.name_select);
        priceSelect = (EditText) findViewById(R.id.price_select);
        newAdButton = (Button) findViewById(R.id.new_add_button);
        newAdButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        nameSelected = nameSelect.getText().toString().trim();
        priceSelected = priceSelect.getText().toString().trim();
        collectionSelected = spinner.getSelectedItem().toString().trim();

        String whereClause = "type = '" +collectionSelected+ "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause( whereClause );
        Backendless.Data.of( "collection" ).find( queryBuilder, new AsyncCallback<List<Map>>(){
            @Override
            public void handleResponse(final List<Map> myCollection ) {
                HashMap newAd = new HashMap();
                BackendlessUser user = Backendless.UserService.CurrentUser();
                newAd.put( "___class", "ads_users");
                newAd.put("name", nameSelected);
                newAd.put("price", priceSelected);
                newAd.put("collection", myCollection.get(0));

                Backendless.Persistence.of( "ads_users" ).save( newAd, new AsyncCallback<Map>() {
                    public void handleResponse( Map savedAd )
                    {
//                        parentObject.put("objectId", savedAd.get("objectId").toString());
//                        relationColumnName= "collection:collection:1";
//                        String whereClause = "type = '"+myCollection.get(0).get("type") +"'";
//                        Backendless.Data.of( "ads_users" ).addRelation
//                                (parentObject,relationColumnName, whereClause, new AsyncCallback<Integer>() {
//                            @Override
//                            public void handleResponse( Integer response )
//                            {
//                                Log.i( "MYAPP", "related objects have been added" );
//                            }
//
//                            @Override
//                            public void handleFault( BackendlessFault fault )
//                            {
//                                Log.e( "MYAPP", "server reported an error - " + fault.getMessage() );
//                            }
//                        } );
                    }

                    public void handleFault( BackendlessFault fault )
                    {
                        // an error has occurred, the error code can be retrieved with fault.getCode()
                    }
                });
                Toast.makeText(NewAdActivity.this, "Добавлено", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(NewAdActivity.this, ProfileActivity.class));
            }
            @Override
            public void handleFault( BackendlessFault fault ) {
                Log.i( "MYAPP", "error - " + fault.getMessage() );
            }
        });

    }
}