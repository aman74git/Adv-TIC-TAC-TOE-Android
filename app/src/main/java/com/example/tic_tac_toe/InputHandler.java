package com.example.tic_tac_toe;

import android.view.View;

public class InputHandler {

    private final View view;
    private final int posID;
    private boolean available;


    public InputHandler(View view, int posID ) {
        this.view = view;
        this.posID = posID;
        this.available = true;
        view.setBackgroundResource(0);
    }

    public View getView() {
        return view;
    }

    public int getPosID() {
        return posID;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
