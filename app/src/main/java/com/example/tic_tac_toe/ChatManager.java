package com.example.tic_tac_toe;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.TypedValue;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

public class ChatManager implements Serializable {
    private final String keyValue;
    private final boolean isCreator;
    private final ChatAdapter chatAdapter;
    private final ArrayList<ChatModel> chatModels;
    private final Context context;

    public ChatManager(String keyValue, boolean isCreator, Context context, TextView textView) {
        this.keyValue = keyValue;
        this.isCreator = isCreator;
        this.context = context;

        chatModels = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatModels, context);

        if(isCreator){
            FirebaseDatabase.getInstance().getReference().child("Game Rooms").child(keyValue).child("Chats").child("Acceptor").addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.exists()){
                        String newMsg = snapshot.getValue(String.class);
                        ChatModel newChat = new ChatModel(newMsg, 2);
                        chatModels.add(newChat);
                        chatAdapter.notifyItemInserted(chatAdapter.getItemCount()-1);

                        setColor(R.attr.player2color, textView);

                        assert newMsg != null;
                        if(newMsg.length()<15){
                            textView.setText(newMsg);
                        }
                        else {
                            String msg = newMsg.substring(0,13)+"...";
                            textView.setText(msg);
                        }


                        new Handler().postDelayed(()->{
                            String text = "Tap to Chat";
                            setColor(R.attr.primaryTextColor, textView);
                            textView.setText(text);
                        }, 3000);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else {
            FirebaseDatabase.getInstance().getReference().child("Game Rooms").child(keyValue).child("Chats").child("Creator").addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.exists()){
                        String newMsg = snapshot.getValue(String.class);
                        ChatModel newChat = new ChatModel(newMsg, 1);
                        chatModels.add(newChat);
                        chatAdapter.notifyItemInserted(chatAdapter.getItemCount()-1);

                        setColor(R.attr.player2color, textView);

                        assert newMsg != null;
                        if(newMsg.length()<15){
                            textView.setText(newMsg);
                        }
                        else {
                            String msg = newMsg.substring(0,13)+"...";
                            textView.setText(msg);
                        }


                        new Handler().postDelayed(()->{
                            String text = "Tap to Chat";
                            setColor(R.attr.primaryTextColor, textView);
                            textView.setText(text);
                        }, 3000);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public ArrayList<ChatModel> getChatModels() {
        return chatModels;
    }

    public ChatAdapter getChatAdapter() {
        return chatAdapter;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public boolean isCreator() {
        return isCreator;
    }

    private void setColor(int resId, TextView textView){
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        @ColorInt int textColor;
        theme.resolveAttribute(resId, typedValue, true);
        textColor = typedValue.data;
        textView.setTextColor(textColor);

    }
}
