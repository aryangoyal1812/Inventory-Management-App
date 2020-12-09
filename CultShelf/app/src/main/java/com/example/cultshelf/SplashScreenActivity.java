package com.example.cultshelf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_splash__screen);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            Log.v("SplashScreenActivity","UserPresent");
            Intent intent = new Intent(SplashScreenActivity.this, Login.class);
            startActivity(intent);
            finish();
        }
        else{
            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
            finish();
        }

    }
}


