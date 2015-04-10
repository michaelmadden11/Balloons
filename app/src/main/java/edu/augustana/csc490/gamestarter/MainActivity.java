// GameStarter.java
// MainActivity displays the MainGameFragment
package edu.augustana.csc490.gamestarter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

public class MainActivity extends Activity
{
    public static boolean gameOver = false;
   @Override
   public void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
   }
   public void showDialog(){
       AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
       TextView myMessage = new TextView(this);
       myMessage.setText("Woot!");
       myMessage.setGravity(Gravity.CENTER_HORIZONTAL);
       dlgAlert.setView(myMessage);
       dlgAlert.setPositiveButton("OK", null);
       dlgAlert.setCancelable(true);
       dlgAlert.create().show();
   }

}

