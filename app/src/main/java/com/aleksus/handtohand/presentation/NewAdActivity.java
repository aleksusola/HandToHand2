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
import com.backendless.files.BackendlessFile;
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

    private Button newAdButton;
    private Button photoSelectButton;
    private Bitmap selImage;

    private String nameSelected;
    private String priceSelected;
    private String collectionSelected;
    private String relationColumnName;
    private String userRelationColumnName;

    private static final String TAG = "MYAPP";
    static final int GALLERY_REQUEST = 1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ad);
        spinner = (Spinner)findViewById(R.id.collection_select);

        nameSelect = (EditText) findViewById(R.id.name_select);
        priceSelect = (EditText) findViewById(R.id.price_select);
        photoGallery = (ImageView) findViewById(R.id.photoSelect);
        selImage = null;
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

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        selImage = null;

        switch(requestCode) {
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        selImage = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    photoGallery.setImageBitmap(selImage);
                }
        }
    }


    @Override
    public void onClick(View v) {

        nameSelected = nameSelect.getText().toString();
        priceSelected = priceSelect.getText().toString();
        collectionSelected = spinner.getSelectedItem().toString();

        Backendless.Files.Android.upload( selImage, Bitmap.CompressFormat.PNG, 10, nameSelected +".png", "icons", new AsyncCallback<BackendlessFile>() {
            @Override
            public void handleResponse(final BackendlessFile backendlessFile) {
                HashMap newAd = new HashMap();
                newAd.put( "___class", "ads_users");
                newAd.put("name", nameSelected);
                newAd.put("price", priceSelected);
                newAd.put("ads_icon", backendlessFile.getFileURL());
                Backendless.Persistence.of( "ads_users" ).save( newAd, new AsyncCallback<Map>() {
                    @Override
                    public void handleResponse(final Map savedAd ) {
                        String whereClause = "type = '" +collectionSelected+ "'";
                        HashMap<String, Object> parentObject = new HashMap<String, Object>();
                        parentObject.put("objectId", savedAd.get("objectId"));
                        relationColumnName= "collection:collection:1";
                        Backendless.Data.of( "ads_users" ).setRelation(parentObject,relationColumnName, whereClause, new AsyncCallback<Integer>() {

                            @Override public void handleResponse( Integer colNum ) {
                                Log.i( TAG, "related objects have been added" + colNum );
                            }

                            @Override public void handleFault( BackendlessFault fault ) {
                                Log.e( TAG, "server reported an error - " + fault.getMessage());
                            }
                        });
                        BackendlessUser user = Backendless.UserService.CurrentUser();
                        HashMap<String, Object> userParentObject = new HashMap<String, Object>();
                        userParentObject.put("objectId", user.getProperty("objectId"));
                        userRelationColumnName= "MyAds:ads_users:n";
                        String whereClauseUser = "name = '"+savedAd.get("name") +"'";
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
                        Toast.makeText(NewAdActivity.this, "Добавлено", Toast.LENGTH_SHORT).show();

                    }
                    @Override
                    public void handleFault( BackendlessFault fault ) {
                        Log.e( TAG, "server reported an error - " + fault.getMessage() );
                    }
                });
            }
            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(NewAdActivity.this, backendlessFault.toString(), Toast.LENGTH_SHORT).show();
            }
        });



    }
}
