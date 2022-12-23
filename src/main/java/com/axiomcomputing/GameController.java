package com.axiomcomputing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JButton;

/**
 * @author Christopher Caldwell
 *  For CST-338 Final
 *  Date: 8/7/2018
 *
 */
public class GameController implements ActionListener
{ 
   GameModel model;
   MainWindow view;
   
   private boolean isGameOver;
   public boolean getIsGameOver()
   {
      return isGameOver;
   }
   
   public GameController(GameModel model, MainWindow view)
   {
      this.model = model;
      this.view = view;
      
      init();  
      newGame();
   }
   
   private void init()
   {
      // Add event listeners
      bindKeyBoardActions();  
      
      //Test buttons
      JButton btn = view.getButton(1);
      btn.addActionListener(this);  
      
      btn = view.getButton(2);
      btn.addActionListener(this);  
   }
      
   // Informs the model and the view that a new game is to begin.
   private void newGame()
   {
      isGameOver = false;
      model.newGame();
      synchronize();
      
      view.setGameOver(false, false);
      view.repaint();  
        
   }
   
   // Sets the BoardView object with a copy of the most current GameModel
   // data.
   private void synchronize()
   {
      //view.setBoardStateData(model.getBoardState());
      view.setBoardTransitionStates(model.getLastTransitions());
      view.setPlayerScore(model.getPlayerScore());
      view.setHighScore(model.getHighScore());
   }
   
   // ***************** EVENTS *************************
   //
   //
   // **************************************************
   
   // Event to fire when buttons in the MainWindow are clicked.
   @Override
   public void actionPerformed(ActionEvent e)
   {
      JButton button = (JButton)e.getSource();
      String name = button.getName();
      
      switch(name)     
      {
         case "1": // NewGame Button
            newGame();
            break;
         case "2": // Progress Animation Frame
            System.exit(0);
            break;
      }
   }
   
   // Method that binds Abstract Action methods to inputMaps
   // in the BoardView. These methods are for responding to keyboard input.
   private void bindKeyBoardActions()
   {
      ActionMap actionMap = view.getGameBoardActionMap();
          
      // When the Up key is pressed
      AbstractAction upAction = new AbstractAction()
      {
         private static final long serialVersionUID = 1L;
         public void actionPerformed(ActionEvent e)
         {
            if(view.getIsTransitioning())
               return;
            onKeyPress(KeyEvent.VK_UP);
         }
      };
      
      // When the Down key is pressed
      AbstractAction downAction = new AbstractAction()
      {
         private static final long serialVersionUID = 1L;
         public void actionPerformed(ActionEvent e)
         {
            if(view.getIsTransitioning())
               return;
            onKeyPress(KeyEvent.VK_DOWN);
         }
      };
      
      // When the Left key is pressed
      AbstractAction leftAction = new AbstractAction()
      {
         private static final long serialVersionUID = 1L;
         public void actionPerformed(ActionEvent e)
         {
            if(view.getIsTransitioning())
               return;
            onKeyPress(KeyEvent.VK_LEFT);
         }
      };
      
      // When the Left key is pressed
      AbstractAction rightAction = new AbstractAction()
      {
         private static final long serialVersionUID = 1L;
         public void actionPerformed(ActionEvent e)
         {
            if(view.getIsTransitioning())
               return;
            onKeyPress(KeyEvent.VK_RIGHT);
         }
      };
      
      actionMap.put("onUp", upAction);
      actionMap.put("onDown", downAction);
      actionMap.put("onLeft", leftAction);
      actionMap.put("onRight", rightAction);
   }
   
   // The method called when the GameController responds to valid
   // key presses. 
   private void onKeyPress(int keyCode)
   {
      // Do not allow moves during transitions.
      if (view.getIsTransitioning())
         return;
      if (isGameOver)
         return;
            
      boolean didMove = false;
      
      System.out.println(keyCode);
               
      switch(keyCode)
      {
         case KeyEvent.VK_UP:
            didMove = model.move(Direction.Up);
            break;
         case KeyEvent.VK_DOWN:
            didMove = model.move(Direction.Down);
            break;  
         case KeyEvent.VK_LEFT:
            didMove = model.move(Direction.Left);
            break;
         case KeyEvent.VK_RIGHT:
            didMove = model.move(Direction.Right);
            break;
      }
      
      // Game over immediate.
      if (!model.isMovePossible())
      {
         isGameOver = true;
         view.setGameOver(true, false);         
      }
      else if (model.getDidWin())
      {
         isGameOver = true;
         view.setGameOver(true, true); 
      }
       
      if (didMove)
      {      
         synchronize();
                   
      }
   }
}
