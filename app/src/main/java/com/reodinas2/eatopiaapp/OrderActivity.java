package com.reodinas2.eatopiaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.reodinas2.eatopiaapp.model.MenuInfo;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {

    TextView txtView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        txtView = findViewById(R.id.txtView);

        Intent intent = getIntent();
        ArrayList<MenuInfo> menuInfoArrayList = (ArrayList<MenuInfo>) intent.getSerializableExtra("menuInfoArrayList");
        txtView.setText(menuInfoArrayList.get(0).getMenuId()+", "+menuInfoArrayList.get(0).getCount());
    }
}