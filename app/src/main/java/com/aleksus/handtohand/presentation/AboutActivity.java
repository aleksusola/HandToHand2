package com.aleksus.handtohand.presentation;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aleksus.handtohand.R;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mDoMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutActivity.super.onBackPressed();
            }
        });
        mDoMail = (Button) findViewById(R.id.btn_mail);
        mDoMail.setOnClickListener(this);
        TextView appVersion = (TextView) findViewById(R.id.textView_version);
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            appVersion.setText(appVersion.getText() + " " + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        Intent mailIntent = new Intent(Intent.ACTION_SEND);
        mailIntent.setType("text/plain");
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Вопрос разработчику");
        mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "aleksusola@yandex.ru" });
        startActivity(Intent.createChooser(mailIntent, "Обратная связь"));
    }
}
