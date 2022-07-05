package com.example.tic_tac_toe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class EnterPlayerNamesActivity extends AppCompatActivity {

    EditText player1;
    EditText player2;
    String player1Name;
    String player2Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_player_names);

        player1 = findViewById(R.id.username_et);
        player2 = findViewById(R.id.editTextTextPersonName2);


        findViewById(R.id.button3).setOnClickListener(v -> {

            player1Name = player1.getText().toString().trim().substring(0,1).toUpperCase() + player1.getText().toString().trim().substring(1);
            player2Name = player2.getText().toString().trim().substring(0,1).toUpperCase() + player2.getText().toString().trim().substring(1);

            Intent intent = new Intent(this, GamePlayActivity.class);
            intent.putExtra("PLAYER1", player1Name);
            intent.putExtra("PLAYER2", player2Name);
            intent.putExtra("COMPUTER CODE", 0);
            startActivity(intent);
            finish();
        });
    }
}