package com.example.tic_tac_toe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>  {

    ArrayList<ChatModel> chatList;
    Context context;

    public ChatAdapter(ArrayList<ChatModel> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_chat_bubble, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ChatModel chat = chatList.get(position);

        holder.imageView.setImageResource(chat.getImgResID());
        holder.textView.setText(chat.getMsg());

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public ArrayList<ChatModel> getChatList(){
        return chatList;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.bubble_icon);
            textView = itemView.findViewById(R.id.bubble_msg);

        }
    }
}
