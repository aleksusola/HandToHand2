package com.aleksus.handtohand.presentation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EditActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MYAPP";

    private ProgressDialog mProgressDialog;
    private EditText mNameEdit;
    private EditText mPriceEdit;
    private EditText mDescEdit;
    private ImageView mPhotoAds;
    private ImageView mPhotoAds2;
    private ImageView mPhotoAds3;
    private ImageView mSelIV;

    private Spinner mSpinner;

    private Bitmap mSelImage;
    private Bitmap mSelImage2;
    private Bitmap mSelImage3;

    private int mPrice;

    private String mNameSelected;
    private String mDescSelected;
    private String mCollectionSelected;
    private String mRelationColumnName;
    private String mAdTitle;

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

        mProgressDialog = new ProgressDialog(this);
        mSpinner = (Spinner) findViewById(R.id.spinner_collection);
        mNameEdit = (EditText) findViewById(R.id.edit_name);
        mPriceEdit = (EditText) findViewById(R.id.edit_price);
        mDescEdit = (EditText) findViewById(R.id.edit_desc);
        mPhotoAds = (ImageView) findViewById(R.id.imageview_first);
        mPhotoAds2 = (ImageView) findViewById(R.id.imageview_second);
        mPhotoAds3 = (ImageView) findViewById(R.id.imageview_third);
        Button photoChangeButton = (Button) findViewById(R.id.button_select_photo);
        Button saveButton = (Button) findViewById(R.id.button_save);

        mPhotoAds.setOnLongClickListener(IViewLongClickListener);
        mPhotoAds2.setOnLongClickListener(IViewLongClickListener);
        mPhotoAds3.setOnLongClickListener(IViewLongClickListener);
        saveButton.setOnClickListener(this);
        photoChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChangeButtonClicked();
            }
        });
        mAdTitle = getIntent().getStringExtra("title");
        String adCol = getIntent().getStringExtra("collection");
        switch (adCol) {
            case "Аксессуары":
                mSpinner.setSelection(0);
                break;
            case "Бытовая техника":
                mSpinner.setSelection(1);
                break;
            case "Инструменты":
                mSpinner.setSelection(2);
                break;
            case "Мебель":
                mSpinner.setSelection(3);
                break;
            case "Недвижимость":
                mSpinner.setSelection(4);
                break;
            case "Одежда":
                mSpinner.setSelection(5);
                break;
            case "Телефоны":
                mSpinner.setSelection(6);
                break;
            case "Транспорт":
                mSpinner.setSelection(7);
                break;
            case "Услуги":
                mSpinner.setSelection(8);
                break;
            case "Электроника":
                mSpinner.setSelection(9);
                break;
            default:
                mSpinner.setSelection(10);
                break;
        }
        BackendlessUser AdsOwner = Backendless.UserService.CurrentUser();
        String whereClause = "ownerId = '" + AdsOwner.getObjectId() + "' and name = '" + mAdTitle + "'";
        DataQueryBuilder adBuilder = DataQueryBuilder.create();
        adBuilder.setWhereClause(whereClause);
        Backendless.Data.of("ads_users").find(adBuilder, new AsyncCallback<List<Map>>() {
            @Override
            public void handleResponse(List<Map> ad) {
                String adPrice = ad.get(0).get("price").toString();
                String adDesc = ad.get(0).get("description").toString();
                String adImage = ad.get(0).get("ads_icon").toString();
                String adImage2 = ad.get(0).get("ads_icon2").toString();
                String adImage3 = ad.get(0).get("ads_icon3").toString();
                Glide
                        .with(EditActivity.this)
                        .load(adImage)
                        .placeholder(R.mipmap.ic_record_voice_over_black)
                        .error(R.drawable.ic_error)
                        .override(500, 500)
                        .crossFade(100)
                        .into(mPhotoAds);
                Glide
                        .with(EditActivity.this)
                        .load(adImage2)
                        .placeholder(R.mipmap.ic_record_voice_over_black)
                        .error(R.drawable.ic_error)
                        .override(500, 500)
                        .crossFade(100)
                        .into(mPhotoAds2);
                Glide
                        .with(EditActivity.this)
                        .load(adImage3)
                        .placeholder(R.mipmap.ic_record_voice_over_black)
                        .error(R.drawable.ic_error)
                        .override(500, 500)
                        .crossFade(100)
                        .into(mPhotoAds3);

                mNameEdit.setText(mAdTitle);
                mPriceEdit.setText(adPrice);
                mDescEdit.setText(adDesc);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e(TAG, "server reported an error - " + fault.getMessage());
            }
        });
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
        mSelIV = (ImageView) findViewById(ivId);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.item_delete:
                        mSelIV.setImageResource(R.drawable.hand_to_hand);
                        Toast.makeText(getApplicationContext(), "Изображение изменено на стандартную", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.item_change:
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        if (mSelIV.equals(mPhotoAds)) startActivityForResult(photoPickerIntent, 1);
                        else if (mSelIV.equals(mPhotoAds2))
                            startActivityForResult(photoPickerIntent, 2);
                        else startActivityForResult(photoPickerIntent, 3);
                        return true;
                    case R.id.item_default:
                        if (mSelIV.equals(mPhotoAds))
                            Toast.makeText(getApplicationContext(), "Это фото уже основное", Toast.LENGTH_SHORT).show();
                        else {
                            Drawable drawable = mSelIV.getDrawable();
                            Drawable drawable2 = mPhotoAds.getDrawable();
                            mPhotoAds.setImageDrawable(drawable);
                            mSelIV.setImageDrawable(drawable2);
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
                        mSelImage = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mPhotoAds.setImageBitmap(mSelImage);
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
                    mPhotoAds2.setImageBitmap(mSelImage2);
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
                    mPhotoAds3.setImageBitmap(mSelImage3);
                }
                break;
        }
    }


    @Override
    public void onClick(View v) {

        mNameSelected = mNameEdit.getText().toString();
        mDescSelected = mDescEdit.getText().toString();
        mCollectionSelected = mSpinner.getSelectedItem().toString();
        if (mDescSelected.equals("")) mDescSelected = "description";
        if (mPriceEdit.getText().toString().equals("")) mPrice = 0;
        else mPrice = Integer.parseInt(mPriceEdit.getText().toString());

        mPhotoAds.buildDrawingCache();
        mSelImage = mPhotoAds.getDrawingCache();
        mPhotoAds.setDrawingCacheEnabled(false);
        mPhotoAds2.buildDrawingCache();
        mSelImage2 = mPhotoAds2.getDrawingCache();
        mPhotoAds2.setDrawingCacheEnabled(false);
        mPhotoAds3.buildDrawingCache();
        mSelImage3 = mPhotoAds3.getDrawingCache();
        mPhotoAds3.setDrawingCacheEnabled(false);

        final BackendlessUser user = Backendless.UserService.CurrentUser();
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause("name != '" + mAdTitle + "' and name= '" + mNameSelected + "' and ownerId = '" + Backendless.UserService.CurrentUser().getObjectId() + "'");
        Backendless.Data.of("ads_users").getObjectCount(queryBuilder, new AsyncCallback<Integer>() {
            @Override
            public void handleResponse(Integer count) {
                if (mNameSelected.equals(""))
                    Toast.makeText(EditActivity.this, "Не заполнены все данные", Toast.LENGTH_SHORT).show();
                else if (count != 0)
                    Toast.makeText(EditActivity.this, "У вас уже есть другое объявление с таким названием, измените его", Toast.LENGTH_SHORT).show();
                else {
                    mProgressDialog.setMessage("Подождите, данные сохраняются");
                    mProgressDialog.show();
                    Backendless.Files.removeDirectory("icons/" + user.getProperty("login") + "/" + mAdTitle, new AsyncCallback<Void>() {
                        @Override
                        public void handleResponse(Void response) {
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Log.e(TAG, "server reported an error - " + fault.getMessage());
                        }
                    });

                    Backendless.Files.Android.upload(mSelImage, Bitmap.CompressFormat.PNG, 100, "1.png", "icons/" + user.getProperty("login") + "/" + mNameSelected, new AsyncCallback<BackendlessFile>() {
                        @Override
                        public void handleResponse(final BackendlessFile firstFile) {
                            Backendless.Files.Android.upload(mSelImage2, Bitmap.CompressFormat.PNG, 100, "2.png", "icons/" + user.getProperty("login") + "/" + mNameSelected, new AsyncCallback<BackendlessFile>() {
                                @Override
                                public void handleResponse(final BackendlessFile secondFile) {
                                    Backendless.Files.Android.upload(mSelImage3, Bitmap.CompressFormat.PNG, 100, "3.png", "icons/" + user.getProperty("login") + "/" + mNameSelected, new AsyncCallback<BackendlessFile>() {
                                        @Override
                                        public void handleResponse(final BackendlessFile thirdFile) {
                                            String whereClause = "name = '" + mAdTitle + "'";
                                            DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                                            queryBuilder.setWhereClause(whereClause);
                                            Backendless.Data.of("ads_users").find(queryBuilder, new AsyncCallback<List<Map>>() {
                                                @Override
                                                public void handleResponse(List<Map> oldAd) {

                                                    Backendless.Persistence.of("ads_users").remove(oldAd.get(0), new AsyncCallback<Long>() {
                                                        @Override
                                                        public void handleResponse(Long response) {
                                                        }

                                                        @Override
                                                        public void handleFault(BackendlessFault fault) {
                                                            Log.e(TAG, "server reported an error - " + fault.getMessage());
                                                        }
                                                    });

                                                    HashMap<String, java.io.Serializable> newAd = new HashMap<>();
                                                    newAd.put("___class", "ads_users");
                                                    newAd.put("name", mNameSelected);
                                                    newAd.put("price", mPrice);
                                                    newAd.put("description", mDescSelected);
                                                    newAd.put("ads_icon", firstFile.getFileURL());
                                                    newAd.put("ads_icon2", secondFile.getFileURL());
                                                    newAd.put("ads_icon3", thirdFile.getFileURL());
                                                    Backendless.Persistence.of("ads_users").save(newAd, new AsyncCallback<Map>() {
                                                        public void handleResponse(Map savedAd) {
                                                            HashMap<String, Object> parentObject = new HashMap<>();
                                                            parentObject.put("objectId", savedAd.get("objectId"));
                                                            mRelationColumnName = "collection:collection:1";
                                                            String whereClause1 = "type = '" + mCollectionSelected + "'";
                                                            Backendless.Data.of("ads_users").setRelation(parentObject, mRelationColumnName, whereClause1, new AsyncCallback<Integer>() {
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
