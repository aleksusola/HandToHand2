
package com.aleksus.handtohand.presentation;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aleksus.handtohand.R;

public class RegistrationSuccessActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_success);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegistrationSuccessActivity.super.onBackPressed();
            }
        });
        initUI();
    }

    private void initUI() {
        TextView messageView = (TextView) findViewById(R.id.textview_message);
        Button loginButton = (Button) findViewById(R.id.button_login);

        Resources resources = getResources();
        String message = String.format(resources.getString(R.string.registration_success_message), resources.getString(R.string.app_name));
        messageView.setText(message);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoginButtonClicked();
            }
        });
    }

    public void onLoginButtonClicked() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
