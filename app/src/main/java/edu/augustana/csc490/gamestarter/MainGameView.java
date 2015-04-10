// CannonView.java
// Displays and controls the Cannon Game
package edu.augustana.csc490.gamestarter;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;


public class MainGameView extends SurfaceView implements SurfaceHolder.Callback
{
    private static final String TAG = "Pop the Balloons!"; // for Log.w(TAG, ...)
    private GameThread gameThread; // runs the main game loop
    private Activity mainActivity; // keep a reference to the main Activity
    private TextView UIScore;
    private TextView UILives;
    Timer timer;
    MyTimerTask myTimerTask;

    private boolean isGameOver = true;

    private int speed = 2;

    private int x;
    private int y;
    private int radius = 40;
    private int screenWidth;
    private int screenHeight;

    private Paint myPaint;
    private Paint backgroundPaint;
    private Paint hardPaint;
    private Paint mediumPaint;
    private Paint easyPaint;
    private Paint blackPaint;

    private Bitmap backgroundBitMap;
    private Bitmap skullBitMap;

    private List<Balloon> allBalloons;
    private int level;
    int score;
    int lives;
    int seconds;
    boolean addBalloons = true;
    boolean gameIsRunning = true;
    boolean newGame = true;


    public MainGameView(Context context, AttributeSet atts)
    {
        super(context, atts);
        mainActivity = (Activity) context;
        getHolder().addCallback(this);

        myPaint = new Paint();
        myPaint.setColor(Color.BLUE);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.rgb(135,206,235));
        hardPaint = new Paint();
        hardPaint.setColor(Color.RED);

        mediumPaint = new Paint();
        mediumPaint.setColor(Color.rgb(255,153,0));

        easyPaint = new Paint();
        easyPaint.setColor(Color.YELLOW);

        blackPaint = new Paint();
        blackPaint.setColor(Color.BLACK);

        backgroundBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.sky);
        backgroundBitMap = backgroundBitMap.createScaledBitmap(backgroundBitMap,675, 1000, true);

        skullBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.skull);
        skullBitMap = skullBitMap.createScaledBitmap(skullBitMap,radius * 2 + 2, radius * 2 + 2, true);

        timer = new Timer();
        myTimerTask = new MyTimerTask();
        timer.scheduleAtFixedRate(myTimerTask, 0, 1000);

    }

    // called when the size changes (and first time, when view is created)
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        screenWidth = w;
        screenHeight = h;
        if(newGame)
        {
            showGameDialog("New Game", "Welcome to Pop the Balloons! Click play to begin.", "Play");
            newGame = false;
        }
    }

    public void startNewGame()
    {
        allBalloons = new ArrayList<Balloon>();
        score = 0;
        lives = 3;
        level = 0;
        seconds = 0;
        UIScore = (TextView) mainActivity.findViewById(R.id.UIScore);
        UIScore.setText("" + score);

        UILives = (TextView) mainActivity.findViewById(R.id.UILives);
        UILives.setText("Lives:  " + lives);

        isGameOver = true;


        if (isGameOver)
        {
            isGameOver = false;
            gameThread = new GameThread(getHolder());
            gameThread.start(); // start the main game loop going
        }
    }

    private void AddBalloons(int numberOfBalloons)
    {
        Random rand = new Random();
        for(int i = 0; i < numberOfBalloons; i++)
        {
            int xCoordinate = rand.nextInt(screenWidth);
            int balloonColorNumber;
            int randomBalloonColorNumber = rand.nextInt(21);
            int randomSpeedNumber = rand.nextInt(5);
            int yAddition = rand.nextInt(250);

            if(randomBalloonColorNumber <= 9)
            {
                balloonColorNumber = 1;
            }
            else if(randomBalloonColorNumber <= 15)
            {
                balloonColorNumber = 2;
            }
            else if(randomBalloonColorNumber <= 19)
            {
                balloonColorNumber = 3;
            }
            else
            {
                balloonColorNumber = 5;
            }
            Balloon addABalloon = new Balloon(xCoordinate,screenHeight + 35 + yAddition,balloonColorNumber, randomSpeedNumber);
            allBalloons.add(addABalloon);
        }
    }


    private void gameStep()
    {
        mainActivity.runOnUiThread(
                new Runnable() {
                    public void run() {
                        do{
                            UILives = (TextView) mainActivity.findViewById(R.id.UILives);
                            UILives.setText("Lives:  " + lives);
                        }
                        while(gameIsRunning);

                    }
                }
                // end Runnable
        ); // end call to runOnUiThread
        if(lives <= 0)
        {
            stopGame();
        }
       for(int i = 0; i < allBalloons.size(); i++)
       {
           Balloon currentBalloon = allBalloons.get(i);
           if(currentBalloon.height <= 30 - radius && currentBalloon.health != 5)
           {
            lives -= 1;
            allBalloons.remove(i);
           }
           currentBalloon.height -= speed;
       }
        if(addBalloons){
            AddBalloons(level);
            addBalloons = false;
        }
       //speed++;
    }

    public void updateView(Canvas canvas)
    {
        if (canvas != null) {
            canvas.drawBitmap(backgroundBitMap, 0, 0, backgroundPaint);
            for(int i = 0; i < allBalloons.size(); i++)
            {
                Balloon thisBalloon = allBalloons.get(i);
                canvas.drawCircle(thisBalloon.width, thisBalloon.height, radius + 2, blackPaint);
                canvas.drawCircle(thisBalloon.width, thisBalloon.height, radius, thisBalloon.balloonColor);
                if(thisBalloon.health == 5)
                {
                    canvas.drawBitmap(skullBitMap, thisBalloon.width - radius, thisBalloon.height - radius, backgroundPaint);
                }
                //canvas.drawLine(thisBalloon.width, thisBalloon.height + radius, thisBalloon.width, thisBalloon.height + 200, blackPaint);
            }
        }
    }

    // stop the game; may be called by the MainGameFragment onPause
    public void stopGame()
    {
        if (gameThread != null){
            gameThread.setRunning(false);
        }
        // highScore = getResources().getInteger(R.integer.high_score);
        //if(score > highScore){
            //highScore = score;
        //}
        showGameDialog("Game Over", "You popped " + score + " balloons! Would you like to play again?", "Reset");
    }

    //Code help from Reed Kottke for showGameOverDialog
    public void showGameDialog(final String title, final String message, final String positiveButton) {

        final DialogFragment gameResult =
                new DialogFragment() {
                    // create an AlertDialog and return it
                    @Override
                    public Dialog onCreateDialog(Bundle bundle) {
                        // create dialog displaying String resource for messageId
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(title);
                        builder.setMessage(message);
                        builder.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startNewGame();
                            }
                        });
                        //builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        //    @Override
                        //    public void onClick(DialogInterface dialog, int which) {

                        //    }
                       // });
                        return builder.create(); // return the AlertDialog
                    } // end method onCreateDialog
                }; // end DialogFragment anonymous inner class
        // in GUI thread, use FragmentManager to display the DialogFragment
        mainActivity.runOnUiThread(
                new Runnable() {
                    public void run() {
                        gameResult.setCancelable(false); // modal dialog
                        gameResult.show(mainActivity.getFragmentManager(), "results");
                        gameIsRunning = false;
                    }
                }
                // end Runnable
        ); // end call to runOnUiThread
    } // end method showGameOverDialog

    // release resources; may be called by MainGameFragment onDestroy
    public void releaseResources()
    {

        // release any resources (e.g. SoundPool stuff)
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    // called when the surface is destroyed
    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        // ensure that thread terminates properly
        boolean retry = true;
        gameThread.setRunning(false); // terminate gameThread

        while (retry)
        {
            try
            {
                gameThread.join(); // wait for gameThread to finish
                retry = false;
            }
            catch (InterruptedException e)
            {
                Log.e(TAG, "Thread interrupted", e);
            }
        }
    }

        @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            this.x = (int) e.getX();
            this.y = (int) e.getY();


            for (int i = allBalloons.size() - 1; i >= 0; i--) {
                boolean balloonTouch = true;
                if(allBalloons.get(i) != null)
                {
                    Balloon currentBalloon = allBalloons.get(i);
                    if (!(x >= currentBalloon.width - radius && x <= currentBalloon.width + radius)) {
                        balloonTouch = false;
                    }
                    if (!(y >= currentBalloon.height - radius && y <= currentBalloon.height + radius)) {
                        balloonTouch = false;
                    }

                    if (balloonTouch) {
                        if (currentBalloon.health == 1) {
                            allBalloons.remove(currentBalloon);
                            score += 1;
                            UIScore.setText("" + score);
                        }
                        else if(currentBalloon.health == 5)
                        {
                            lives -= 1;
                            allBalloons.remove(currentBalloon);
                        }
                        else {
                            updateBalloonColor(currentBalloon);
                        }
                        balloonTouch = false;
                        return true;
                    }
                }
            }
        }
        return true;
    }
    private void updateBalloonColor(Balloon thisBalloon)
    {
        thisBalloon.health = thisBalloon.health - 1;
        if(thisBalloon.health == 1)
        {
            thisBalloon.balloonColor = easyPaint;
        }
        else if(thisBalloon.health == 2)
        {
            thisBalloon.balloonColor = mediumPaint;
        }
        else if(thisBalloon.health == 3)
        {
            thisBalloon.balloonColor = hardPaint;
        }
    }
    // Thread subclass to run the main game loop
    private class GameThread extends Thread {
        private SurfaceHolder surfaceHolder; // for manipulating canvas
        private boolean threadIsRunning = true; // running by default

        // initializes the surface holder
        public GameThread(SurfaceHolder holder) {
            surfaceHolder = holder;
            setName("GameThread");
        }

        // changes running state
        public void setRunning(boolean running) {
            threadIsRunning = running;
        }

        @Override
        public void run() {
            Canvas canvas = null;

            while (threadIsRunning) {
                try {
                    // get Canvas for exclusive drawing from this thread
                    canvas = surfaceHolder.lockCanvas(null);
                    // lock the surfaceHolder for drawing
                    synchronized (surfaceHolder) {
                        gameStep();         // update game state
                        updateView(canvas); // draw using the canvas
                    }
                    Thread.sleep(1); // if you want to slow down the action...
                } catch (InterruptedException ex) {
                    Log.e(TAG, ex.toString());
                } finally  // regardless if any errors happen...
                {
                    // make sure we unlock canvas so other threads can use it
                    if (canvas != null)
                        surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            seconds += 1;
            level = seconds / 10;
            if(level < 1){
                level = 1;
            }
            addBalloons = true;
        }

    }
}