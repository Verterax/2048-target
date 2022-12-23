/**
 * 
 */
package com.axiomcomputing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.EventListener;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


/**
 * @author Christopher Caldwell
 *  For CST-338 Final
 *  Date: 8/7/2018
 *
 */
public class MainWindow extends JFrame implements EventListener
{
   BoardView boardView;

   Font font;
   JPanel infoTop;
   JPanel infoBottom;
   JPanel pnlScores;
   JPanel pnlButton;
   
   JLabel playerScore;
   JLabel highScore;
        
   JButton newGameButton;
   JButton exitButton;
   
   int prefferedWidth = 550;

   public static void main(String[] args)
   {
      // Create main window.
      GameModel model = new GameModel();
      MainWindow view = new MainWindow();
      
      @SuppressWarnings("unused")
      GameController controller = new GameController(model, view);
                    
      view.setVisible(true);      
   }
    
   public MainWindow()
   {     
      init();            
   }
    
   // Sets up the layout and UI of the MainWindow.
   private void init()
   {
      font = FontFetcher.getFont();
      Color green = Color.decode("#4caf4f");
      Color red = Color.decode("#ce333b");
      Color blue = Color.decode("#3e51b5");
      
      setVisible(false);
      setBackground(Color.WHITE);
      setSize(700, 900);
      setTitle("2048! Implemented By Christopher Caldwell");
      setResizable(true);
      setLocationRelativeTo(null);
      setLayout(new FlowLayout());
      this.getContentPane().setBackground(Color.LIGHT_GRAY);;
      this.setBackground(Color.LIGHT_GRAY);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
      this.repaint();
           
      // Header Panel
      infoTop = new JPanel();
      infoTop.setLayout(new GridLayout(2,2));
      infoTop.setPreferredSize(new Dimension(prefferedWidth, 120));
      infoTop.setBackground(Color.LIGHT_GRAY);
      
      // 2048! Label
      JLabel bigLabel = new JLabel("   2048");      
      Font bigFont  = font.deriveFont(Font.BOLD, 72);
      bigLabel.setFont(bigFont);
      bigLabel.setForeground(Color.DARK_GRAY);
      infoTop.add(bigLabel);
      
      // Scores
      pnlScores = new JPanel();
      pnlScores.setLayout(new GridLayout(1,2));
      pnlScores.setPreferredSize(new Dimension(450, 50));
      pnlScores.setBackground(Color.LIGHT_GRAY);
      
      playerScore = new JLabel("Score: \n0", SwingConstants.CENTER);
      playerScore.setFont(font.deriveFont(Font.BOLD, 16));
      playerScore.setBackground(Color.WHITE);
      playerScore.setForeground(blue);
      
      highScore = new JLabel("High: \n0", SwingConstants.CENTER);
      highScore.setFont(font.deriveFont(Font.BOLD, 16));
      highScore.setBackground(Color.WHITE);
      highScore.setForeground(blue);
      
      pnlScores.add(highScore);
      pnlScores.add(playerScore);  
      infoTop.add(pnlScores);
      
      // Button Panels / Buttons
      pnlButton = new JPanel();
      pnlButton.setLayout(new GridLayout(1,2));
      pnlButton.setBackground(Color.WHITE);
      pnlButton.setBorder(BorderFactory.createMatteBorder(
                  5, 5, 5, 5, Color.DARK_GRAY));
          
      newGameButton = new JButton("New Game");
      newGameButton.setFocusable(false);
      newGameButton.setBackground(green);
      newGameButton.setName("1");
      pnlButton.add(newGameButton);
      exitButton = new JButton("Quit");
      exitButton.setFocusable(false);
      exitButton.setBackground(red);
      exitButton.setName("2");
      pnlButton.add(exitButton);
      
      JLabel lblHints = new JLabel("     Use the arrow keys to play.");
      lblHints.setFont(font.deriveFont(Font.BOLD, 16));
      lblHints.setForeground(blue);
      infoTop.add(lblHints);
      
      infoTop.add(pnlButton);
      
      JPanel topPadding = new JPanel();
      topPadding.setBackground(Color.LIGHT_GRAY);
      topPadding.setPreferredSize(new Dimension(prefferedWidth, 30));
      this.add(topPadding);
      
      this.add(infoTop);
      
      JPanel morePadding = new JPanel();
      morePadding.setBackground(Color.LIGHT_GRAY);
      morePadding.setPreferredSize(new Dimension(prefferedWidth, 20));
      this.add(morePadding);
      
      boardView = new BoardView();
      this.add(boardView);
      
      infoBottom = new JPanel();
      infoBottom.setLayout(new FlowLayout());
      infoBottom.setPreferredSize(new Dimension(prefferedWidth, 50));
      infoBottom.setBackground(Color.LIGHT_GRAY);
      
      JLabel lblHint2 = new JLabel("Try to create the 2048 tile!",
                   SwingConstants.CENTER);
      lblHint2.setFont(font.deriveFont(Font.BOLD, 20));
      lblHint2.setForeground(blue);
      infoBottom.add(lblHint2);
      this.add(infoBottom);
      
      boardView.requestFocus();
   }
   
   // Accessor for the GameController to subscribe to keyboard events in
   // the BoardView.
   public ActionMap getGameBoardActionMap()
   {
      return boardView.getActionMap();
   }
   
   // Mutator for GameController to set the transition states of the
   // BoardView with the transition data from the GameModel.
   public boolean setBoardTransitionStates(ArrayList<Tile[][]> boardTransitionStates)
   {
      if (boardTransitionStates == null)
         return false;
      boardView.setTransitions(boardTransitionStates);
      return true;
   }
   
   // Accessor for the GameController to subscribe to button click events.
   public JButton getButton(int buttonN)
   {
      switch(buttonN)
      {
         case 1:
            return newGameButton;
         case 2:
            return exitButton;
         default:
            return null;
      }
   }

   // Accessor for the GameController to learn if the view is
   // currently animating a transition.
   public boolean getIsTransitioning()
   {
      return boardView.getIsTransitioning();
   }

   // Mutator for player score.
   public boolean setPlayerScore(int score)
   {
      // TODO Auto-generated method stub
      this.playerScore.setText("Score: \n" + score);
      return true;
   }
   
   // Mutator for high score.
   public boolean setHighScore(int score)
   {
      this.highScore.setText("High: \n" + score);
      return true;
   }

   // Call up to boardView with game over status.
   public boolean setGameOver(boolean isGameOver, boolean didWin)
   {
      boardView.setGameOver(isGameOver, didWin);
      return true;
      
   }
   
  

}
