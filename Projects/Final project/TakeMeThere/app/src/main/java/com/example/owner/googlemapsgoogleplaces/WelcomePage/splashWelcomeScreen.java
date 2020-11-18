package com.example.owner.googlemapsgoogleplaces.WelcomePage;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.owner.googlemapsgoogleplaces.R;

public class splashWelcomeScreen extends AppCompatActivity {

    ProgressBar progressBar ;
    private int progressStatus = 0 ;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_welcome_screen);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (progressStatus < 100) {
                    progressStatus++;
                    android.os.SystemClock.sleep(40);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progressStatus);
                        }
                    });
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(splashWelcomeScreen.this,welcomePage.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }).start();
    }
}
