package com.reodinas2.eatopiaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.reodinas2.eatopiaapp.config.Config;

public class MainActivity extends AppCompatActivity {

    String accessToken;
    Fragment homeFragment;
    Fragment mapFragment;
    Fragment favoriteFragment;
    Fragment myPageFragment;
    BottomNavigationView navigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 억세스토큰이 저장되어 있으면,
        // 로그인한 유저이므로 메인액티비티를 실행하고,

        // 그렇지 않으면,
        // 회원가입 액티비티를 실행하고 메인액티비티는 종료!

        SharedPreferences sp = getSharedPreferences(Config.SP_NAME, MODE_PRIVATE);
        accessToken = sp.getString(Config.ACCESS_TOKEN, "");

        if (accessToken.isEmpty()){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // 회원가입/로그인 유저면, 아래 코드를 실행하도록 둔다.


        // 프래그먼트 연결
        navigationView = findViewById(R.id.bottomNavigationView);
        homeFragment = new HomeFragment();
        mapFragment = new MapFragment();
        favoriteFragment = new FavoriteFragment();
        myPageFragment = new MyPageFragment();

        getSupportActionBar().setTitle("Home");

        // 탭바를 누르면 동작하는 코드
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemid = item.getItemId();

                Fragment fragment = null;

                if (itemid == R.id.HomeFragment){
                    fragment = homeFragment;
                    getSupportActionBar().setTitle("Home");

                } else if (itemid == R.id.MapFragment) {
                    fragment = mapFragment;
                    getSupportActionBar().setTitle("Map");


                } else if (itemid == R.id.FavoriteFragment) {
                    fragment = favoriteFragment;
                    getSupportActionBar().setTitle("Favorite");

                } else if (itemid == R.id.MyPageFragment) {
                    fragment = myPageFragment;
                    getSupportActionBar().setTitle("MyPage");
                }

                    return loadFragment(fragment);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }




    private boolean loadFragment(Fragment fragment){
        if (fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment, fragment)
                    .commit();
            return true;
        }else {
            return false;
        }
    }




}