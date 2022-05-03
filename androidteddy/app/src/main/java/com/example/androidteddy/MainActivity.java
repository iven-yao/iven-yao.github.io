package com.example.androidteddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        SharedPreferences s1 = getSharedPreferences("FAVORITES", MODE_PRIVATE);
//        s1.edit().clear().commit();
//        SharedPreferences s2 = getSharedPreferences("PORTFOLIO", MODE_PRIVATE);
//        s2.edit().clear().commit();
//        SharedPreferences s3 = getSharedPreferences("NETWORTH", MODE_PRIVATE);
//        s3.edit().clear().commit();

        new Handler(Looper.getMainLooper()).postDelayed(()->{
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }, 3000);

    }
}