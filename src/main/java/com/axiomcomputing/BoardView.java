package com.axiomcomputing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.Timer;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 * @author Christopher Caldwell
 *  For CST-338 Final
 *  Date: 8/7/2018
 *
 */

// BoardView class is responsible for displaying the state of the GameModel
// object, as well as animating the transition between intermediate game
// states.

@SuppressWarnings("serial")
public class BoardView extends JPanel implements Runnable, ActionListener
{
   private static final int CELL_DIM = 4;
   private static final int LINE_SIZE = 18; // Needs to be evenly divisible.
   private static final int BOARD_SIZE = 602;
   private static final int TILE_SIZE = (BOARD_SIZE - LINE_SIZE * 5) / CELL_DIM;
   private static final int OFFSET = LINE_SIZE / 2;
   private static final int DELAY = 25;
   private static final int TRANSITION_TIME = 5;
   
   private boolean isGameOver;
   private boolean didWin;
   public boolean setGameOver(boolean isGameOver, boolean didWin)
   {
      this.didWin = didWin;
      this.isGameOver = isGameOver;
      return true;
   }
   
   private Thread drawThread;
   private Timer timer;
   private int animSpeed = 100;
   private int frameCount = 0;
   private double pixelsPerFrame;
     
   private Color gridColor;
   private Color bgColor;
   private int size;
   
   private ArrayList<Tile[][]> transitions;
   public boolean setTransitions(ArrayList<Tile[][]> transitionStates)
   {
      if (transitionStates == null)
         return false;
      this.transitions = transitionStates;
      isTransitioning = false;
      frameCount = 0;
      setTransitionId(0);
      return true;
   }
     
   private int transitionId = 0;
   private int getTransitionId() {return transitionId;}
   private boolean setTransitionId(int id) 
   {
      if (id >= transitions.size())
         return false;
      
      this.transitionId = id;    
      return true;
   }
   
   private boolean isTransitioning = false;
   public boolean getIsTransitioning()
   {
      return isTransitioning;
   }
   private Image[] tileGraphics;
   Font tileFont;
  
         
   public BoardView()
   {
      size = BOARD_SIZE;     
      this.setPreferredSize(new Dimension(size, size));
      gridColor = Color.DARK_GRAY;
      bgColor = Color.LIGHT_GRAY;    
      init();
   }
   
   // Sets up a new game board.
   private void init()
   {
      cacheTileGraphics();
      createKeyboardBindingHandles();
      
      pixelsPerFrame = getPixelsPerFrame();
      
      drawThread = new Thread((Runnable)this);
      drawThread.start();
      
      timer = new Timer(DELAY, this);
      timer.start();      
   }
   
      
   // ***************** KEYBOARD EVENTS ***************
   //
   //
   // **************************************************
   
   // Create the inputMap keyboard event handles for other classes
   // such as the GameController to subscribe to.
   private void createKeyboardBindingHandles()
   {
      InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
      
      inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "onUp");
      inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "onDown");
      inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "onLeft");
      inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "onRight");
      
      inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), "onUp");
      inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "onDown");
      inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), "onLeft");
      inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "onRight");               
   }
      
   
   // ***************** GRAPHICS HANDLING **************
   //
   //
   // **************************************************   
   
   // Preprocess and store the images for the game's tile graphics.
   private boolean cacheTileGraphics()
   {
      Color textColor = Color.WHITE;
      Font font = FontFetcher.getFont();
      tileFont = font.deriveFont(Font.TRUETYPE_FONT, 36f);
      GraphicsEnvironment ge = GraphicsEnvironment
                  .getLocalGraphicsEnvironment();
      ge.registerFont(font);
           
      String[] colorCodes = new String[] 
                  {"#3e51b5",
                   "#4caf4f",
                   "#6a92cd",
                   "#914a6a",
                   "#839098",
                   "#989550",
                   "#a4d27a",
                   "#c297c5",
                   "#b111c5",
                   "#ce333b",
                   "#e71e62",
                   "#fac140" };    
      tileGraphics = new Image[colorCodes.length];
      
      int value = 2;
      Rectangle rect = new Rectangle(TILE_SIZE, TILE_SIZE);
      for (int index = 0; index < colorCodes.length; index++)
      {
         BufferedImage tileImg = new BufferedImage(TILE_SIZE + 1, TILE_SIZE + 1, 
                     BufferedImage.TYPE_INT_RGB);
         Graphics2D g = tileImg.createGraphics();
         Color color = Color.decode(colorCodes[index]);
         
         // Fill Tile color
         g.setColor(color);
         g.fillRect(0, 0, TILE_SIZE + 1, TILE_SIZE + 1);
         
         // Draw Value on Tile
         g.setFont(tileFont);
         g.setColor(textColor);
         
         drawStringCentered(String.valueOf(value), g, tileFont, rect);
         
         tileGraphics[index] = tileImg;
         
         // Double value of value. 2, 4, 8, 16, 32....
         value *= 2;
      }
      
      return true;
   }
   
   // Draws a string centered within a given area on a graphics object
   // using a given font.
   private void drawStringCentered(String str, Graphics2D g, 
               Font font, Rectangle rect)
   {
      FontMetrics metrics = g.getFontMetrics(font);
      int x = rect.x + (rect.width - metrics.stringWidth(str)) / 2;
      int y = rect.y + ((rect.height - metrics.getHeight()) / 2) +
                  metrics.getAscent();
      g.drawString(str, x, y);      
   }
   
   // Draws the associated win/lose message onto the game board area.
   public void gameOver(Graphics2D g)
   {  
      Font font = tileFont.deriveFont(Font.BOLD, 96);
      g.setFont(font);
      Color green = Color.decode("#4caf4f");
      Color red = Color.decode("#ce333b");
      Rectangle rect = new Rectangle(this.getWidth(), this.getHeight() - 150);
      
      if (didWin)
      {
         String str = "Win! 2048!!";
         g.setColor(Color.BLACK);
         drawStringCentered(str, g,  font, rect);
         rect = new Rectangle(rect.width + 8, rect.height + 8);
         g.setColor(green);
         drawStringCentered(str, g,  font, rect);    
      }
      else
      {
         String str = "Game Over!";
         g.setColor(Color.BLACK);
         drawStringCentered(str, g,  font, rect);
         rect = new Rectangle(rect.width + 8, rect.height + 8);
         g.setColor(red);
         drawStringCentered(str, g, font, rect);
      }
   }
   
   // Called repeatedly in a separate Thread to give the illusion of
   // tiles transiting smoothly across the game board.
   @Override public void paintComponent(Graphics g) 
   { 
      super.paintComponent(g); 
      Graphics2D g2 = (Graphics2D)g;
      drawGrid(g2);
        
      if (isGameOver)
      {
         animateTransition(g2); 
         gameOver(g2);         
      }
      else
      {
         animateTransition(g2); 
      }
                  
      try
      {
         Thread.sleep(1000 / animSpeed);
      }
      catch (InterruptedException e)
      {
         e.printStackTrace();
      }
      
      Toolkit.getDefaultToolkit().sync();
      repaint();
   }
   
   // Handles when the transition state should change,
   // Given how many frames have elapsed since the transition began.
   private void animateTransition(Graphics2D g2)
   {
      frameCount++;
      if (frameCount >= TRANSITION_TIME)
      {
         frameCount = 0;
         setTransitionId(getTransitionId() + 1);
      }
                                 
      drawCurrentBoardState(g2);
   }
   
   // Helper function to calculate how many pixels tiles need to transit
   // per ms.
   private double getPixelsPerFrame()
   {
      double distanceToTravel = (TILE_SIZE + LINE_SIZE);
      return distanceToTravel / TRANSITION_TIME;
   }
   
   // Helper function to return how far a tile should be
   // drawn from its origin given the direction it is traveling.
   private int getXTimeOffset(Tile tile, int frameId)
   {
      switch(tile.getDirection())
      {
         case Left:
            return (int)((-frameId) * pixelsPerFrame);
         case Right:
            return (int)(frameId * pixelsPerFrame);
         default:
            return 0;
      }          
   }
   
   // Helper function to return how far a tile should be
   // drawn from its origin given the direction it is traveling.
   private int getYTimeOffset(Tile tile, int frameId)
   {
      switch(tile.getDirection())
      {
         case Up:
            return (int)((-frameId) * pixelsPerFrame);
         case Down:
            return (int)(frameId * pixelsPerFrame);
         default:
            return 0;
      }   
   }
   
   // Draws the tiles onto the graphics object using the data stored
   // in the transitions ArrayList of Tile[][].
   private void drawCurrentBoardState(Graphics2D g2)
   {
      if (g2 == null)
         return;
      
      Tile[][] boardState = transitions.get(transitionId); 
      for (int row = 0; row < CELL_DIM; row++)
         for(int col = 0; col < CELL_DIM; col++)
         {
            Tile tile = boardState[row][col];
            if (tile == null)
               continue;
            
            int tileIndex = getIndexForValue(tile.getValue());
            Image img = tileGraphics[tileIndex];
            int x = getXCol(col) + getXTimeOffset(tile, frameCount);
            int y = getYRow(row) + getYTimeOffset(tile, frameCount);
            
            g2.drawImage(img, x, y, x + TILE_SIZE + 1, y + TILE_SIZE + 1,
                        0, 0, TILE_SIZE + 1, TILE_SIZE + 1, null);
         }     
   }
   
   // For indexing the tile graphics by value.
   private int getIndexForValue(int value)
   {
      return 31 - Integer.numberOfLeadingZeros(value / 2);
   }
   
   // For calculating the x distance for a given column.
   private int getXCol(int col)
   {
      int x = LINE_SIZE + col * (LINE_SIZE + TILE_SIZE + 1);
      return x;
   }
   
   // Is same as get column because of the board's symmetry.
   private int getYRow(int row)
   {
      return getXCol(row);
   }

   // Draws the grid onto the JPanel.
   private void drawGrid(Graphics2D g2)
   {            
      // Bg color fill
      g2.setColor(bgColor);
      g2.fillRect(0,0, size, size);
      
      // grid outline
      g2.setColor(gridColor);
      g2.setStroke(new BasicStroke(LINE_SIZE));         
      
      for(int col = OFFSET; col < size; col += TILE_SIZE + LINE_SIZE + 1)  
      {
         for (int row = OFFSET; row < size; row += TILE_SIZE + LINE_SIZE + 1)
         {            
            g2.drawLine(0, row, size, row); // Draw horizontal lines
            g2.drawLine(col, 0, col, size); // Draw vertical lines
         }
      }
   }
   
   
   // ***************** Overrides **************************
   //
   //
   // ******************************************************
     

   @Override
   public void actionPerformed(ActionEvent e)
   {
      
   }
   
   @Override
   public void run()
   {}


   // For debugging purposes.
   @SuppressWarnings("unused")
   private static void printBoard(Tile[][] board)
   {
      int BOARD_DIM = 4;
      
      for (int row = 0; row < BOARD_DIM; row++)
      {
         for (int col = 0; col < BOARD_DIM; col++)
         {
            if (board[row][col] == null)
               System.out.print("-\t");
            else
               System.out.print(board[row][col].toString() + "\t");
         }
         System.out.println("");
      }
      System.out.println("");
   }


   

   
}
