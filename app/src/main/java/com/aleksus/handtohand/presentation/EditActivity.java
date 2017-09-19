package com.aleksus.handtohand.presentation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.backendless.persistence.DataQueryBuilder;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EditActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText nameEdit;
    private EditText priceEdit;
    private EditText descEdit;
    private ImageView photoAds;
    private ImageView photoAds2;
    private ImageView photoAds3;

    private Spinner spinner;

    private Bitmap myImage;
    private Bitmap selImage;
    private Bitmap selImage2;
    private Bitmap selImage3;

    private String nameSelected;
    private int priceSelected;
    private String descSelected;
    private String collectionSelected;
    private String relationColumnName;
    private String adTitle;

    private static final String TAG = "MYAPP";

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
        photoAds2 = (ImageView) findViewById(R.id.photoSelect2);
        photoAds3 = (ImageView) findViewById(R.id.photoSelect3);
        Button photoChangeButton = (Button) findViewById(R.id.photo_select_button);
        Button saveButton = (Button) findViewById(R.id.editSaveButton);
        saveButton.setOnClickListener(this);
        adTitle = getIntent().getStringExtra("title");
        Bundle extras = getIntent().getExtras();
        myImage = extras.getParcelable("imagebitmap");
        photoAds.setImageBitmap(myImage);
        BackendlessUser AdsOwner = Backendless.UserService.CurrentUser();
        String whereClause = "ownerId = '" + AdsOwner.getObjectId() + "' and name = '" + adTitle + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);
        Backendless.Data.of("ads_users").find(queryBuilder, new AsyncCallback<List<Map>>() {
            @Override
            public void handleResponse(List<Map> ad) {
                String adPrice = ad.get(0).get("price").toString();
                String adDesc = ad.get(0).get("description").toString();
                String adImage2 = ad.get(0).get("ads_icon2").toString();
                String adImage3 = ad.get(0).get("ads_icon3").toString();

                if (photoAds2.getDrawable() == null && photoAds3.getDrawable() == null) {
                    Glide
                            .with(EditActivity.this)
                            .load(adImage2)
                            .placeholder(R.mipmap.ic_record_voice_over_black)
                            .error(R.drawable.ic_error)
                            .override(150, 150)
                            .crossFade(100)
                            .into(photoAds2);
                    Glide
                            .with(EditActivity.this)
                            .load(adImage3)
                            .placeholder(R.mipmap.ic_record_voice_over_black)
                            .error(R.drawable.ic_error)
                            .override(150, 150)
                            .crossFade(100)
                            .into(photoAds3);
                } else {
                    Toast.makeText(EditActivity.this, "бла бла", Toast.LENGTH_SHORT).show();

                }
                nameEdit.setText(adTitle);
                priceEdit.setText(adPrice);
                descEdit.setText(adDesc);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
            }

        });
        photoChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChangeButtonClicked();
            }
        });
    }

    private void onChangeButtonClicked() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        for (int i = 3; i > 0; i--)
            startActivityForResult(photoPickerIntent, i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        selImage = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    photoAds.setImageBitmap(selImage);
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        selImage2 = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    photoAds2.setImageBitmap(selImage2);
                }
                break;
            case 3:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        selImage3 = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    photoAds3.setImageBitmap(selImage3);
                }
                break;
        }
    }


    @Override
    public void onClick(View v) {

        nameSelected = nameEdit.getText().toString();
        descSelected = descEdit.getText().toString();
        collectionSelected = spinner.getSelectedItem().toString();
        if (descSelected.equals("")) descSelected = "description";
        if (priceEdit.getText().toString().equals("")) priceSelected = 0;
        else priceSelected = Integer.parseInt(priceEdit.getText().toString());

        final BackendlessUser user = Backendless.UserService.CurrentUser();
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause("name != '" + adTitle + "' and name= '" + nameSelected + "' and ownerId = '" + Backendless.UserService.CurrentUser().getObjectId() + "'");
        Backendless.Data.of("ads_users").getObjectCount(queryBuilder, new AsyncCallback<Integer>() {
            @Override
            public void handleResponse(Integer count) {
                if (nameSelected.equals(""))
                    Toast.makeText(EditActivity.this, "Не заполнены все данные", Toast.LENGTH_SHORT).show();
                else if (count != 0)
                    Toast.makeText(EditActivity.this, "У вас уже есть другое объявление с таким названием, измените его", Toast.LENGTH_SHORT).show();
                else {
                    Backendless.Files.removeDirectory("icons/" + user.getProperty("login") + "/" + adTitle, new AsyncCallback<Void>() {
                        @Override
                        public void handleResponse(Void response) {
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Log.e(TAG, "server reported an error - " + fault.getMessage());
                        }
                    });

                    Backendless.Files.Android.upload(selImage, Bitmap.CompressFormat.PNG, 100, "1.png", "icons/" + user.getProperty("login") + "/" + nameSelected, new AsyncCallback<BackendlessFile>() {
                        @Override
                        public void handleResponse(final BackendlessFile firstFile) {
                            Backendless.Files.Android.upload(selImage2, Bitmap.CompressFormat.PNG, 100, "2.png", "icons/" + user.getProperty("login") + "/" + nameSelected, new AsyncCallback<BackendlessFile>() {
                                @Override
                                public void handleResponse(final BackendlessFile secondFile) {
                                    Backendless.Files.Android.upload(selImage3, Bitmap.CompressFormat.PNG, 100, "3.png", "icons/" + user.getProperty("login") + "/" + nameSelected, new AsyncCallback<BackendlessFile>() {
                                        @Override
                                        public void handleResponse(final BackendlessFile thirdFile) {
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
                                                    editAd.get(0).put("ads_icon", firstFile.getFileURL());
                                                    editAd.get(0).put("ads_icon2", secondFile.getFileURL());
                                                    editAd.get(0).put("ads_icon3", thirdFile.getFileURL());
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

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "server reported an error - " + fault.getMessage());
            }
        });


    }
}
