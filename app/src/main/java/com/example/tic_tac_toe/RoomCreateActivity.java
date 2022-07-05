package com.example.tic_tac_toe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class RoomCreateActivity extends AppCompatActivity implements AvatarChooserFragment.BottomSheetListener {

    SharedPreferences userInfoFolder;
    SharedPreferences.Editor userInfoFolderEditor;
    ConstraintLayout editBtn, avatarSelector;
    EditText userNameET, roomText;
    Button createBtn, joinBtn;
    TextView msgTV;
    FirebaseDatabase database;
    DatabaseReference roomRef;
    String roomCode, userName, keyValue;
    boolean isFound, isCreator, hasGameStarted;
    int avatarID;
    int win, loss, tie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_create);

        userInfoFolder = this.getSharedPreferences("UserInfo", MODE_PRIVATE);

        roomText = findViewById(R.id.room_text);

        userNameET = findViewById(R.id.username_et2);
        userNameET.setText(userInfoFolder.getString("UserName",""));

        win = userInfoFolder.getInt("win", 0);
        loss = userInfoFolder.getInt("loss", 0);
        tie = userInfoFolder.getInt("tie", 0);

        ((TextView)findViewById(R.id.win_TV)).setText(String.valueOf(win));
        ((TextView)findViewById(R.id.loss_TV)).setText(String.valueOf(loss));
        ((TextView)findViewById(R.id.tie_TV)).setText(String.valueOf(tie));

        createBtn = findViewById(R.id.create_btn);
        joinBtn = findViewById(R.id.join_btn);
        msgTV = findViewById(R.id.msg_tv);
        database = FirebaseDatabase.getInstance();
        roomRef = database.getReference().child("Game Rooms");
        isFound = false;
        hasGameStarted = false;
        keyValue = null;

        avatarID = userInfoFolder.getInt("AvatarID",0);
        AvatarSelector initialAvatarSelector = new AvatarSelector();
        for (AvatarModel avatars: initialAvatarSelector.getAvatars()
             ) {
            if(avatars.getId() == avatarID){
                initialAvatarSelector.setSelectedImageID(avatars, findViewById(R.id.mainAvatar));
            }
        }

        editBtn = findViewById(R.id.edit_btn);
        avatarSelector = findViewById(R.id.avatar_selector);

        createBtn.setOnClickListener(v -> createFun());
        joinBtn.setOnClickListener(v -> joinFun());

        editBtn.setOnClickListener(v -> {

            AvatarChooserFragment avatarChooserFragment = new AvatarChooserFragment();
            avatarChooserFragment.show(getSupportFragmentManager(), "AvatarChooserBottomSheet");

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(keyValue!=null){
            roomRef.child(keyValue).child("HasAccepted").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!hasGameStarted){
                        if(snapshot.exists()){
                            String value = snapshot.getValue(String.class);
                            assert value != null;
                            if(value.equals("no")){
                                roomRef.child(keyValue).removeValue();
                                hasGameStarted = true;
                            }
                        }
                        else {
                            roomRef.child(keyValue).removeValue();
                            hasGameStarted = true;
                        }
                    }
                    else {
                        roomRef.child(keyValue).child("HasAccepted").removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void createFun() {
        userName = userNameET.getText().toString().trim();

        userInfoFolderEditor = userInfoFolder.edit();
        userInfoFolderEditor.putString("UserName",userName);
        userInfoFolderEditor.apply();

        roomCode = roomText.getText().toString().trim();

        if(hasCorrectRoomCode(roomCode) && !userName.equals("")){
            viewEnabled(false);
            String msg1 = "Checking....";
            msgTV.setText(msg1);

            roomRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(!hasGameStarted){
                        boolean check = isAvailable(snapshot, roomCode);

                        new Handler().postDelayed(()->{
                            if(check){
                                if(isFound){
                                    viewEnabled(false);
                                }
                                else {
                                    viewEnabled(true);
                                    String msg = "Code already in use, Create another code";
                                    msgTV.setText(msg);
                                }

                            }
                            else{

                                roomRef.push().child("roomCode").setValue(roomCode);
                                isAvailable(snapshot, roomCode);
                                new Handler().postDelayed(()->{
                                    if(!hasGameStarted){
                                        String msg2 = "Ask your opponent to enter the code "+roomCode+" and click on join";
                                        msgTV.setText(msg2);
                                        roomRef.child(keyValue).child("Creator").child("UserName").setValue(userName);
                                        roomRef.child(keyValue).child("Creator").child("AvatarID").setValue(String.valueOf(avatarID));
                                        roomRef.child(keyValue).child("HasAccepted").setValue("no");
                                        viewEnabled(false);
                                        isFound = true;
                                        isCreator = true;
                                        accepted();
                                    }



                                },300);
                            }
                        },2000);
                    }
                    else roomRef.removeEventListener(this);



                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });




        }
        else {
            String text = "Enter userName and minimum four characters room code";
            msgTV.setText(text);
        }


    }

    private void joinFun(){

        keyValue = null;
        userName = userNameET.getText().toString().trim();
        userInfoFolderEditor = userInfoFolder.edit();
        userInfoFolderEditor.putString("UserName",userName);
        userInfoFolderEditor.apply();

        roomCode = roomText.getText().toString().trim();

        if(hasCorrectRoomCode(roomCode) && !userName.equals("")){
            viewEnabled(false);
            isFound = false;
            String msg1 = "Searching....";
            msgTV.setText(msg1);


            roomRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    boolean check = isJoinAvailable(snapshot, roomCode);

                    if(!hasGameStarted){
                        new Handler().postDelayed(()->{
                            if(!check){
                                String msg = "Room not Found...";
                                msgTV.setText(msg);
                                viewEnabled(true);
                            }
                            else{
                                if(!isFound){
                                    isAvailable(snapshot, roomCode);
                                    new Handler().postDelayed(()->{
                                        String msg2 = "Room found, joining... ";
                                        msgTV.setText(msg2);
                                        roomRef.child(keyValue).child("Acceptor").child("UserName").setValue(userName);
                                        roomRef.child(keyValue).child("Acceptor").child("AvatarID").setValue(String.valueOf(avatarID));
                                        roomRef.child(keyValue).child("HasAccepted").setValue("yes");
                                        viewEnabled(false);
                                        isFound = true;
                                        isCreator = false;
                                        accepted();

                                    },300);
                                }

                            }
                        },2000);
                    }
                    else roomRef.removeEventListener(this);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
        else {
            String text = "Enter userName and minimum four characters room code";
            msgTV.setText(text);
        }

    }

    private void accepted() {

        roomRef.child(keyValue).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(!hasGameStarted){
                    if(snapshot.exists()){
                        String acceptVal = snapshot.child("HasAccepted").getValue(String.class);
                        assert acceptVal != null;
                        if(acceptVal.equals("yes")){
                            viewEnabled(true);

                            String msgInitial = "Create a room code or join using a code";
                            msgTV.setText(msgInitial);

                            String p1name = Objects.requireNonNull(snapshot.child("Creator").child("UserName").getValue()).toString();
                            String p2name = Objects.requireNonNull(snapshot.child("Acceptor").child("UserName").getValue()).toString();

                            int p1AvatarID = Integer.parseInt(Objects.requireNonNull(snapshot.child("Creator").child("AvatarID").getValue(String.class)));
                            int p2AvatarID = Integer.parseInt(Objects.requireNonNull(snapshot.child("Acceptor").child("AvatarID").getValue(String.class)));

                            Intent intent = new Intent(RoomCreateActivity.this, GamePlayActivity.class);
                            intent.putExtra("PLAYER1", p1name);
                            intent.putExtra("PLAYER2", p2name);
                            intent.putExtra("KEY", keyValue);
                            intent.putExtra("COMPUTER CODE", 5);//change this to 5
                            intent.putExtra("IS CREATOR", isCreator);
                            intent.putExtra("P1ID", p1AvatarID);
                            intent.putExtra("P2ID", p2AvatarID);

                            if(!hasGameStarted){
                                Toast.makeText(RoomCreateActivity.this, "Don't Press Back Button", Toast.LENGTH_SHORT).show();
                                hasGameStarted = true;
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                }
                else roomRef.child(keyValue).removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private boolean hasCorrectRoomCode(String rCode){
        return rCode.length() > 3;
    }

    private boolean isAvailable(DataSnapshot snapshot, String rCode){
        for (DataSnapshot snaps: snapshot.getChildren()
             ) {
            String roomVal = snaps.child("roomCode").getValue(String.class);
            assert roomVal != null;
            if(roomVal.equals(rCode)){
                keyValue = snaps.getKey();
                return true;
            }
        }
        return false;
    }
    private boolean isJoinAvailable(DataSnapshot snapshot, String rCode){
        for (DataSnapshot snaps: snapshot.getChildren()
        ) {
            String roomVal = snaps.child("roomCode").getValue(String.class);
            String acceptVal = snaps.child("HasAccepted").getValue(String.class);
            assert roomVal != null;
            if(roomVal.equals(rCode)) {
                assert acceptVal != null;
                if (acceptVal.equals("no")) {
                    keyValue = snaps.getKey();
                    return true;
                }
            }
        }
        return false;
    }

    private void viewEnabled(boolean val){
        roomText.setEnabled(val);
        userNameET.setEnabled(val);
        createBtn.setEnabled(val);
        joinBtn.setEnabled(val);
        editBtn.setClickable(val);
    }

    @Override
    public void onAvatarChosen(int resID, int avatarNum) {
        ((ImageView)findViewById(R.id.mainAvatar)).setImageResource(resID);
        avatarID = avatarNum;
        userInfoFolderEditor = userInfoFolder.edit();
        userInfoFolderEditor.putInt("AvatarID",avatarID);
        userInfoFolderEditor.apply();
    }
}
