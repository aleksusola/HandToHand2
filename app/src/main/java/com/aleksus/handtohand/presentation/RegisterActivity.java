
package com.aleksus.handtohand.presentation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
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
    private EditText nameField;
    private EditText emailField;
    private EditText phoneField;
    private ImageView iconGallery;

    private Button registerButton;
    private Button iconSelectButton;
    private Bitmap selImage;

    private String password;
    private String name;
    private String email;
    private String phone;
    private Bitmap avatar;

    static final int GALLERY_REQUEST = 1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initUI();
    }

    private void initUI() {
        passwordField = (EditText) findViewById(R.id.passwordField);
        nameField = (EditText) findViewById(R.id.nameField);
        emailField = (EditText) findViewById(R.id.emailField);
        phoneField = (EditText) findViewById(R.id.phoneField);
        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRegisterButtonClicked();
            }
        });
        iconGallery = (ImageView) findViewById(R.id.iconSelect);
        selImage = null;
        iconSelectButton = (Button) findViewById(R.id.icon_select_button);
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
        switch(requestCode) {
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
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
        String nameText = nameField.getText().toString().trim();
        String emailText = emailField.getText().toString().trim();
        String phoneText = phoneField.getText().toString().trim();

        if (nameText.isEmpty()) {
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

        if (selImage == null) {
            showToast("Загрузите ваш аватар");
            return;
        }

        if (!passwordText.isEmpty()) {
            password = passwordText;
        }

        if (!nameText.isEmpty()) {
            name = nameText;
        }

        if (!emailText.isEmpty()) {
            email = emailText;
        }

        if (!phoneText.isEmpty()) {
            phone = phoneText;
        }

        if (selImage != null) {
            avatar = selImage;
        }

        Backendless.Files.Android.upload( selImage, Bitmap.CompressFormat.PNG, 10, name +"_user.png", "icons", new AsyncCallback<BackendlessFile>() {
            @Override
            public void handleResponse(final BackendlessFile backendlessFile) {
                ExampleUser user = new ExampleUser();

                if (password != null) {
                    user.setPassword(password);
                }

                if (name != null) {
                    user.setName(name);
                }

                if (email != null) {
                    user.setEmail(email);
                }

                if (phone != null) {
                    user.setProperty( "phone", phone );
                }

                if (avatar != null) {
                    user.setProperty( "avatar", backendlessFile.getFileURL() );
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
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(RegisterActivity.this, backendlessFault.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
                