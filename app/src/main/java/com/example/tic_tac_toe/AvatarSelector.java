package com.example.tic_tac_toe;

import android.widget.ImageView;

public class AvatarSelector {
    
    private final AvatarModel[] avatars;

    public AvatarSelector() {
        avatars = new AvatarModel[18];

        avatars[0] = new AvatarModel(0, R.drawable.avatar1_one);
        avatars[1] = new AvatarModel(1, R.drawable.avatar_two);
        avatars[2] = new AvatarModel(2, R.drawable.avatar_3);
        avatars[3] = new AvatarModel(3, R.drawable.avatar_4);
        avatars[4] = new AvatarModel(4, R.drawable.avatar_5);
        avatars[5] = new AvatarModel(5, R.drawable.avatar_6);
        avatars[6] = new AvatarModel(6, R.drawable.avatar_7);
        avatars[7] = new AvatarModel(7, R.drawable.avatar_8);
        avatars[8] = new AvatarModel(8, R.drawable.avatar_9);
        avatars[9] = new AvatarModel(9, R.drawable.avatar_10);
        avatars[10] = new AvatarModel(10, R.drawable.avatar_11);
        avatars[11] = new AvatarModel(11, R.drawable.avatar_12);
        avatars[12] = new AvatarModel(12,R.drawable.avatar_13);
        avatars[13] = new AvatarModel(13, R.drawable.avatar_14);
        avatars[14] = new AvatarModel(14, R.drawable.avatar_15);
        avatars[15] = new AvatarModel(15, R.drawable.avatar_16);
        avatars[16] = new AvatarModel(16, R.drawable.avatar_17);
        avatars[17] = new AvatarModel(17, R.drawable.avatar_18);
    }

    public AvatarModel[] getAvatars() {
        return avatars;
    }

    public void setSelectedImageID(AvatarModel avatar, ImageView imageView){

        imageView.setImageResource(avatar.getResID());
    }
}
