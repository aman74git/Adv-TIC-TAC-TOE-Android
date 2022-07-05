package com.example.tic_tac_toe;

import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AvatarChooserFragment extends BottomSheetDialogFragment {

    private BottomSheetListener bottomSheetListener;

    public AvatarChooserFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_avatar_chooser, container, false);

        AvatarModel[] avatars = new AvatarModel[18];
        avatars[0] = new AvatarModel(view.findViewById(R.id.av1),0, R.drawable.avatar1_one);
        avatars[1] = new AvatarModel(view.findViewById(R.id.av2),1, R.drawable.avatar_two);
        avatars[2] = new AvatarModel(view.findViewById(R.id.av3),2, R.drawable.avatar_3);
        avatars[3] = new AvatarModel(view.findViewById(R.id.av4),3, R.drawable.avatar_4);
        avatars[4] = new AvatarModel(view.findViewById(R.id.av5),4, R.drawable.avatar_5);
        avatars[5] = new AvatarModel(view.findViewById(R.id.av6),5, R.drawable.avatar_6);
        avatars[6] = new AvatarModel(view.findViewById(R.id.av7),6, R.drawable.avatar_7);
        avatars[7] = new AvatarModel(view.findViewById(R.id.av8),7, R.drawable.avatar_8);
        avatars[8] = new AvatarModel(view.findViewById(R.id.av9),8, R.drawable.avatar_9);
        avatars[9] = new AvatarModel(view.findViewById(R.id.av10),9, R.drawable.avatar_10);
        avatars[10] = new AvatarModel(view.findViewById(R.id.av11),10, R.drawable.avatar_11);
        avatars[11] = new AvatarModel(view.findViewById(R.id.av12),11, R.drawable.avatar_12);
        avatars[12] = new AvatarModel(view.findViewById(R.id.av13),12,R.drawable.avatar_13);
        avatars[13] = new AvatarModel(view.findViewById(R.id.av14),13, R.drawable.avatar_14);
        avatars[14] = new AvatarModel(view.findViewById(R.id.av15),14, R.drawable.avatar_15);
        avatars[15] = new AvatarModel(view.findViewById(R.id.av16),15, R.drawable.avatar_16);
        avatars[16] = new AvatarModel(view.findViewById(R.id.av17),16, R.drawable.avatar_17);
        avatars[17] = new AvatarModel(view.findViewById(R.id.av18),17, R.drawable.avatar_18);

        for (AvatarModel avatar : avatars
                ) {
            avatar.getImageView().setOnClickListener(v -> {
                bottomSheetListener.onAvatarChosen(avatar.getResID(), avatar.getId());
                dismiss();
            });
        }

        return view;
    }
    public interface BottomSheetListener{
        void onAvatarChosen(int resID, int avatarNum);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        bottomSheetListener = (BottomSheetListener) context;
    }
}