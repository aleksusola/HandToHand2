package com.aleksus.handtohand.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.aleksus.handtohand.Defaults;
import com.aleksus.handtohand.R;
import com.backendless.Backendless;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

import java.util.concurrent.TimeUnit;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash_screen);


        Backendless.setUrl(Defaults.SERVER_URL);
        Backendless.initApp(getApplicationContext(), Defaults.APPLICATION_ID, Defaults.API_KEY);

        LinearLayout linearAnim = (LinearLayout) findViewById(R.id.linearAnim);
        Animation anim = AnimationUtils.loadAnimation(SplashScreenActivity.this, R.anim.combo);
        linearAnim.startAnimation(anim);
        new Thread() {
            public void run() {
                try {
                    TimeUnit.MILLISECONDS.sleep(2000);
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }

}