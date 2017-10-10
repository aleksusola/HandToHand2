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

    private static final String TAG = "MYAPP";

    private ProgressDialog mProgressDialog;
    private EditText mNameSelect;
    private EditText mPriceSelect;
    private EditText mDescSelect;
    private ImageView mPhotoGallery;
    private ImageView mPhotoGallery2;
    private ImageView mPhotoGallery3;
    private ImageView mSelectedView;
    private Spinner mSpinner;

    private Bitmap mSelImage;
    private Bitmap mSelImage2;
    private Bitmap mSelImage3;

    private String mNameSelected;
    private String mCollectionSelected;
    private String mDescSelected;
    private String mRelationColumnName;

    private int mPriceSelected;

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
        mProgressDialog = new ProgressDialog(this);
        mSpinner = (Spinner) findViewById(R.id.spinner_collection);
        mNameSelect = (EditText) findViewById(R.id.edit_name);
        mPriceSelect = (EditText) findViewById(R.id.edit_price);
        mDescSelect = (EditText) findViewById(R.id.edit_desc);
        Button newAdButton = (Button) findViewById(R.id.button_create);
        newAdButton.setOnClickListener(this);
        mPhotoGallery = (ImageView) findViewById(R.id.imageview_first);
        mPhotoGallery2 = (ImageView) findViewById(R.id.imageview_second);
        mPhotoGallery3 = (ImageView) findViewById(R.id.imageview_third);
        mSelImage = ((BitmapDrawable) mPhotoGallery.getDrawable()).getBitmap();
        mSelImage2 = ((BitmapDrawable) mPhotoGallery.getDrawable()).getBitmap();
        mSelImage3 = ((BitmapDrawable) mPhotoGallery.getDrawable()).getBitmap();

        mPhotoGallery.setOnLongClickListener(IViewLongClickListener);
        mPhotoGallery2.setOnLongClickListener(IViewLongClickListener);
        mPhotoGallery3.setOnLongClickListener(IViewLongClickListener);
        Button photoSelectButton = (Button) findViewById(R.id.button_select_photo);
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
        mSelectedView = (ImageView) findViewById(ivId);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.item_delete:
                        mSelectedView.setImageResource(R.drawable.hand_to_hand);
                        Toast.makeText(getApplicationContext(), "Изображение изменено на стандартную", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.item_change:
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        if (mSelectedView.equals(mPhotoGallery)) {
                            startActivityForResult(photoPickerIntent, 1);
                        } else if (mSelectedView.equals(mPhotoGallery2)) {
                            startActivityForResult(photoPickerIntent, 2);
                        } else {
                            startActivityForResult(photoPickerIntent, 3);
                        }
                        return true;
                    case R.id.item_default:
                        if (mSelectedView.equals(mPhotoGallery)) {
                            Toast.makeText(getApplicationContext(), "Это фото уже основное", Toast.LENGTH_SHORT).show();
                        } else {
                            Drawable drawable = mSelectedView.getDrawable();
                            Drawable drawable2 = mPhotoGallery.getDrawable();
                            mPhotoGallery.setImageDrawable(drawable);
                            mSelectedView.setImageDrawable(drawable2);
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
                        mSelImage = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mPhotoGallery.setImageBitmap(mSelImage);
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        mSelImage2 = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mPhotoGallery2.setImageBitmap(mSelImage2);
                }
                break;
            case 3:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        mSelImage3 = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mPhotoGallery3.setImageBitmap(mSelImage3);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {

        mNameSelected = mNameSelect.getText().toString();
        mCollectionSelected = mSpinner.getSelectedItem().toString();
        mDescSelected = mDescSelect.getText().toString();
        if (mDescSelected.equals("")) mDescSelected = "description";
        if (mPriceSelect.getText().toString().equals("")) mPriceSelected = 0;
        else mPriceSelected = Integer.parseInt(mPriceSelect.getText().toString());

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause("name= '" + mNameSelected + "' and ownerId = '" + Backendless.UserService.CurrentUser().getObjectId() + "'");
        final BackendlessUser user = Backendless.UserService.CurrentUser();
        Backendless.Data.of("ads_users").getObjectCount(queryBuilder, new AsyncCallback<Integer>() {
            @Override
            public void handleResponse(Integer count) {
                if (mNameSelected.equals(""))
                    Toast.makeText(NewAdActivity.this, "Не заполнены все данные", Toast.LENGTH_SHORT).show();
                else if (count != 0)
                    Toast.makeText(NewAdActivity.this, "У вас уже есть объявление с таким названием, измените его", Toast.LENGTH_SHORT).show();
                else {
                    mProgressDialog.setMessage("Подождите, данные сохраняются");
                    mProgressDialog.show();
                    Backendless.Files.Android.upload(mSelImage, Bitmap.CompressFormat.PNG, 100, "1.png", "icons/" + user.getProperty("login") + "/" + mNameSelected, new AsyncCallback<BackendlessFile>() {
                        @Override
                        public void handleResponse(final BackendlessFile firstFile) {
                            Backendless.Files.Android.upload(mSelImage2, Bitmap.CompressFormat.PNG, 100, "2.png", "icons/" + user.getProperty("login") + "/" + mNameSelected, new AsyncCallback<BackendlessFile>() {
                                @Override
                                public void handleResponse(final BackendlessFile secondFile) {
                                    Backendless.Files.Android.upload(mSelImage3, Bitmap.CompressFormat.PNG, 100, "3.png", "icons/" + user.getProperty("login") + "/" + mNameSelected, new AsyncCallback<BackendlessFile>() {
                                        @Override
                                        public void handleResponse(BackendlessFile thirdFile) {
                                            HashMap<String, java.io.Serializable> newAd = new HashMap<>();
                                            newAd.put("___class", "ads_users");
                                            newAd.put("name", mNameSelected);
                                            newAd.put("price", mPriceSelected);
                                            newAd.put("description", mDescSelected);
                                            newAd.put("ads_icon", firstFile.getFileURL());
                                            newAd.put("ads_icon2", secondFile.getFileURL());
                                            newAd.put("ads_icon3", thirdFile.getFileURL());
                                            Backendless.Persistence.of("ads_users").save(newAd, new AsyncCallback<Map>() {
                                                @Override
                                                public void handleResponse(final Map savedAd) {
                                                    HashMap<String, Object> parentObject = new HashMap<>();
                                                    parentObject.put("objectId", savedAd.get("objectId"));
                                                    mRelationColumnName = "collection:collection:1";
                                                    String whereClause = "type = '" + mCollectionSelected + "'";
                                                    Backendless.Data.of("ads_users").setRelation(parentObject, mRelationColumnName, whereClause, new AsyncCallback<Integer>() {

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
