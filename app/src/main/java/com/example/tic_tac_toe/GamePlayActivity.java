package com.example.tic_tac_toe;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class GamePlayActivity extends AppCompatActivity {

    int computerNo, p1Score, p2Score, player;
    String p1Name, p2Name;
    List<InputHandler> myInputHandlers;
    Game onGoingGame;
    String keyValue;
    boolean isCreator;
    long backPressedTime;
    Toast backToast;
    GameChatFragment gameChatFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        keyValue = null;
        isCreator = true;

        //It will initialise the game board
        initialise(getIntent());

        //A new game will be started
        onGoingGame = new Game(this,player,p1Score,p2Score,computerNo,p1Name,p2Name,keyValue, isCreator, false);

        //If computer no. is 5,then it will start listening to the opponent, from fireBase
        if(computerNo == 5) onGoingGame.invokeOpponent(isCreator);

        myInputHandlers = Arrays.asList(onGoingGame.getInputHandlers());

        for (InputHandler box:myInputHandlers
             ) {
            box.getView().setOnClickListener(v -> onGoingGame.afterClick(box));
        }

        //When Play again will be clicked
        findViewById(R.id.playAgainBtn).setOnClickListener(v -> {


            //It will obtain the player1 and player2 score from last game
            p1Score = onGoingGame.getP1Score();
            p2Score = onGoingGame.getP2Score();

            if(computerNo == 5){

                if(player == 1) player = 2;
                else  player = 1;

                findViewById(R.id.playAgainBtn).setVisibility(View.GONE);
                findViewById(R.id.resultBoard).setVisibility(View.VISIBLE);

                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = this.getTheme();
                @ColorInt int textColor;
                String text = "waiting for rematch, from opponent";
                TextView progressBoard = findViewById(R.id.resultBoard);

                if(isCreator){
                    theme.resolveAttribute(R.attr.player1color, typedValue, true);

                    FirebaseDatabase.getInstance().getReference().child("Game Rooms").child(keyValue).child("creatorRematchReq").setValue("yes");

                    FirebaseDatabase.getInstance().getReference().child("Game Rooms").child(keyValue).child("acceptorRematchReq").addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if(snapshot.exists()){
                                onGoingGame = new Game(GamePlayActivity.this,player,p1Score,p2Score,computerNo,p1Name,p2Name, keyValue, isCreator, true);
                                onGoingGame.invokeOpponent(isCreator);
                                myInputHandlers = Arrays.asList(onGoingGame.getInputHandlers());
                                for (InputHandler box:myInputHandlers) {
                                    box.getView().setOnClickListener( vw -> onGoingGame.afterClick(box));
                                }
                                FirebaseDatabase.getInstance().getReference().child("Game Rooms").child(keyValue).child("acceptorRematchReq").removeEventListener(this);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("Game Rooms").child(keyValue).child("acceptorRematchReq").setValue("yes");
                    theme.resolveAttribute(R.attr.player2color, typedValue, true);
                    FirebaseDatabase.getInstance().getReference().child("Game Rooms").child(keyValue).child("creatorRematchReq").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if(snapshot.exists()){

                                onGoingGame = new Game(GamePlayActivity.this,player,p1Score,p2Score,computerNo,p1Name,p2Name, keyValue, isCreator, true);
                                onGoingGame.invokeOpponent(isCreator);

                                myInputHandlers = Arrays.asList(onGoingGame.getInputHandlers());
                                for (InputHandler box:myInputHandlers) {
                                    box.getView().setOnClickListener( vw -> onGoingGame.afterClick(box));
                                }

                                FirebaseDatabase.getInstance().getReference().child("Game Rooms").child(keyValue).child("creatorRematchReq").removeEventListener(this);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                textColor = typedValue.data;
                progressBoard.setText(text);
                progressBoard.setTextColor(textColor);
            }
            else{
                if(computerNo == 0 ){
                    if(player == 1) player = 2;
                    else  player = 1;
                }
                onGoingGame = new Game(this,player,p1Score,p2Score,computerNo,p1Name,p2Name, keyValue, isCreator, true);
                myInputHandlers = Arrays.asList(onGoingGame.getInputHandlers());
                for (InputHandler box:myInputHandlers) {
                    box.getView().setOnClickListener( vw -> onGoingGame.afterClick(box));
                }

            }

        });


        //when msg Box will be clicked in online game
        findViewById(R.id.mainChatView).setOnClickListener(v -> {
            gameChatFragment.show(getSupportFragmentManager(), "GAME CHAT");
            v.setClickable(false);
            new Handler().postDelayed(()-> v.setClickable(true), 1000);

        });

        //End the game if opponent exits, in online match
        if(computerNo == 5){
            FirebaseDatabase.getInstance().getReference().child("Game Rooms").child(keyValue).child("Game Over").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String str = snapshot.getValue(String.class);
                        assert str != null;
                        if(str.equals("true")){
                            onGoingGame.setHasGameEnded(true);
                            Toast.makeText(GamePlayActivity.this, "Game ended " , Toast.LENGTH_SHORT).show();
                            finish();
                            FirebaseDatabase.getInstance().getReference().child("Game Rooms").child(keyValue).removeValue();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    @Override
    public void onBackPressed() {

        if(backPressedTime + 2000 >System.currentTimeMillis()){
            if(computerNo == 5){
                onGoingGame.setHasGameEnded(true);
                FirebaseDatabase.getInstance().getReference().child("Game Rooms").child(keyValue).child("Game Over").setValue("true");
            }
            backToast.cancel();
            super.onBackPressed();
            return;
        }
        else{
            backToast = Toast.makeText(this, "Press again to end the game", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }


    private void initialise(Intent intent){
        computerNo = intent.getIntExtra("COMPUTER CODE",1);
        if(computerNo == 0 || computerNo == 5){
            p1Name = intent.getStringExtra("PLAYER1");
            p2Name = intent.getStringExtra("PLAYER2");
        } else{
            p1Name = "You";
            p2Name = "Computer";
        }

        if(computerNo == 5){
            keyValue = intent.getStringExtra("KEY");
            isCreator = intent.getBooleanExtra("IS CREATOR", false);

            //Initialising Chat
            findViewById(R.id.mainChatView).setVisibility(View.VISIBLE);
            Bundle bundle = new Bundle();
            bundle.putSerializable("CHAT MANAGER", new ChatManager(keyValue, isCreator, this, findViewById(R.id.mainChatText)));
            gameChatFragment = new GameChatFragment();
            gameChatFragment.setArguments(bundle);

        }
        else player = 1;

        TextView p1Text = findViewById(R.id.textView6);
        p1Text.setText(p1Name);
        TextView p2Text = findViewById(R.id.textView7);
        p2Text.setText(p2Name);
        ImageView p1Avatar = findViewById(R.id.avat1);
        ImageView p2Avatar = findViewById(R.id.mainAvatar);
        switch (computerNo){

            case 0:
                p2Avatar.setImageResource(R.drawable.avatar_two);
                break;

            case 1:
                p2Avatar.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.easy_bot));
                break;

            case 2:
                p2Avatar.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.medium_bot));
                break;

            case 3:
                p2Avatar.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.difficult_bot));
                break;

            case 4:
                p2Avatar.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.random_bot));
                break;

            case 5:
                int p1ID = intent.getIntExtra("P1ID",0);
                int p2ID = intent.getIntExtra("P2ID",0);

                AvatarSelector myAvatarSelector = new AvatarSelector();
                AvatarModel[] myAvatar = myAvatarSelector.getAvatars();


                for (AvatarModel avatars : myAvatar
                     ) {
                    if(avatars.getId() == p1ID){
                        myAvatarSelector.setSelectedImageID(avatars,p1Avatar);
                    }
                    if(avatars.getId() == p2ID){
                        myAvatarSelector.setSelectedImageID(avatars,p2Avatar);
                    }
                }
                break;
        }
        p1Score = 0;
        p2Score = 0;
        player = 1;
    }



}