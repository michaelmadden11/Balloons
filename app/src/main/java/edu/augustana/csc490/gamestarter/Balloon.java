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
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by michaelmadden11 on 4/1/2015.
 */
public class Balloon
{
    public Paint balloonColor;
    public int health;
    public int width;
    public int height;
    public int speed;

    private Paint hardPaint;
    private Paint mediumPaint;
    private Paint easyPaint;
    private Paint blackPaint;

    public Balloon(int x, int y, int health, int speed)
    {
        hardPaint = new Paint();
        hardPaint.setColor(Color.RED);

        mediumPaint = new Paint();
        mediumPaint.setColor(Color.rgb(255,153,0));

        easyPaint = new Paint();
        easyPaint.setColor(Color.YELLOW);

        blackPaint = new Paint();
        blackPaint.setColor(Color.BLACK);

        width = x;
        height = y;
        this.health = health;
        this.speed = speed;

        if(health == 1)
        {
            balloonColor = easyPaint;
        }
        else if(health == 2)
        {
            balloonColor = mediumPaint;
        }
        else if(health == 3)
        {
            balloonColor = hardPaint;
        }
        else
        {
            balloonColor = blackPaint;
        }
    }

    public int getHealth()
    {
        return health;
    }
    public Paint getBalloonColor()
    {
        return balloonColor;
    }

}
