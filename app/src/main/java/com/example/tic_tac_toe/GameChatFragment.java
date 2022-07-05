package com.example.tic_tac_toe;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GameChatFragment extends BottomSheetDialogFragment {

    private String keyValue;
    private EditText msgET;
    private Boolean isCreator;
    private ChatManager chatManager;

    public GameChatFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_game_chat, container, false);

        if (getArguments() != null) {
            chatManager = (ChatManager) getArguments().getSerializable("CHAT MANAGER");
            keyValue = chatManager.getKeyValue();
            isCreator = chatManager.isCreator();

        }


        RecyclerView chatRV = view.findViewById(R.id.msgRV);
        msgET = view.findViewById(R.id.msgET);
        ImageView sendBtn = view.findViewById(R.id.sendBtn);

        chatRV.setAdapter(chatManager.getChatAdapter());
        chatRV.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRV.scrollToPosition(chatManager.getChatAdapter().getItemCount()-1);


        FirebaseDatabase.getInstance().getReference().child("Game Rooms").child(keyValue).child("Chats").child("Creator").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    chatRV.scrollToPosition(chatManager.getChatAdapter().getItemCount()-1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Game Rooms").child(keyValue).child("Chats").child("Acceptor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    chatRV.scrollToPosition(chatManager.getChatAdapter().getItemCount()-1);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendBtn.setOnClickListener(v -> {
            if(!msgET.getText().toString().trim().equals("")){
                if(isCreator){
                    ChatModel newChat = new ChatModel(msgET.getText().toString().trim(), 1);

                    chatManager.getChatModels().add(newChat);
                    chatManager.getChatAdapter().notifyItemInserted(chatManager.getChatAdapter().getItemCount()-1);
                    chatRV.scrollToPosition(chatManager.getChatAdapter().getItemCount()-1);

                    FirebaseDatabase.getInstance().getReference().child("Game Rooms").child(keyValue).child("Chats").child("Creator").setValue(msgET.getText().toString().trim());
                }
                else {
                    ChatModel newChat = new ChatModel(msgET.getText().toString().trim(), 2);

                    chatManager.getChatModels().add(newChat);
                    chatManager.getChatAdapter().notifyItemInserted(chatManager.getChatAdapter().getItemCount()-1);
                    chatRV.scrollToPosition(chatManager.getChatAdapter().getItemCount()-1);

                    FirebaseDatabase.getInstance().getReference().child("Game Rooms").child(keyValue).child("Chats").child("Acceptor").setValue(msgET.getText().toString().trim());
                }
                msgET.setText("");

                new Handler().postDelayed(this::dismiss,400);
            }
        });

        return view;
    }

}