
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

    private EditText passwordField;
    private EditText loginField;
    private EditText emailField;
    private EditText phoneField;
    private EditText nameField;
    private EditText familyField;
    private ImageView iconGallery;

    private Bitmap selImage;

    private String password;
    private String login;
    private String email;
    private String phone;
    private String name;
    private String family;
    private Bitmap avatar;

    static final int GALLERY_REQUEST = 1;

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
        passwordField = (EditText) findViewById(R.id.passwordField);
        loginField = (EditText) findViewById(R.id.loginField);
        emailField = (EditText) findViewById(R.id.emailField);
        phoneField = (EditText) findViewById(R.id.phoneField);
        nameField = (EditText) findViewById(R.id.nameField);
        familyField = (EditText) findViewById(R.id.familyField);
        iconGallery = (ImageView) findViewById(R.id.iconSelect);
        iconGallery.buildDrawingCache();
        selImage = iconGallery.getDrawingCache();
        Button registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRegisterButtonClicked();
            }
        });
        Button iconSelectButton = (Button) findViewById(R.id.icon_select_button);
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
                    iconGallery.setImageBitmap(selImage);
                }
        }
    }

    private void onRegisterButtonClicked() {
        String passwordText = passwordField.getText().toString().trim();
        String loginText = loginField.getText().toString().trim();
        String emailText = emailField.getText().toString().trim();
        String phoneText = phoneField.getText().toString().trim();
        String nameText = nameField.getText().toString().trim();
        String familyText = familyField.getText().toString().trim();

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

        if (selImage == null) {
            showToast("Загрузите ваш аватар");
            return;
        }

        if (!passwordText.isEmpty() && !loginText.isEmpty() && !emailText.isEmpty() && !phoneText.isEmpty() && !nameText.isEmpty() && !familyText.isEmpty() && selImage != null) {
            password = passwordText;
            login = loginText;
            email = emailText;
            phone = phoneText;
            name = nameText;
            family = familyText;
            avatar = selImage;
        }

        Backendless.Files.Android.upload(selImage, Bitmap.CompressFormat.PNG, 10, login + "_user.png", "icons", new AsyncCallback<BackendlessFile>() {
            @Override
            public void handleResponse(final BackendlessFile backendlessFile) {
                ExampleUser user = new ExampleUser();

                if (password != null && login != null && email != null && phone != null && name != null && family != null && avatar != null) {
                    user.setPassword(password);
                    user.setLogin(login);
                    user.setEmail(email);
                    user.setPhone(phone);
                    user.setFirstName(name);
                    user.setSecondName(family);
                    user.setProperty("avatar", backendlessFile.getFileURL());
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
                