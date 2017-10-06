package com.aleksus.handtohand.presentation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class NewAdActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog progressDialog;
    private EditText nameSelect;
    private EditText priceSelect;
    private EditText descSelect;
    private ImageView photoGallery;
    private ImageView photoGallery2;
    private ImageView photoGallery3;
    private ImageView selIV;
    private Spinner spinner;

    private Bitmap selImage;
    private Bitmap selImage2;
    private Bitmap selImage3;

    private String nameSelected;
    private String collectionSelected;
    private String descSelected;
    private String relationColumnName;

    private int priceSelected;
    private static final String TAG = "MYAPP";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ad);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewAdActivity.super.onBackPressed();
            }
        });
        progressDialog = new ProgressDialog(this);
        spinner = (Spinner) findViewById(R.id.collection_select);
        nameSelect = (EditText) findViewById(R.id.name_select);
        priceSelect = (EditText) findViewById(R.id.price_select);
        descSelect = (EditText) findViewById(R.id.desc_select);
        Button newAdButton = (Button) findViewById(R.id.new_add_button);
        newAdButton.setOnClickListener(this);
        photoGallery = (ImageView) findViewById(R.id.photoSelect);
        photoGallery2 = (ImageView) findViewById(R.id.photoSelect2);
        photoGallery3 = (ImageView) findViewById(R.id.photoSelect3);
        selImage = ((BitmapDrawable) photoGallery.getDrawable()).getBitmap();
        selImage2 = ((BitmapDrawable) photoGallery.getDrawable()).getBitmap();
        selImage3 = ((BitmapDrawable) photoGallery.getDrawable()).getBitmap();

        photoGallery.setOnLongClickListener(IViewLongClickListener);
        photoGallery2.setOnLongClickListener(IViewLongClickListener);
        photoGallery3.setOnLongClickListener(IViewLongClickListener);
        Button photoSelectButton = (Button) findViewById(R.id.photo_select_button);
        photoSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onISelectClicked();
            }
        });
    }

    private void onISelectClicked() {

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        for (int i = 3; i > 0; i--)
            startActivityForResult(photoPickerIntent, i);
    }

    View.OnLongClickListener IViewLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            showPopupMenu(v);
            return true;
        }

    };

    private void showPopupMenu(final View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.menu_popup);
        int ivId = v.getId();
        selIV = (ImageView) findViewById(ivId);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.iDel:
                        selIV.setImageResource(R.drawable.hand_to_hand);
                        Toast.makeText(getApplicationContext(), "Изображение изменено на стандартную", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.iChan:
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        if (selIV.equals(photoGallery))
                            startActivityForResult(photoPickerIntent, 1);
                        else if (selIV.equals(photoGallery2))
                            startActivityForResult(photoPickerIntent, 2);
                        else startActivityForResult(photoPickerIntent, 3);
                        return true;
                    case R.id.iDef:
                        if (selIV.equals(photoGallery))
                            Toast.makeText(getApplicationContext(), "Это фото уже основное", Toast.LENGTH_SHORT).show();
                        else {
                            Drawable drawable = selIV.getDrawable();
                            Drawable drawable2 = photoGallery.getDrawable();
                            photoGallery.setImageDrawable(drawable);
                            selIV.setImageDrawable(drawable2);
                            Toast.makeText(getApplicationContext(), "Теперь это фото основное", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
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
                    photoGallery.setImageBitmap(selImage);
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
                    photoGallery2.setImageBitmap(selImage2);
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
                    photoGallery3.setImageBitmap(selImage3);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {

        nameSelected = nameSelect.getText().toString();
        collectionSelected = spinner.getSelectedItem().toString();
        descSelected = descSelect.getText().toString();
        if (descSelected.equals("")) descSelected = "description";
        if (priceSelect.getText().toString().equals("")) priceSelected = 0;
        else priceSelected = Integer.parseInt(priceSelect.getText().toString());

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause("name= '" + nameSelected + "' and ownerId = '" + Backendless.UserService.CurrentUser().getObjectId() + "'");
        final BackendlessUser user = Backendless.UserService.CurrentUser();
        Backendless.Data.of("ads_users").getObjectCount(queryBuilder, new AsyncCallback<Integer>() {
            @Override
            public void handleResponse(Integer count) {
                if (nameSelected.equals(""))
                    Toast.makeText(NewAdActivity.this, "Не заполнены все данные", Toast.LENGTH_SHORT).show();
                else if (count != 0)
                    Toast.makeText(NewAdActivity.this, "У вас уже есть объявление с таким названием, измените его", Toast.LENGTH_SHORT).show();
                else {
                    progressDialog.setMessage("Подождите, данные сохраняются");
                    progressDialog.show();
                    Backendless.Files.Android.upload(selImage, Bitmap.CompressFormat.PNG, 100, "1.png", "icons/" + user.getProperty("login") + "/" + nameSelected, new AsyncCallback<BackendlessFile>() {
                        @Override
                        public void handleResponse(final BackendlessFile firstFile) {
                            Backendless.Files.Android.upload(selImage2, Bitmap.CompressFormat.PNG, 100, "2.png", "icons/" + user.getProperty("login") + "/" + nameSelected, new AsyncCallback<BackendlessFile>() {
                                @Override
                                public void handleResponse(final BackendlessFile secondFile) {
                                    Backendless.Files.Android.upload(selImage3, Bitmap.CompressFormat.PNG, 100, "3.png", "icons/" + user.getProperty("login") + "/" + nameSelected, new AsyncCallback<BackendlessFile>() {
                                        @Override
                                        public void handleResponse(BackendlessFile thirdFile) {
                                            HashMap<String, java.io.Serializable> newAd = new HashMap<>();
                                            newAd.put("___class", "ads_users");
                                            newAd.put("name", nameSelected);
                                            newAd.put("price", priceSelected);
                                            newAd.put("description", descSelected);
                                            newAd.put("ads_icon", firstFile.getFileURL());
                                            newAd.put("ads_icon2", secondFile.getFileURL());
                                            newAd.put("ads_icon3", thirdFile.getFileURL());
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
