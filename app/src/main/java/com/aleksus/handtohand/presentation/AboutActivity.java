package com.aleksus.handtohand.presentation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.aleksus.handtohand.R;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_mail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        btn_mail= (Button) findViewById(R.id.btn_mail);
        btn_mail.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:89276807107"));
        startActivity(intent);
    }
}
