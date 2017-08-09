package com.aleksus.handtohand.presentation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    private TextView newPassword;
    private ImageView iconChange;

    private Bitmap avatar;

    private String txtNewPassword;

    private static final String TAG = "MYAPP";
    static final int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        iconChange = (ImageView) findViewById(R.id.iconChange);
        newPassword = (TextView) findViewById(R.id.passwordFieldNew);
        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
        Button iconChangeButton = (Button) findViewById(R.id.icon_change_button);
        iconChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChangeButtonClicked();
            }
        });
        BackendlessUser user = Backendless.UserService.CurrentUser();
        if (user != null) {
            new DownloadImageTask(iconChange).execute(user.getProperty("avatar").toString());
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
        avatar = null;
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        avatar = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    iconChange.setImageBitmap(avatar);
                }
        }
    }


    private void onSaveButtonClicked() {

        txtNewPassword = newPassword.getText().toString();
        final BackendlessUser user = Backendless.UserService.CurrentUser();
        if (txtNewPassword.equals("") && avatar == null) {
            Toast.makeText(SettingsActivity.this, "Ничего не изменилось", Toast.LENGTH_LONG).show();
        } else if (txtNewPassword.equals("") && avatar != null){
            Backendless.Files.remove("icons/" + user.getProperty("login") + "_user.png", new AsyncCallback<Void>() {
                @Override
                public void handleResponse(Void response) {
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Log.e(TAG, "server reported an error - " + fault.getMessage());
                }
            });
            Backendless.Files.Android.upload(avatar, Bitmap.CompressFormat.PNG, 10, user.getProperty("login") + "_user.png", "icons", new AsyncCallback<BackendlessFile>() {
                @Override
                public void handleResponse(final BackendlessFile backendlessFile) {
                    user.setProperty("avatar", backendlessFile.getFileURL());
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
        } else if(!txtNewPassword.equals("") && avatar == null){
            user.setPassword(txtNewPassword);
            Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
                public void handleResponse(BackendlessUser user) {
                    Toast.makeText(SettingsActivity.this, "Пароль изменен, авторизуйтесь заново", Toast.LENGTH_LONG).show();
                    Backendless.UserService.logout(new DefaultCallback<Void>(SettingsActivity.this)
                    {
                        @Override
                        public void handleResponse (Void response){
                            super.handleResponse(response);
                            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                            finish();
                        }
                        @Override
                        public void handleFault (BackendlessFault fault){
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
        } else if(!txtNewPassword.equals("") && avatar != null){
            Backendless.Files.remove("icons/" + user.getProperty("login") + "_user.png", new AsyncCallback<Void>() {
                @Override
                public void handleResponse(Void response) {
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Log.e(TAG, "server reported an error - " + fault.getMessage());
                }
            });
            Backendless.Files.Android.upload(avatar, Bitmap.CompressFormat.PNG, 10, user.getProperty("login") + "_user.png", "icons", new AsyncCallback<BackendlessFile>() {
                @Override
                public void handleResponse(final BackendlessFile backendlessFile) {
                    user.setProperty("avatar", backendlessFile.getFileURL());
                    user.setPassword(txtNewPassword);
                    Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
                        public void handleResponse(BackendlessUser user) {
                            Toast.makeText(SettingsActivity.this, "Пароль и аватар изменены", Toast.LENGTH_LONG).show();
                            Backendless.UserService.logout(new DefaultCallback<Void>(SettingsActivity.this)
                            {
                                @Override
                                public void handleResponse (Void response){
                                    super.handleResponse(response);
                                    startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                                    finish();
                                }
                                @Override
                                public void handleFault (BackendlessFault fault){
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
