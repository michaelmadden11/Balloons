// CannonView.java
// Displays and controls the Cannon Game
package edu.augustana.csc490.gamestarter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Timer;

public class MainGameView extends SurfaceView implements SurfaceHolder.Callback
{
    private static final String TAG = "Pop the Balloons!"; // for Log.w(TAG, ...)

    private GameThread gameThread; // runs the main game loop
    private Activity mainActivity; // keep a reference to the main Activity

    private boolean isGameOver = true;

    private int speed = 10;

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

    private List<Balloon> allBalloons;
    private int level;
    private Timer timer;
    int score = 0;
    int lives = 3;

    public MainGameView(Context context, AttributeSet atts)
    {
        super(context, atts);
        mainActivity = (Activity) context;
        getHolder().addCallback(this);

        TextView timerTextView;
        long startTime = 0;

        //runs without a timer by reposting this handler at the end of the runnable

        myPaint = new Paint();
        myPaint.setColor(Color.BLUE);
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.CYAN);
        hardPaint = new Paint();
        hardPaint.setColor(Color.RED);

        mediumPaint = new Paint();
        mediumPaint.setColor(Color.rgb(255,153,0));

        easyPaint = new Paint();
        easyPaint.setColor(Color.YELLOW);

        blackPaint = new Paint();
        blackPaint.setColor(Color.BLACK);

        allBalloons = new ArrayList<Balloon>();

    }

    // called when the size changes (and first time, when view is created)
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        screenWidth = w;
        screenHeight = h;

        startNewGame();
    }

    public void startNewGame()
    {
       // TextView t = (TextView) findViewById(R.id.UIScore);
       // t.setText("" + score);
        Random rand = new Random();
        for(int i = 0; i < 10; i++)
        {
            int xCoordinate = rand.nextInt(screenWidth);
            int balloonColorNumber;
            int randomBalloonColorNumber = rand.nextInt(10);
            int randomSpeedNumber = rand.nextInt(5);
            int yAddition = rand.nextInt(250);

            if(randomBalloonColorNumber < 6)
            {
                balloonColorNumber = 1;
            }
            else if(randomBalloonColorNumber < 9)
            {
                balloonColorNumber = 2;
            }
            else
            {
                balloonColorNumber = 3;
            }
            Balloon addABalloon = new Balloon(xCoordinate,screenHeight + 35 + yAddition,balloonColorNumber, randomSpeedNumber);
            allBalloons.add(addABalloon);
        }

        if (isGameOver)
        {
            isGameOver = false;
            gameThread = new GameThread(getHolder());
            gameThread.start(); // start the main game loop going
        }
    }


    private void gameStep()
    {
        if(lives <= 0)
        {
            stopGame();
        }
       for(int i = 0; i < allBalloons.size(); i++)
       {
           Balloon currentBalloon = allBalloons.get(i);
           if(currentBalloon.height <= 30 - radius)
           {
            lives -= 1;
            allBalloons.remove(i);
           }
           currentBalloon.height -= speed;
       }
       //speed++;
    }

    public void updateView(Canvas canvas)
    {
        if (canvas != null) {
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), backgroundPaint);
            for(int i = 0; i < allBalloons.size(); i++)
            {
                Balloon thisBalloon = allBalloons.get(i);
                canvas.drawCircle(thisBalloon.width, thisBalloon.height, radius, thisBalloon.balloonColor);
            }
        }
    }

    // stop the game; may be called by the MainGameFragment onPause
    public void stopGame()
    {
        if (gameThread != null)
            gameThread.setRunning(false);
    }

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
        if (e.getAction() == MotionEvent.ACTION_DOWN)
        {
            this.x = (int) e.getX();
            this.y = (int) e.getY();
        }

        for(int i = 0; i < allBalloons.size(); i++)
        {
            boolean balloonTouch = true;
            if(allBalloons.get(i) != null)
            {
                Balloon currentBalloon = allBalloons.get(i);
                if(!(x >= currentBalloon.width - radius && x <= currentBalloon.width + radius))
                {
                    balloonTouch = false;
                }
                if(!(y >= currentBalloon.height - radius && y <= currentBalloon.height + radius))
                {
                    balloonTouch = false;
                }

                if(balloonTouch)
                {
                    score++;
                    if(currentBalloon.health == 1)
                    {
                        allBalloons.remove(i);
                        score++;
                    }
                    else
                    {
                    updateBalloonColor(currentBalloon);
                    }
                    return true;
                }
            }
        }
        return true;
    }
    private void updateBalloonColor(Balloon thisBalloon)
    {
        thisBalloon.health -= 1;
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
    private class GameThread extends Thread
    {
        private SurfaceHolder surfaceHolder; // for manipulating canvas
        private boolean threadIsRunning = true; // running by default

        // initializes the surface holder
        public GameThread(SurfaceHolder holder)
        {
            surfaceHolder = holder;
            setName("GameThread");
        }

        // changes running state
        public void setRunning(boolean running)
        {
            threadIsRunning = running;
        }

        @Override
        public void run()
        {
            Canvas canvas = null;

            while (threadIsRunning)
            {
                try
                {
                    // get Canvas for exclusive drawing from this thread
                    canvas = surfaceHolder.lockCanvas(null);

                    // lock the surfaceHolder for drawing
                    synchronized(surfaceHolder)
                    {
                        gameStep();         // update game state
                        updateView(canvas); // draw using the canvas
                    }
                    //Thread.sleep(1); // if you want to slow down the action...
                //} catch (InterruptedException ex) {
                //    Log.e(TAG,ex.toString());
                }
                finally  // regardless if any errors happen...
                {
                    // make sure we unlock canvas so other threads can use it
                    if (canvas != null)
                        surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}