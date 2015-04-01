// GameStarterFragment.java
// MainGameFragment creates and manages a CannonView
package edu.augustana.csc490.gamestarter;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainGameFragment extends Fragment
{
   private MainGameView mainGameView; // custom view

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
   {
      super.onCreateView(inflater, container, savedInstanceState);    
      View view = 
         inflater.inflate(R.layout.fragment_game, container, false);

      mainGameView = (MainGameView) view.findViewById(R.id.mainGameView);
      return view;
   }

   @Override
   public void onActivityCreated(Bundle savedInstanceState)
   {
      super.onActivityCreated(savedInstanceState);
   }

   // when paused, MainGameFragment stops the game
   @Override
   public void onPause()
   {
      super.onPause(); 
      mainGameView.stopGame();
   } 
   
   // when MainActivity is over, releases game resources
   @Override
   public void onDestroy()
   {
      super.onDestroy();
      mainGameView.releaseResources();
   }
}