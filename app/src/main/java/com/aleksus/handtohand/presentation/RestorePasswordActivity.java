
package com.aleksus.handtohand.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aleksus.handtohand.DefaultCallback;
import com.aleksus.handtohand.R;
import com.backendless.Backendless;

public class RestorePasswordActivity extends AppCompatActivity {
    private EditText loginField;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestorePasswordActivity.super.onBackPressed();
            }
        });
        initUI();
    }

    private void initUI() {
        Button restorePasswordButton = (Button) findViewById(R.id.restorePasswordButton);
        loginField = (EditText) findViewById(R.id.loginField);

        restorePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRestorePasswordButtonClicked();
            }
        });
    }

    public void onRestorePasswordButtonClicked() {
        String login = loginField.getText().toString();
        Backendless.UserService.restorePassword(login, new DefaultCallback<Void>(this) {
            @Override
            public void handleResponse(Void response) {
                super.handleResponse(response);
                startActivity(new Intent(RestorePasswordActivity.this, PasswordRecoveryActivity.class));
                finish();
            }
        });
    }
}