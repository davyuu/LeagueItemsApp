package com.davyuu.leagueitemsmobafire;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        Intent i = getIntent();
        String name = i.getStringExtra("name");
        setTitle(name);
    }
}
