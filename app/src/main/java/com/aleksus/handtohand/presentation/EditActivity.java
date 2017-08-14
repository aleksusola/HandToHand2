package com.aleksus.handtohand.presentation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.backendless.persistence.DataQueryBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EditActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText nameEdit;
    private EditText priceEdit;
    private EditText descEdit;
    private ImageView photoAds;
    private Spinner spinner;

    private Bitmap selImage;

    private String nameSelected;
    private int priceSelected;
    private String descSelected;
    private String collectionSelected;
    private String relationColumnName;
    private String adTitle;

    private static final String TAG = "MYAPP";
    static final int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditActivity.super.onBackPressed();
            }
        });

        spinner = (Spinner) findViewById(R.id.edit_collection);
        nameEdit = (EditText) findViewById(R.id.edit_name);
        priceEdit = (EditText) findViewById(R.id.edit_price);
        descEdit = (EditText) findViewById(R.id.edit_desc);
        photoAds = (ImageView) findViewById(R.id.photoSelect);
        Button photoChangeButton = (Button) findViewById(R.id.photo_select_button);
        Button saveButton = (Button) findViewById(R.id.editSaveButton);
        saveButton.setOnClickListener(this);

        photoChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChangeButtonClicked();
            }
        });
        adTitle = getIntent().getStringExtra("title");
        String adPrice = getIntent().getStringExtra("price");
        String adDesc = getIntent().getStringExtra("desc");
        Bundle extras = getIntent().getExtras();
        selImage = extras.getParcelable("imagebitmap");
        photoAds.setImageBitmap(selImage);
        nameEdit.setText(adTitle);
        priceEdit.setText(adPrice);
        descEdit.setText(adDesc);

    }

    private void onChangeButtonClicked() {

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
                    photoAds.setImageBitmap(selImage);
                }
        }
    }


    @Override
    public void onClick(View v) {

        nameSelected = nameEdit.getText().toString();
        priceSelected = Integer.parseInt(priceEdit.getText().toString());
        descSelected = descEdit.getText().toString();
        collectionSelected = spinner.getSelectedItem().toString();

        Backendless.Files.remove("icons/" + adTitle + ".png", new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "server reported an error - " + fault.getMessage());
            }
        });

        Backendless.Files.Android.upload(selImage, Bitmap.CompressFormat.PNG, 10, nameSelected + ".png", "icons", new AsyncCallback<BackendlessFile>() {
            @Override
            public void handleResponse(final BackendlessFile backendlessFile) {

                String whereClause = "name = '" + adTitle + "'";
                DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                queryBuilder.setWhereClause(whereClause);
                Backendless.Data.of("ads_users").find(queryBuilder, new AsyncCallback<List<Map>>() {
                    @Override
                    public void handleResponse(List<Map> editAd) {
                        editAd.get(0).put("___class", "ads_users");
                        editAd.get(0).put("name", nameSelected);
                        editAd.get(0).put("price", priceSelected);
                        editAd.get(0).put("description", descSelected);
                        editAd.get(0).put("ads_icon", backendlessFile.getFileURL());
                        Backendless.Persistence.of("ads_users").save(editAd.get(0), new AsyncCallback<Map>() {
                            public void handleResponse(Map savedAd) {
                                HashMap<String, Object> parentObject = new HashMap<>();
                                parentObject.put("objectId", savedAd.get("objectId"));
                                relationColumnName = "collection:collection:1";
                                String whereClause1 = "type = '" + collectionSelected + "'";
                                Backendless.Data.of("ads_users").setRelation(parentObject, relationColumnName, whereClause1, new AsyncCallback<Integer>() {
                                    @Override
                                    public void handleResponse(Integer colNum) {
                                        Toast.makeText(EditActivity.this, "Изменено! Обновите данные на странице", Toast.LENGTH_SHORT).show();
                                        EditActivity.super.onBackPressed();
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault fault) {
                                        Log.e(TAG, "server reported an error - " + fault.getMessage());
                                    }
                                });
                            }

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

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "server reported an error - " + fault.getMessage());
            }
        });
    }
}
