package com.aleksus.handtohand.presentation;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.aleksus.handtohand.R;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.attr.data;


public class NewAdActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText nameSelect;
    private EditText priceSelect;
    private ImageView photoGallery;
    private Spinner spinner;
    private HashMap<String,Object> parentObject;
    private HashMap<String,Object> userParentObject;

    private Button newAdButton;
    private Button photoSelectButton;

    private String nameSelected;
    private String priceSelected;
    private String collectionSelected;
    private String relationColumnName;
    private String userRelationColumnName;
    private String selectedImagePath;

    private static final String TAG = "MYAPP";
    private static final int SELECT_PICTURE = 1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ad);
        spinner = (Spinner)findViewById(R.id.collection_select);
        selectedImagePath = "ic_record_voice_over_black.png";

        nameSelect = (EditText) findViewById(R.id.name_select);
        priceSelect = (EditText) findViewById(R.id.price_select);
        photoGallery = (ImageView) findViewById(R.id.photoSelect);
        newAdButton = (Button) findViewById(R.id.new_add_button);
        photoSelectButton = (Button) findViewById(R.id.photo_select_button);
        newAdButton.setOnClickListener(this);
        photoSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectButtonClicked();
            }
        });
    }

    private void onSelectButtonClicked() {

        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/*");
        startActivityForResult(i, SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri u = (Uri) data.getData();
            Toast.makeText(getApplicationContext(), ""+u, Toast.LENGTH_LONG).show();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(u, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            selectedImagePath = cursor.getString(columnIndex);
            Toast.makeText(getApplicationContext(), ""+selectedImagePath, Toast.LENGTH_LONG).show();
            cursor.close();
            photoGallery.setImageURI(u);
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public void onClick(View v) {

        nameSelected = nameSelect.getText().toString();
        priceSelected = priceSelect.getText().toString();
        collectionSelected = spinner.getSelectedItem().toString();

        HashMap newAd = new HashMap();
        newAd.put( "___class", "ads_users");
        newAd.put("name", nameSelected);
        newAd.put("price", priceSelected);
        newAd.put("ads_icon", selectedImagePath);

        Backendless.Persistence.of( "ads_users" ).save( newAd, new AsyncCallback<Map>() {
            @Override
            public void handleResponse(final Map savedAd ) {
                final String whereClause = "type = '" +collectionSelected+ "'";
                DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                queryBuilder.setWhereClause( whereClause );
//                List<Map>  myCollection = Backendless.Data.of( "collection" ).find( queryBuilder);
                Backendless.Data.of( "collection" ).find( queryBuilder, new AsyncCallback<List<Map>>(){
                    @Override
                    public void handleResponse( List<Map> myCollection ) {
                        parentObject.put("objectId", savedAd.get("objectId").toString());
                        relationColumnName= "collection:collection:1";
                        Backendless.Data.of( "ads_users" ).addRelation(parentObject,relationColumnName, whereClause, new AsyncCallback<Integer>() {

                            @Override public void handleResponse( Integer colNum ) {
                                Log.i( TAG, "related objects have been added" + colNum );
                            }

                            @Override public void handleFault( BackendlessFault fault ) {
                                Log.e( TAG, "server reported an error - " + fault.getMessage());
                            }
                        });
                    }
                    @Override
                    public void handleFault( BackendlessFault fault ) {
                        Log.e( TAG, "server reported an error - " + fault.getMessage());
                    }
                });

                BackendlessUser user = Backendless.UserService.CurrentUser();
                userParentObject.put("objectId", user.getProperty("objectId").toString());
                userRelationColumnName= "MyAds:ads_users:n";
                String whereClauseUser = "name = '"+savedAd.get("name").toString() +"'";
                Backendless.Data.of( "Users" ).addRelation(userParentObject,userRelationColumnName, whereClauseUser, new AsyncCallback<Integer>() {
                    @Override
                    public void handleResponse( Integer adsNum ) {
                        Log.i( TAG, "related objects have been added" + adsNum);
                    }
                    @Override
                    public void handleFault( BackendlessFault fault ) {
                        Log.e( TAG, "server reported an error - " + fault.getMessage() );
                    }
                } );

            }
            @Override
            public void handleFault( BackendlessFault fault ) {
                Log.e( TAG, "server reported an error - " + fault.getMessage() );
            }
        });
        Toast.makeText(NewAdActivity.this, "Добавлено", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(NewAdActivity.this, ProfileActivity.class));

    }
}
