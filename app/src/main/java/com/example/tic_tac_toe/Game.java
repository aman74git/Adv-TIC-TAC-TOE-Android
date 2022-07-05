package com.example.tic_tac_toe;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game implements Serializable {

    SharedPreferences userInfoFolder;
    SharedPreferences.Editor userInfoFolderEditor;
    private int player, p1Score, p2Score;
    private final int computerNo;
    private int p2Status;
    private int counter;
    private final String p1Name, p2Name;
    private String winnerCode;
    private final Context context;
    private final InputHandler[] inputHandlers;
    List<InputHandler> unlockedInputHandlers;
    private final List<Integer> p1List, p2List;
    private final String keyValue;
    private final boolean isCreator;
    private boolean hasGameEnded;

    public Game(Context context, int player, int p1Score, int p2Score, int computerNo, String p1Name, String p2Name, String keyValue, boolean isCreator, boolean isRematch) {

        //initialising variables
        if(computerNo == 5 ){
            userInfoFolder = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        }
        this.keyValue = keyValue;
        this.context = context;
        this.player = player;
        this.p1Score = p1Score;
        this.p2Score = p2Score;
        this.p1Name = p1Name;
        this.p2Name = p2Name;
        this.counter = 0;
        this.p1List = new ArrayList<>();
        this.p2List = new ArrayList<>();
        inputHandlers = new InputHandler[9];
        this.isCreator = isCreator;
        if(computerNo == 4){
            this.computerNo = 1 + new Random().nextInt(3);
        }
        else this.computerNo = computerNo;

        //Initialising boxes
        inputHandlers[0] = new InputHandler(((Activity)context).findViewById(R.id.view10),0);
        inputHandlers[1] = new InputHandler(((Activity)context).findViewById(R.id.view22),1);
        inputHandlers[2] = new InputHandler(((Activity)context).findViewById(R.id.view01),2);
        inputHandlers[3] = new InputHandler(((Activity)context).findViewById(R.id.view02),3);
        inputHandlers[4] = new InputHandler(((Activity)context).findViewById(R.id.view11),4);
        inputHandlers[5] = new InputHandler(((Activity)context).findViewById(R.id.view20),5);
        inputHandlers[6] = new InputHandler(((Activity)context).findViewById(R.id.view21),6);
        inputHandlers[7] = new InputHandler(((Activity)context).findViewById(R.id.view00),7);
        inputHandlers[8] = new InputHandler(((Activity)context).findViewById(R.id.view12),8);

        //Updating Player1 and Player2 score
        TextView p1Board = ((Activity)context).findViewById(R.id.p1Score);
        String p1ScoreText = Integer.toString(p1Score);
        p1Board.setText(p1ScoreText);

        TextView p2Board = ((Activity)context).findViewById(R.id.p2Score);
        String p2ScoreText = Integer.toString(p2Score);
        p2Board.setText(p2ScoreText);

        //Enabling and disabling Views
        ((Activity)context).findViewById(R.id.resultBox).setVisibility(View.GONE);
        ((Activity)context).findViewById(R.id.playAgainBtn).setVisibility(View.GONE);
        ((Activity)context).findViewById(R.id.gameBox).setVisibility(View.VISIBLE);
        ((Activity)context).findViewById(R.id.resultBoard).setVisibility(View.VISIBLE);
        ((Activity)context).findViewById(R.id.line258).setVisibility(View.GONE);
        ((Activity)context).findViewById(R.id.line036).setVisibility(View.GONE);
        ((Activity)context).findViewById(R.id.line345).setVisibility(View.GONE);
        ((Activity)context).findViewById(R.id.line012).setVisibility(View.GONE);
        ((Activity)context).findViewById(R.id.line048).setVisibility(View.GONE);
        ((Activity)context).findViewById(R.id.line147).setVisibility(View.GONE);
        ((Activity)context).findViewById(R.id.line246).setVisibility(View.GONE);
        ((Activity)context).findViewById(R.id.line678).setVisibility(View.GONE);

        //Showing first turn
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        @ColorInt int textColor;
        String text;
        TextView progressBoard = ((Activity)context).findViewById(R.id.resultBoard);

        if(player == 1){
            text = p1Name + " will start";
            theme.resolveAttribute(R.attr.player1color, typedValue, true);
        }
        else {
            text = p2Name + " will start ";
            theme.resolveAttribute(R.attr.player2color, typedValue, true);
        }
        textColor = typedValue.data;
        progressBoard.setText(text);
        progressBoard.setTextColor(textColor);

        showTurn(player);

        if(computerNo == 5 && !isCreator && player == 1){
            lockUnlocked();
        }
        if(computerNo == 5 && isCreator && player == 2){
            lockUnlocked();
        }
        hasGameEnded = false;

        if(computerNo == 5 && isRematch){
            if(isCreator){
                FirebaseDatabase.getInstance().getReference().child("Game Rooms").child(keyValue).child("acceptorRematchReq").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!hasGameEnded){
                            if(snapshot.exists()){
                                FirebaseDatabase.getInstance().getReference().child("Game Rooms").child(keyValue).child("acceptorRematchReq").removeValue();
                            }
                        }
                        else{
                            FirebaseDatabase.getInstance().getReference().child("Game Rooms").child(keyValue).child("acceptorRematchReq").removeEventListener(this);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            else{
                FirebaseDatabase.getInstance().getReference().child("Game Rooms").child(keyValue).child("creatorRematchReq").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!hasGameEnded){
                            if(snapshot.exists()){
                                FirebaseDatabase.getInstance().getReference().child("Game Rooms").child(keyValue).child("creatorRematchReq").removeValue();
                            }
                        }
                        else{
                            FirebaseDatabase.getInstance().getReference().child("Game Rooms").child(keyValue).child("creatorRematchReq").removeEventListener(this);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }


    }

    public void setHasGameEnded(boolean hasGameEnded) {
        this.hasGameEnded = hasGameEnded;
    }

    public int getP1Score() {
        return p1Score;
    }

    public int getP2Score() {
        return p2Score;
    }

    public InputHandler[] getInputHandlers() {
        return inputHandlers;
    }

    public void afterClick(InputHandler box){
        if(box.isAvailable()){
            if(player == 1){
                assignX(box);

                if(computerNo ==5 ){
                    lockUnlocked();
                    FirebaseDatabase.getInstance().getReference().child("Game Rooms").child(keyValue).child("GamePlay").child("creatorTurn").setValue(String.valueOf(box.getPosID()));

                }

                printWinner(checkWinner());
            }else if(player == 2 && computerNo == 0){
                assignO(box);
                printWinner(checkWinner());
            }
            else if(player == 2 && computerNo == 5){
                assignO(box);
                lockUnlocked();
                FirebaseDatabase.getInstance().getReference().child("Game Rooms").child(keyValue).child("GamePlay").child("acceptorTurn").setValue(String.valueOf(box.getPosID()));
                printWinner(checkWinner());
            }

        }
    }


    //helper functions

    // It will lock all the boxes
    private void lockUnlocked() {
        unlockedInputHandlers = getAvailableBoxes();
        for (InputHandler box: unlockedInputHandlers
             ) {
            box.setAvailable(false);
        }

    }
    private void unlock() {

        for (InputHandler box: unlockedInputHandlers
        ) {
            box.setAvailable(true);
        }

    }



    //it will assign X to the corresponding box
    private void assignX(InputHandler box){
        box.getView().setBackground(ContextCompat.getDrawable(context,R.drawable.x));
        box.setAvailable(false);
        p1List.add(box.getPosID());
        counter++;
    }

    //it will assign O to the corresponding box
    private void assignO(InputHandler box){
        box.getView().setBackground(ContextCompat.getDrawable(context,R.drawable.o));
        box.setAvailable(false);
        p2List.add(box.getPosID());
        counter++;
    }

    //function to change the turn
    private void changeTurn() {
        if(player == 1) player = 2;
        else player = 1;
        showTurn(player);
    }

    //function to make winning line visible
    private void makeLineVisible(View view){
        view.setVisibility(View.VISIBLE);
        if(player == 1) view.setBackground(ContextCompat.getDrawable(context,R.drawable.btn1bg));
        else view.setBackground(ContextCompat.getDrawable(context,R.drawable.btn2bg));
    }

    //this will highlight the box for the player's turn
    private void showTurn(int player){
        ConstraintLayout player1Card = ((Activity)context).findViewById(R.id.p1Constraint);
        ConstraintLayout player2Card = ((Activity)context).findViewById(R.id.p2Constraint);
        if(player == 1){
            if(computerNo == 5){
                if(isCreator){
                    player1Card.setBackground(ContextCompat.getDrawable(context,R.drawable.player_1_bg));
                }
                else{
                    player1Card.setBackgroundResource(0);
                }
            }
            else {
                player1Card.setBackground(ContextCompat.getDrawable(context,R.drawable.player_1_bg));
            }
            player2Card.setBackgroundResource(0);
        }
        else if(player == 2){
            if(computerNo == 5){
                if(isCreator){
                    player2Card.setBackgroundResource(0);
                }
                else{
                    player2Card.setBackground(ContextCompat.getDrawable(context,R.drawable.player_2_bg));
                }
            }
            else {
                player2Card.setBackground(ContextCompat.getDrawable(context,R.drawable.player_2_bg));
            }
            player1Card.setBackgroundResource(0);
        }

        //when match is finished
        else {
            player1Card.setBackgroundResource(0);
            player2Card.setBackgroundResource(0);
        }
    }

    //function that returns the lists of the available boxes
    private List<InputHandler> getAvailableBoxes(){
        List<InputHandler> availableBox = new ArrayList<>();
        for (InputHandler obj:inputHandlers
        ) {
            if(obj.isAvailable()){
                availableBox.add(obj);
            }
        }
        return availableBox;
    }


    //AI autoGamePlay functions

    //function that will make computer play randomly(easy)
    private void randomPlay(){
        List<InputHandler> availableBox = getAvailableBoxes();

        Random rand = new Random();
        int rand_int = rand.nextInt(availableBox.size());

        assignO(availableBox.get(rand_int));

    }

    //computer play medium function
    private void mediumBotPlay(){

        if(counter == 1){
            randomPlay();
        }
        else{
            int flagWin = 0;
            int flagSaved = 0;

            for(int i = 0; i < p2List.size()-1; i++){
                for(int j = i+1; j < p2List.size(); j++){

                    int probableNextIndex = 12-(p2List.get(i)+p2List.get(j));

                    if (probableNextIndex < 9 && probableNextIndex >= 0) {
                        if(inputHandlers[probableNextIndex].isAvailable()){

                            assignO(inputHandlers[probableNextIndex]);
                            flagWin = 1;
                            break;
                        }
                    }

                }
                if(flagWin == 1) break;
            }

            if(flagWin == 0){
                for(int i = 0; i < p1List.size()-1; i++){
                    for(int j = i+1; j < p1List.size(); j++){
                        int probableNextIndex = 12-(p1List.get(i)+p1List.get(j));
                        if (probableNextIndex < 9 && probableNextIndex >= 0) {
                            if(inputHandlers[probableNextIndex].isAvailable()){

                                assignO(inputHandlers[probableNextIndex]);
                                flagSaved = 1;
                                break;
                            }
                        }

                    }
                    if(flagSaved == 1) break;
                }
            }

            if(flagSaved == 0 && flagWin == 0) randomPlay();
        }

    }

    //impossibleBotPlay impossible function
    private void impossibleBotPlay(){
        List<InputHandler> availableBoxes = getAvailableBoxes();
        int maxScore = Integer.MIN_VALUE;
        InputHandler bestPlace = null;
        for (int i = 0; i < availableBoxes.size(); i++){
            p2List.add(availableBoxes.get(i).getPosID());
            ++counter;
            availableBoxes.get(i).setAvailable(false);

            int score = minimax(0, "player1");

            p2List.remove(p2List.size()-1);
            --counter;
            availableBoxes.get(i).setAvailable(true);

            if(score > maxScore){
                maxScore = score;
                bestPlace = availableBoxes.get(i);
            }
        }
        assert bestPlace != null;
        assignO(bestPlace);
    }
    private int minimax(int depth, String player){
        if(checkWinner() == 1){
            return p2Status;
        }
        if(checkWinner() == -1){
            return 0;
        }

        if(player.equals("player2")){
            List<InputHandler> availableBoxes = getAvailableBoxes();
            int maxScore = Integer.MIN_VALUE;
            for(int i = 0; i < availableBoxes.size(); i++){
                p2List.add(availableBoxes.get(i).getPosID());
                ++counter;
                availableBoxes.get(i).setAvailable(false);
                int score = minimax(depth+1, "player1");

                p2List.remove(p2List.size()-1);
                --counter;
                availableBoxes.get(i).setAvailable(true);

                if(score > maxScore) {
                    maxScore = score;
                }
            }
            return maxScore;
        }
        else{
            List<InputHandler> availableBoxes = getAvailableBoxes();
            int minScore = Integer.MAX_VALUE;
            for (int i = 0; i < availableBoxes.size(); i++){
                p1List.add(availableBoxes.get(i).getPosID());
                ++counter;
                availableBoxes.get(i).setAvailable(false);
                int score = minimax(depth+1, "player2");

                p1List.remove(p1List.size()-1);
                --counter;
                availableBoxes.get(i).setAvailable(true);

                if(score < minScore){
                    minScore = score;
                }
            }
            return minScore;
        }
    }

    //onlinePlayFunction
    public void invokeOpponent(boolean isCreator) {
        if(isCreator){
            FirebaseDatabase.getInstance().getReference().child("Game Rooms").child(keyValue).child("GamePlay").child("acceptorTurn").setValue("-1");
            new Handler().postDelayed(()-> FirebaseDatabase.getInstance().getReference().child("Game Rooms").child(keyValue).child("GamePlay").child("acceptorTurn").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(!hasGameEnded){
                        if(snapshot.exists()){
                            String snapStr = snapshot.getValue(String.class);

                            assert snapStr != null;
                            int snapVal = Integer.parseInt(snapStr);
                            if(snapVal != -1){
                                unlock();
                                assignO(inputHandlers[snapVal]);
                                printWinner(checkWinner());

                            }

                        }
                    }
                    else FirebaseDatabase.getInstance().getReference().child("Game Rooms").child(keyValue).child("GamePlay").child("acceptorTurn").removeEventListener(this);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            }), 200);

        }

        else{
            FirebaseDatabase.getInstance().getReference().child("Game Rooms").child(keyValue).child("GamePlay").child("creatorTurn").setValue("-1");
            new Handler().postDelayed(()-> FirebaseDatabase.getInstance().getReference().child("Game Rooms").child(keyValue).child("GamePlay").child("creatorTurn").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(!hasGameEnded){
                        if(snapshot.exists()){
                            String snapStr = snapshot.getValue(String.class);

                            assert snapStr != null;
                            int snapVal = Integer.parseInt(snapStr);
                            if(snapVal != -1){
                                unlock();
                                assignX(inputHandlers[snapVal]);
                                printWinner(checkWinner());
                            }

                        }
                    }
                    else FirebaseDatabase.getInstance().getReference().child("Game Rooms").child(keyValue).child("GamePlay").child("creatorTurn").removeEventListener(this);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            }), 200);
        }
    }


    //function for checking and obtaining winner

    //function to check winner
    private int checkWinner() {

        //when no. of case is less than the no. of cases to win
        if(counter < 5) {
            return 0;
        }

        //when there is probability of any player to win
        else {

            //1st checking for player one
            for(int i = 0; i < p1List.size()-2; i++){
                for(int j = i+1; j < p1List.size()-1; j++){
                    for(int k = j+1; k < p1List.size(); k++){

                        if(p1List.get(i)+p1List.get(j)+p1List.get(k) == 12){

                            //sorting i,j,k to obtain winnerCode
                            int[] min = {p1List.get(i),p1List.get(j),p1List.get(k)};
                            for(int z = 0; z<2; z++){
                                int minimum = min[z];
                                for(int x = z+1; x<3; x++){
                                    if(min[x]<minimum){
                                        minimum = min[x];
                                        min[x] = min[z];
                                        min[z] = minimum;
                                    }
                                }
                            }

                            //assigning winner codes and other variables
                            winnerCode = ""+ min[0] + "" + min[1] + "" + min[2];
                            p2Status = -100;
                            return 1;
                        }

                    }
                }
            }

            //now checking for player 2
            for(int i = 0; i < p2List.size()-2; i++){
                for(int j = i+1; j < p2List.size()-1; j++){
                    for(int k = j+1; k < p2List.size(); k++){

                        if(p2List.get(i)+p2List.get(j)+p2List.get(k) == 12){
                            //sorting
                            int[] min = {p2List.get(i),p2List.get(j),p2List.get(k)};
                            for(int z = 0; z<2; z++){
                                int minimum = min[z];
                                for(int x = z+1; x<3; x++){
                                    if(min[x]<minimum){
                                        minimum = min[x];
                                        min[x] = min[z];
                                        min[z] = minimum;
                                    }
                                }
                            }

                            //assigning winner codes and other variables
                            winnerCode = ""+ min[0] + "" + min[1] + "" + min[2];
                            p2Status = 100;
                            return 1;
                        }
                    }
                }
            }



            //if we don't get a winner then we reach here

            if(counter < 9){
                return 0;
            }

            //condition if match draws
            else {
                p2Status = 0;
                return -1;
            }
        }
    }


    //function to print, winner or gameStatus
    private void printWinner(int matchStatus) {

        TextView progressBoard = ((Activity)context).findViewById(R.id.resultBoard);

        //if any player has won
        if(matchStatus  == 1){

            if(player == 1){

                //updating scoreBoard
                p1Score++;
                TextView p1Board = ((Activity)context).findViewById(R.id.p1Score);
                String p1ScoreText = Integer.toString(p1Score);
                p1Board.setText(p1ScoreText);

                //If it is an online game then save the result
                if(computerNo == 5){
                    userInfoFolderEditor = userInfoFolder.edit();
                    if(isCreator){
                        userInfoFolderEditor.putInt("win",(1+userInfoFolder.getInt("win", 0)));
                    }
                    else{
                        userInfoFolderEditor.putInt("loss",(1+userInfoFolder.getInt("loss", 0)));
                    }
                    userInfoFolderEditor.apply();

                }
            }
            else {

                //updating scoreBoard
                p2Score++;
                TextView p2Board = ((Activity)context).findViewById(R.id.p2Score);
                String p2ScoreText = Integer.toString(p2Score);
                p2Board.setText(p2ScoreText);

                //If online then result is saved
                if(computerNo == 5){
                    userInfoFolderEditor = userInfoFolder.edit();
                    if(!isCreator){
                        userInfoFolderEditor.putInt("win",(1+userInfoFolder.getInt("win", 0)));
                    }
                    else{
                        userInfoFolderEditor.putInt("loss", (1+userInfoFolder.getInt("loss", 0)));
                    }
                    userInfoFolderEditor.apply();

                }
            }
            showTurn(0); //None of the player's chance

            //lock all the box
            lockUnlocked();

            // display winning line according to the winnerCode
            switch (winnerCode) {
                case "237": {
                    makeLineVisible(((Activity)context).findViewById(R.id.line012));
                    break;
                }
                case "048": {
                    makeLineVisible(((Activity)context).findViewById(R.id.line345));
                    break;
                }
                case "156": {
                    makeLineVisible(((Activity)context).findViewById(R.id.line678));
                    break;
                }
                case "057": {
                    makeLineVisible(((Activity)context).findViewById(R.id.line036));
                    break;
                }
                case "246": {
                    makeLineVisible(((Activity)context).findViewById(R.id.line147));
                    break;
                }
                case "138": {
                    makeLineVisible(((Activity)context).findViewById(R.id.line258));
                    break;
                }
                case "147": {
                    makeLineVisible(((Activity)context).findViewById(R.id.line048));
                    break;
                }
                case "345": {
                    makeLineVisible(((Activity)context).findViewById(R.id.line246));
                    break;
                }
            }

            //The result will be shown
            new Handler().postDelayed(() -> showResult(player), 400);

        }

        //if match draws
        else if(matchStatus == -1){

            //if online, then save the result
            if(computerNo == 5){
                userInfoFolderEditor = userInfoFolder.edit();
                userInfoFolderEditor.putInt("tie", (1+userInfoFolder.getInt("tie",0)));
                userInfoFolderEditor.apply();
            }

            new Handler().postDelayed(() -> showResult(0), 300);
            showTurn(0);
        }

        //if match is going on
        else{
            changeTurn();

            progressBoard.setText("");

            if(player == 2){
                if(computerNo > 0 && computerNo != 5 ){

                    new Handler().postDelayed(() -> {
                        if(computerNo == 1) randomPlay();
                        else if(computerNo == 2) mediumBotPlay();
                        else impossibleBotPlay();
                        printWinner(checkWinner());
                    }, 500);
                }
            }

        }
    }

    private void showResult(int winner){

        hasGameEnded = true;// only for online game

        ((Activity)context).findViewById(R.id.resultBox).setVisibility(View.VISIBLE);
        ((Activity)context).findViewById(R.id.playAgainBtn).setVisibility(View.VISIBLE);
        ((Activity)context).findViewById(R.id.gameBox).setVisibility(View.GONE);
        ((Activity)context).findViewById(R.id.resultBoard).setVisibility(View.GONE);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        @ColorInt int textColor;

        ImageView resultAvatar = ((Activity)context).findViewById(R.id.imageView11);
        TextView resultTextView = ((Activity)context).findViewById(R.id.textView8);

        String text;

        if(winner == 0){
            resultAvatar.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.draw));
            theme.resolveAttribute(R.attr.primaryTextColor, typedValue, true);
            text = "It's a Tie!!";

        }
        else if(winner == 1){

            if(computerNo == 5){
                if(isCreator){
                    resultAvatar.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.winner));
                    text = "You won!!";
                }
                else{
                    resultAvatar.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.loser));
                    text = "You loose!!";
                }
            }
            else{
                resultAvatar.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.winner));
                text = p1Name + " won!!";
            }
            theme.resolveAttribute(R.attr.player1color, typedValue, true);

        }
        else{
            if(computerNo == 0) {
                resultAvatar.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.winner));
                text = p2Name + " won!!";
            }
            else if(computerNo == 5){
                if(!isCreator){
                    resultAvatar.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.winner));
                    text = "You won!!";
                }
                else{
                    resultAvatar.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.loser));
                    text = "You loose!!";
                }
            }
            else{
                resultAvatar.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.loser));
                text = "You Loose!!";
            }
            theme.resolveAttribute(R.attr.player2color, typedValue, true);
        }
        textColor = typedValue.data;
        resultTextView.setText(text);
        resultTextView.setTextColor(textColor);

    }

}
