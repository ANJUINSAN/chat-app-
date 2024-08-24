package com.example.chatapp.activities;



import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.chatapp.R;
import com.example.chatapp.utilities.Constants;
import com.example.chatapp.utilities.PreferenceManager;
import com.google.firebase.firestore.auth.User;


public class SplashActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preferenceManager = new PreferenceManager(getApplicationContext());

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));

            }
            else{
                startActivity(new Intent(SplashActivity.this,SignInActivity.class));

            }
            finish();

        }, 2000);
    }
}