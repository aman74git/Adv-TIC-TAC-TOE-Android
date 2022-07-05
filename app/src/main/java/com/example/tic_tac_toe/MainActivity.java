package com.example.tic_tac_toe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(v -> startActivity(new Intent(this, ChooseBotActivity.class)));

        findViewById(R.id.button2).setOnClickListener(v -> startActivity( new Intent(this, EnterPlayerNamesActivity.class)));

        findViewById(R.id.button4).setOnClickListener(v -> startActivity( new Intent(this, RoomCreateActivity.class)));
    }
}