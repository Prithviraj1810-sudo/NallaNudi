package com.nallanudi.nallanudi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Seed DB in background then go to MainActivity
        new Thread(() -> {
            DataSeeder.seedIfEmpty(this);
            NotificationScheduler.scheduleDailyNotification(this);
            new Handler(getMainLooper()).postDelayed(() -> {
                startActivity(new Intent(
                        SplashActivity.this, MainActivity.class));
                finish();
            }, 2500);
        }).start();
    }
}
