package com.example.tic_tac_toe;

import android.widget.ImageView;

public class AvatarModel {

    private final ImageView imageView;
    private final int id, resID;

    public AvatarModel(ImageView imageView, int id, int resID) {

        this.imageView = imageView;
        this.id = id;
        this.resID = resID;
    }

    public AvatarModel(int id, int resID) {
        this.imageView = null;
        this.id = id;
        this.resID = resID;
    }

    public int getResID(){
        return resID;
    }

    public ImageView getImageView() {
        return imageView;
    }
    public int getId() {
        return id;
    }
}
