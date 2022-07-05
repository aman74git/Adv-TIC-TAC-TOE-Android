package com.example.tic_tac_toe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class ChooseBotActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_bot);

        findViewById(R.id.view).setOnClickListener(v -> newIntent(1));
        findViewById(R.id.textView2).setOnClickListener(v -> newIntent(1));

        findViewById(R.id.view2).setOnClickListener(v -> newIntent(2));
        findViewById(R.id.textView3).setOnClickListener(v -> newIntent(2));

        findViewById(R.id.view3).setOnClickListener(v -> newIntent(3));
        findViewById(R.id.textView4).setOnClickListener(v -> newIntent(3));

        findViewById(R.id.view4).setOnClickListener(v -> newIntent(4));
        findViewById(R.id.textView5).setOnClickListener(v -> newIntent(4));

    }
    private void newIntent(int value){
        Intent intent = new Intent(this, GamePlayActivity.class);
        intent.putExtra("COMPUTER CODE", value);
        startActivity(intent);
        finish();
    }
}