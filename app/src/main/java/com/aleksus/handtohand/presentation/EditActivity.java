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


public class EditActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText nameEdit;
    private EditText priceEdit;
    private Spinner spinner;
    private HashMap<String,Object> parentObject;
    private HashMap<String,Object> userParentObject;

    private Button saveButton;

    private String nameSelected;
    private String priceSelected;
    private String collectionSelected;
    private String relationColumnName;
    private String userRelationColumnName;
    private String adTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        spinner = (Spinner)findViewById(R.id.edit_collection);

        nameEdit = (EditText) findViewById(R.id.edit_name);
        priceEdit = (EditText) findViewById(R.id.edit_price);
        saveButton = (Button) findViewById(R.id.editSaveButton);
        saveButton.setOnClickListener(this);

        adTitle = getIntent().getStringExtra("title");

        nameEdit.setText(adTitle);

    }

    @Override
    public void onClick(View v) {

        nameSelected = nameEdit.getText().toString().trim();
        priceSelected = priceEdit.getText().toString().trim();
        collectionSelected = spinner.getSelectedItem().toString().trim();

        String whereClause = "name = '"+ adTitle +"'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause( whereClause );
        List<Map> editAd = Backendless.Data.of( "ads_users" ).find( queryBuilder);

//        editAd.get(0).put("___class", "ads_users");
        editAd.get(0).put("name", nameSelected);
        editAd.get(0).put("price", priceSelected);
        Map savedAd = Backendless.Persistence.of( "ads_users" ).save( editAd.get(0));
        parentObject.put("objectId", savedAd.get("objectId").toString());
        relationColumnName= "collection:collection:1";
        String whereClause1 = "type = '"+ collectionSelected +"'";
        Backendless.Data.of( "ads_users" ).addRelation(parentObject,relationColumnName, whereClause1, new AsyncCallback<Integer>() {
            @Override
            public void handleResponse( Integer colNum ) {
                Log.i( "MYAPP", "related objects have been added" + colNum );
            }
            @Override
            public void handleFault( BackendlessFault fault ) {
                Log.e( "MYAPP", "server reported an error - " + fault.getMessage() );
            }
        } );
        BackendlessUser user = Backendless.UserService.CurrentUser();
        userParentObject.put("objectId", user.getProperty("objectId").toString());
        userRelationColumnName= "MyAds:ads_users:n";
        String whereClauseUser = "name = '"+savedAd.get("name").toString() +"'";
        Backendless.Data.of( "Users" ).addRelation(userParentObject,userRelationColumnName, whereClauseUser, new AsyncCallback<Integer>() {
            @Override
            public void handleResponse( Integer adsNum ) {
                Log.i( "MYAPP", "related objects have been added" + adsNum);
            }
            @Override
            public void handleFault( BackendlessFault fault ) {
                Log.e( "MYAPP", "server reported an error - " + fault.getMessage() );
            }
        } );
        Toast.makeText(EditActivity.this, "Добавлено", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(EditActivity.this, ProfileActivity.class));

    }
}
