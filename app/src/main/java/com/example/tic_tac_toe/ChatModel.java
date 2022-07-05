package com.example.tic_tac_toe;

public class ChatModel {
    private final String msg;
    private final int imgResID;

    public ChatModel(String msg, int sender) {
        this.msg = msg;
        if(sender == 1) imgResID = R.drawable.x;
        else imgResID = R.drawable.o;
    }

    public String getMsg() {
        return msg;
    }

    public int getImgResID() {
        return imgResID;
    }
}
