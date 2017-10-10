
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
import android.widget.Toast;

import com.aleksus.handtohand.DefaultCallback;
import com.aleksus.handtohand.ExampleUser;
import com.aleksus.handtohand.R;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 1;

    private EditText mPasswordField;
    private EditText mLoginField;
    private EditText mEmailField;
    private EditText mPhoneField;
    private EditText mNameField;
    private EditText mFamilyField;
    private ImageView mIconGallery;

    private Bitmap mSelImage;

    private String mPassword;
    private String mLogin;
    private String mEmail;
    private String mPhone;
    private String mName;
    private String mFamily;
    private Bitmap mAvatar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.super.onBackPressed();
            }
        });

        initUI();
    }

    private void initUI() {
        mPasswordField = (EditText) findViewById(R.id.edit_password);
        mLoginField = (EditText) findViewById(R.id.edit_login);
        mEmailField = (EditText) findViewById(R.id.edit_email);
        mPhoneField = (EditText) findViewById(R.id.edit_phone);
        mNameField = (EditText) findViewById(R.id.edit_name);
        mFamilyField = (EditText) findViewById(R.id.edit_family);
        mIconGallery = (ImageView) findViewById(R.id.imageview_avatar);
        mIconGallery.buildDrawingCache();
        mSelImage = mIconGallery.getDrawingCache();
        Button registerButton = (Button) findViewById(R.id.button_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRegisterButtonClicked();
            }
        });
        Button iconSelectButton = (Button) findViewById(R.id.button_avatar_select);
        iconSelectButton.setOnClickListener(new View.OnClickListener() {
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
        mSelImage = null;
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        mSelImage = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mIconGallery.setImageBitmap(mSelImage);
                }
        }
    }

    private void onRegisterButtonClicked() {
        String passwordText = mPasswordField.getText().toString().trim();
        String loginText = mLoginField.getText().toString().trim();
        String emailText = mEmailField.getText().toString().trim();
        String phoneText = mPhoneField.getText().toString().trim();
        String nameText = mNameField.getText().toString().trim();
        String familyText = mFamilyField.getText().toString().trim();

        if (loginText.isEmpty()) {
            showToast("Поле 'Логин' не может быть пустым.");
            return;
        }

        if (emailText.isEmpty()) {
            showToast("Поле 'EMail' не может быть пустым.");
            return;
        }

        if (passwordText.isEmpty()) {
            showToast("Поле 'Пароль' не может быть пустым.");
            return;
        }

        if (phoneText.isEmpty()) {
            showToast("Поле 'Телефон' не может быть пустым.");
            return;
        }

        if (nameText.isEmpty()) {
            showToast("Поле 'Имя' не может быть пустым.");
            return;
        }

        if (familyText.isEmpty()) {
            showToast("Поле 'Фамилия' не может быть пустым.");
            return;
        }

        if (mSelImage == null) {
            showToast("Загрузите ваш аватар");
            return;
        }

        if (!passwordText.isEmpty() && !loginText.isEmpty() && !emailText.isEmpty() && !phoneText.isEmpty() && !nameText.isEmpty() && !familyText.isEmpty() && mSelImage != null) {
            mPassword = passwordText;
            mLogin = loginText;
            mEmail = emailText;
            mPhone = phoneText;
            mName = nameText;
            mFamily = familyText;
            mAvatar = mSelImage;
        }

        Backendless.Files.Android.upload(mSelImage, Bitmap.CompressFormat.PNG, 10, mLogin + "_user.png", "icons", new AsyncCallback<BackendlessFile>() {
            @Override
            public void handleResponse(final BackendlessFile backendlessFile) {
                ExampleUser user = new ExampleUser();

                if (mPassword != null && mLogin != null && mEmail != null && mPhone != null && mName != null && mFamily != null && mAvatar != null) {
                    user.setPassword(mPassword);
                    user.setLogin(mLogin);
                    user.setEmail(mEmail);
                    user.setPhone(mPhone);
                    user.setFirstName(mName);
                    user.setSecondName(mFamily);
                    user.setProperty("mAvatar", backendlessFile.getFileURL());
                }

                Backendless.UserService.register(user, new DefaultCallback<BackendlessUser>(RegisterActivity.this) {
                    @Override
                    public void handleResponse(BackendlessUser response) {
                        super.handleResponse(response);
                        startActivity(new Intent(RegisterActivity.this, RegistrationSuccessActivity.class));
                        finish();
                    }
                });
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e("MYAPP", "server reported an error - " + fault.getMessage());
            }
        });

    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
                