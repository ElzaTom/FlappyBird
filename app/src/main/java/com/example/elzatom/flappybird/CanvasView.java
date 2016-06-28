package com.example.elzatom.flappybird;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;


public class CanvasView extends SurfaceView implements Runnable,View.OnTouchListener {

    Thread thread = null;
    SurfaceHolder holder;
    boolean isItOk,gameWin,gameLoss,mTouch,mStart = false;
    Bitmap  canvasBackground , birdWingsUp , birdWingsDown ,gameEnd;
    int score,birdXPosition,birdYPosition,birdTransistionTime,screenHeight,screenWidth,rectLeftPosition,rectRightPosition,downRectHeight,upRectHeight,secRectLeftPosition,secRectRightPosition,secDownRectHeight,secUpRectHeight;
    Canvas canvas;
    float touchXPosition,touchYPosition;
    Activity context;
    static Paint greenPaint;
    static {
        greenPaint = new Paint();
        greenPaint.setColor(Color.parseColor("#90e475"));
        greenPaint.setStyle(Paint.Style.FILL);
    }


    public CanvasView(Context context, Activity currentActivity) {

        super(context);
        this.context =currentActivity;
        canvas = new Canvas();
        holder = getHolder();
        score=0;
        birdTransistionTime = 120;
        rectLeftPosition = 0;
        secRectLeftPosition = 0;
        canvasBackground = BitmapFactory.decodeResource(getResources(), R.mipmap.background);
        birdWingsUp = BitmapFactory.decodeResource(getResources(), R.drawable.birdup);
        birdWingsDown = BitmapFactory.decodeResource(getResources(), R.drawable.birddown);

        setOnTouchListener(this);
    }


    public void run() {

        while(isItOk) {

            if (!holder.getSurface().isValid()){
                continue;
            }
            canvas = holder.lockCanvas();
            screenHeight =canvas.getHeight();
            screenWidth = canvas.getWidth();
            birdXPosition = screenWidth/4;
            Rect dest = new Rect(0, 0,screenWidth, screenHeight);
            canvas.drawARGB(255,150,150,10);
            canvas.drawBitmap(canvasBackground, null, dest, null);

            if(!mStart){

                clickStart();
                birdYPosition = screenHeight/4;
                rectRightPosition = -screenWidth/7;
                secRectRightPosition = -screenWidth/7;
            }else {

                if (birdXPosition + birdWingsDown.getWidth() >= (screenWidth - (rectLeftPosition)) && birdXPosition <= (screenWidth - rectRightPosition)) {
                    if (birdYPosition <= upRectHeight || birdYPosition + birdWingsUp.getHeight() >= screenHeight - downRectHeight) {
                        isItOk = false;
                        gameLoss = true;
                    }
                }
                drawBird();
                drawRectangle();

                if (birdXPosition + birdWingsDown.getWidth() >= (screenWidth - (secRectLeftPosition)) && birdXPosition <= (screenWidth - secRectRightPosition)) {
                    if (birdYPosition <= secUpRectHeight || birdYPosition + birdWingsUp.getHeight() >= screenHeight - secDownRectHeight) {
                        isItOk = false;
                        gameLoss = true;
                    }
                }
                if ((screenWidth - rectLeftPosition) <= screenWidth / 2) {
                    drawSecondRectangle();
                }
                drawScore();
                if (birdYPosition < 0 || birdYPosition > screenHeight - birdWingsDown.getHeight() || secRectRightPosition == screenWidth) {
                    isItOk = false;
                    gameLoss = true;
                }
                if (secRectRightPosition == screenWidth) {
                    gameWin = true;
                }
                if (birdXPosition == (screenWidth - rectRightPosition) || birdXPosition == (screenWidth - secRectRightPosition) ){
                    score = score+1;

                }
            }
            holder.unlockCanvasAndPost(canvas);

            if (!isItOk){
                final Runnable run = new Runnable(){
                    public void run(){
                        if (gameWin){

                            showAlert("You Won");

                        }else if(gameLoss) {
                            showAlert("Game Over");
                        }

                    }
                };
                context.runOnUiThread(run);
            }
        }
    }

//draw the bird in canvas
    public void drawBird(){

        if (mTouch){
            birdYPosition= birdYPosition-3;
        }else{
            birdYPosition=birdYPosition+2;
        }

        if ( birdTransistionTime <= 150 && birdTransistionTime >= 135 ) {

            canvas.drawBitmap(birdWingsDown,birdXPosition,birdYPosition,new Paint());
            birdTransistionTime = birdTransistionTime+1;
        }else {
            if (birdTransistionTime == 151) {
                birdTransistionTime=120;
            }
            canvas.drawBitmap(birdWingsUp,birdXPosition,birdYPosition,new Paint());

            birdTransistionTime= birdTransistionTime+1;
        }


    }


    public void clickStart (){


        mStart= true;


    }

//draw the first rectangle in canvas
    public void drawRectangle(){

        Rect downRect = new Rect();
        downRect.set(screenWidth - (rectLeftPosition), screenHeight/3, screenWidth - rectRightPosition, screenHeight);
        Rect upRectangle = new Rect();
        upRectangle.set(screenWidth - (rectLeftPosition), 0, screenWidth - rectRightPosition, (screenHeight)/6 );
        rectLeftPosition = (rectLeftPosition) + 1;
        rectRightPosition = rectRightPosition + 1;
        upRectHeight = upRectangle.height();
        downRectHeight = downRect.height();
        canvas.drawRect(downRect, greenPaint);
        canvas.drawRect(upRectangle, greenPaint);
    }
//draw the second rectangle in canvas
    public void drawSecondRectangle(){

        Rect secDownRect = new Rect();
        secDownRect.set(screenWidth - secRectLeftPosition, screenHeight/2, screenWidth - secRectRightPosition, screenHeight);
        Rect secUpRectangle = new Rect();
        secUpRectangle.set(screenWidth-secRectLeftPosition,0,screenWidth-secRectRightPosition,screenHeight/3);
        secRectLeftPosition = secRectLeftPosition + 1;
        secRectRightPosition = secRectRightPosition + 1;
        secUpRectHeight = secUpRectangle.height();
        secDownRectHeight = secDownRect.height();
        canvas.drawRect(secDownRect, greenPaint);
        canvas.drawRect(secUpRectangle, greenPaint);
    }
//draw the score on canvas
    public void drawScore() {

        Paint red = new Paint();
        red.setColor(Color.RED);
        red.setTextSize(30);
        canvas.drawText("Score " + score, canvas.getWidth()/6, 50, red);
    }

    public void pause(){

        isItOk = false;
        gameWin = false;
        gameLoss = false;
        mStart = false;
        while(true){
            try {
                thread.join();
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
            break;
        }
        thread = null;
    }

    public void resume(){
        gameWin = false;
        gameLoss = false;
        mStart = false;
        isItOk = true;
        birdTransistionTime = 120;
        rectLeftPosition = 0;
        secRectLeftPosition = 0;
        score=0;
        thread = new Thread(this);
        thread.start();

    }
    //calls when the the game want to replay
    public void restartGame(){
        gameWin = false;
        birdTransistionTime = 120;
        rectLeftPosition = 0;
        secRectLeftPosition = 0;
        score=0;
        isItOk = true;
        mStart = false;
        thread = new Thread(this);
        thread.start();
    }
// Showing the alert when game won and game over
    public void showAlert( String message){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setIcon(R.drawable.wings_up);
        alertDialogBuilder.setTitle(message);
        alertDialogBuilder.setMessage("Replay");
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                restartGame();
                dialog.cancel();
            }
        });
        // set negative button: No message
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                context.finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        logTouchType(event);
        touchXPosition = event.getX();
        touchYPosition = event.getY();
        return true;
    }

    private void logTouchType(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouch = true;
                break;

            case MotionEvent.ACTION_UP:
                mTouch = false;
        }
    }

}