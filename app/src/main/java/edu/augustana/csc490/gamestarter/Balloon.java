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

    public Balloon(int x, int y, int health, Paint balloonColor)
    {
        width = x;
        height = y;
        this.health = health;
        this.balloonColor = balloonColor;
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
