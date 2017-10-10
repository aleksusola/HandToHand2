package com.aleksus.handtohand.presentation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aleksus.handtohand.DefaultCallback;
import com.aleksus.handtohand.DownloadImageTask;
import com.aleksus.handtohand.R;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;

import java.io.IOException;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "MYAPP";
    private static final int GALLERY_REQUEST = 1;

    private TextView mNewPassword;
    private ImageView mIconChange;
    private Bitmap mAvatar;
    private String mTxtNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.super.onBackPressed();
            }
        });

        mIconChange = (ImageView) findViewById(R.id.imageview_avatar);
        mNewPassword = (EditText) findViewById(R.id.edit_password);
        Button saveButton = (Button) findViewById(R.id.button_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
        Button iconChangeButton = (Button) findViewById(R.id.button_change_avatar);
        iconChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChangeButtonClicked();
            }
        });
        BackendlessUser user = Backendless.UserService.CurrentUser();
        if (user != null) {
            new DownloadImageTask(mIconChange).execute(user.getProperty("avatar").toString());
        }
    }

    private void onChangeButtonClicked() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        mAvatar = null;
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        mAvatar = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mIconChange.setImageBitmap(mAvatar);
                }
        }
    }


    private void onSaveButtonClicked() {

        mTxtNewPassword = mNewPassword.getText().toString();
        final BackendlessUser user = Backendless.UserService.CurrentUser();
        if (mTxtNewPassword.equals("") && mAvatar == null) {
            Toast.makeText(SettingsActivity.this, "Ничего не изменилось", Toast.LENGTH_LONG).show();
        } else if (mTxtNewPassword.equals("") && mAvatar != null) {
            Backendless.Files.remove("icons/" + user.getProperty("login") + "_user.png", new AsyncCallback<Void>() {
                @Override
                public void handleResponse(Void response) {
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Log.e(TAG, "server reported an error - " + fault.getMessage());
                }
            });
            Backendless.Files.Android.upload(mAvatar, Bitmap.CompressFormat.PNG, 10, user.getProperty("login") + "_user.png", "icons", new AsyncCallback<BackendlessFile>() {
                @Override
                public void handleResponse(final BackendlessFile backendlessFile) {
                    user.setProperty("mAvatar", backendlessFile.getFileURL());
                    Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
                        public void handleResponse(BackendlessUser user) {
                            startActivity(new Intent(SettingsActivity.this, ProfileActivity.class));
                            finish();
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
        } else if (!mTxtNewPassword.equals("") && mAvatar == null) {
            user.setPassword(mTxtNewPassword);
            Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
                public void handleResponse(BackendlessUser user) {
                    Toast.makeText(SettingsActivity.this, "Пароль изменен, авторизуйтесь заново", Toast.LENGTH_LONG).show();
                    Backendless.UserService.logout(new DefaultCallback<Void>(SettingsActivity.this) {
                        @Override
                        public void handleResponse(Void response) {
                            super.handleResponse(response);
                            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                            finish();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            if (fault.getCode().equals("3023")) // Unable to logout: not logged in (session expired, etc.)
                                handleResponse(null);
                            else
                                super.handleFault(fault);
                        }
                    });
                }

                public void handleFault(BackendlessFault fault) {
                    Log.e(TAG, "server reported an error - " + fault.getMessage());
                }
            });
        } else if (!mTxtNewPassword.equals("") && mAvatar != null) {
            Backendless.Files.remove("icons/" + user.getProperty("login") + "_user.png", new AsyncCallback<Void>() {
                @Override
                public void handleResponse(Void response) {
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Log.e(TAG, "server reported an error - " + fault.getMessage());
                }
            });
            Backendless.Files.Android.upload(mAvatar, Bitmap.CompressFormat.PNG, 10, user.getProperty("login") + "_user.png", "icons", new AsyncCallback<BackendlessFile>() {
                @Override
                public void handleResponse(final BackendlessFile backendlessFile) {
                    user.setProperty("mAvatar", backendlessFile.getFileURL());
                    user.setPassword(mTxtNewPassword);
                    Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
                        public void handleResponse(BackendlessUser user) {
                            Toast.makeText(SettingsActivity.this, "Пароль и аватар изменены", Toast.LENGTH_LONG).show();
                            Backendless.UserService.logout(new DefaultCallback<Void>(SettingsActivity.this) {
                                @Override
                                public void handleResponse(Void response) {
                                    super.handleResponse(response);
                                    startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                                    finish();
                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {
                                    if (fault.getCode().equals("3023")) // Unable to logout: not logged in (session expired, etc.)
                                        handleResponse(null);
                                    else
                                        super.handleFault(fault);
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


    }
}
