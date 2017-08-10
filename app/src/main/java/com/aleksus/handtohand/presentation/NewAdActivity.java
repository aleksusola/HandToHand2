package com.aleksus.handtohand.presentation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;



public class NewAdActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText nameSelect;
    private EditText priceSelect;
    private EditText descSelect;
    private ImageView photoGallery;
    private Spinner spinner;

    private Bitmap selImage;

    private String nameSelected;
    private int priceSelected;
    private String collectionSelected;
    private String descSelected;
    private String relationColumnName;

    private static final String TAG = "MYAPP";
    static final int GALLERY_REQUEST = 1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ad);
        spinner = (Spinner) findViewById(R.id.collection_select);

        nameSelect = (EditText) findViewById(R.id.name_select);
        priceSelect = (EditText) findViewById(R.id.price_select);
        descSelect = (EditText) findViewById(R.id.desc_select);
        Button newAdButton = (Button) findViewById(R.id.new_add_button);
        newAdButton.setOnClickListener(this);
        photoGallery = (ImageView) findViewById(R.id.photoSelect);
        selImage = ((BitmapDrawable) photoGallery.getDrawable()).getBitmap();
        Button photoSelectButton = (Button) findViewById(R.id.photo_select_button);
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

        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
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
        priceSelected = Integer.parseInt(priceSelect.getText().toString());
        descSelected = descSelect.getText().toString();
        collectionSelected = spinner.getSelectedItem().toString();

        if (descSelected.equals("")){
            descSelected = "description";
        }
        Backendless.Files.Android.upload(selImage, Bitmap.CompressFormat.PNG, 10, nameSelected + ".png", "icons", new AsyncCallback<BackendlessFile>() {
            @Override
            public void handleResponse(final BackendlessFile backendlessFile) {
                HashMap<String, java.io.Serializable> newAd = new HashMap<>();
                newAd.put("___class", "ads_users");
                newAd.put("name", nameSelected);
                newAd.put("price", priceSelected);
                newAd.put("description", descSelected);
                newAd.put("ads_icon", backendlessFile.getFileURL());
                Backendless.Persistence.of("ads_users").save(newAd, new AsyncCallback<Map>() {
                    @Override
                    public void handleResponse(final Map savedAd) {
                        HashMap<String, Object> parentObject = new HashMap<>();
                        parentObject.put("objectId", savedAd.get("objectId"));
                        relationColumnName = "collection:collection:1";
                        String whereClause = "type = '" + collectionSelected + "'";
                        Backendless.Data.of("ads_users").setRelation(parentObject, relationColumnName, whereClause, new AsyncCallback<Integer>() {

                            @Override
                            public void handleResponse(Integer colNum) {
                                Log.i(TAG, "related objects have been added" + colNum);
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Log.e(TAG, "server reported an error - " + fault.getMessage());
                            }
                        });
                        Toast.makeText(NewAdActivity.this, "Добавлено! Обновите главную страницу", Toast.LENGTH_SHORT).show();
                        NewAdActivity.super.onBackPressed();
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.e(TAG, "server reported an error - " + fault.getMessage());
                    }
                });
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "server reported an error - " + fault.getMessage());
            }
        });


    }
}
