package com.axiomcomputing;

import java.util.ArrayList;
import java.util.Random;



/**
 * @author Christopher Caldwell
 *  For CST-338 Final
 *  Date: 8/7/2018
 *
 */
public class GameModel
{ 
   private static final int BOARD_DIM = 4;
   private static final int CHANCE_OF_4 = 25; // 1 in 25;
   private Random rand;
   
   //private Tile[][] board;
   private ArrayList<Tile[][]> transitions;
   
   private boolean didWin;
   public boolean getDidWin()
   {
      return didWin;
   }
   private int playerScore;
   public int getPlayerScore()
   {
      return playerScore;
   }
   private int highScore;
   public int getHighScore()
   {
      return highScore;
   }
  
   public GameModel()
   {
      init();
   }
   
   private void init()
   {
      Tile[][] board = new Tile[BOARD_DIM][BOARD_DIM];
      transitions = new ArrayList<Tile[][]>();
      transitions.add(board);
      rand = new Random();
      highScore = 0;
      newGame();
   }
   
   // Resets the game board.
   // Updates the highScore.
   public void newGame()
   {
      clearBoard();
     
      didWin = false;
      if (playerScore > highScore)
         highScore = playerScore;      
      playerScore = 0;
      
      // Populate board with 2 initial tiles.
      placeRandomTile();
      placeRandomTile();
   }
   
   // ******************** BOARD MANIPULATIONS ********************
   //
   //
   // **************************************************************  
 
   // Attempts to apply a movement to the game board.
   // Returns true if any movement was done, false otherwise.
   public boolean move(Direction dir)
   {
      boolean didMove = false;
      // Manipulate copy of board state.
   // For simplicity, rotate board and perform a down movement on it.
      Tile[][] tempBoard = rotateBoard(getBoardState(), dir, false);

      // Do down movement
      clearDidMergeFlag(tempBoard);
      setDirectionFlags(tempBoard, dir);
      didMove = collapseDown(tempBoard, dir);
                  
      return didMove;
   }
   
   // Assigns the direction flags of the game board given which direction
   // is passed in as an argument. For example, On a left movement tiles
   // would be flagged with the Left Direction flag, unless the tile cannot
   // move, in which case it is given the Direction.None flag.
   private static boolean setDirectionFlags(Tile[][] tempBoard, Direction dir)
   {
      if (tempBoard == null)
         return false;
      for (int col = 0; col < BOARD_DIM; col++)
         for (int row = BOARD_DIM - 1; row >= 0; row--)
         {
            Tile top = tempBoard[row][col];           
            if (top == null)
               continue;
                                
            if (row == BOARD_DIM - 1)
               top.setDirection(Direction.None);
            else
            {
               Tile bottom = tempBoard[row + 1][col];
               
               if (bottom == null) // Definitely move into null space.
                  top.setDirection(dir);
               else if (top.canMerge(bottom)) // Can tile merge?
               {
                  top.setDirection(dir);
               }
               else if (bottom.getDirection() == Direction.None)
               {
                  // Bottom is stopped. Also stop.
                  top.setDirection(Direction.None);   
               }
               else
               {
                  top.setDirection(dir);
               }
            }
         }
                        
     return true;
   }
   
   // Does not modify reference, modifies model state.
   // Rotates and performs the down movement upon a given game board.
   // After performing the down movement, it then unrotates the board
   // so as to emulate moving the board in any given direction.
   // If a movement was done, this method updates the transitions array.
   private boolean collapseDown(Tile[][] tempBoard, Direction dir)
   {
      boolean didMove = false;
      ArrayList<Tile[][]> tempTransitions = new ArrayList<Tile[][]>();
      Tile[][] workingBoard = copyBoard(tempBoard);
      tempTransitions.add(rotateBoard(copyBoard(tempBoard), dir, true));
          
      for (int time = 1; time < BOARD_DIM; time++)
      {       
         if (progressCollapse(workingBoard, dir))
            didMove = true;
         else
            break;
         tempTransitions.add(rotateBoard(workingBoard, dir, true));
         workingBoard = copyBoard(workingBoard);
      }
      
      // Un-rotate board, and save.      
      if (didMove)
      {        
         workingBoard = rotateBoard(workingBoard, dir, true);      
         tempTransitions.set(tempTransitions.size() - 1, workingBoard);
         this.transitions = tempTransitions;  
         placeRandomTile();
      }

      return didMove;
   }
   
   // Set all merge flags of the argument's board tiles to did not merge.
   private void clearDidMergeFlag(Tile[][] tempBoard)
   {
      for (int row = 0; row < BOARD_DIM; row++)
         for (int col = 0; col < BOARD_DIM; col++)
            if (tempBoard[row][col] != null)
               tempBoard[row][col].setDidMerge(false);
   }
   
   // Modifies reference.
   // Takes a single step in the down movement on the argument board.
   private boolean progressCollapse(Tile[][] tempBoard, Direction dir)
   {
      boolean didMove = false;     
      
      for (int col = 0; col < BOARD_DIM; col++)
      {
         for (int row = BOARD_DIM - 1; row >= 0; row--)
         {
            if (tempBoard[row][col] == null)
               continue;

            Tile tileTop = tempBoard[row][col];

            if (row == BOARD_DIM - 1)
            {
               tileTop.setDirection(Direction.None);
               continue;
            }
            
            Tile tileBottom = tempBoard[row + 1][col];

            if (tileBottom == null)
            {
               // Move tile down one.
               tempBoard[row][col] = null;
               tempBoard[row + 1][col] = tileTop;
               didMove = true;
            }
            else if (tileTop.canMerge(tileBottom))
            {                            
               // Merge
               Tile newTile = new Tile(tileTop.getValue() * 2);
               
               if (newTile.getValue() == 2048)
                  this.didWin = true;
               
               newTile.setDidMerge(true);
               tempBoard[row + 1][col] = newTile;
               tempBoard[row][col] = null;
               
               playerScore += newTile.getValue();
               didMove = true;
            }
            else
            {
               tileTop.setDirection(Direction.None);
            }
            
            setDirectionFlags(tempBoard, dir);
         }
      }
      
      return didMove;
   }
       
   // Returns new reference.
   // Rotates the 2D Tile array argument to the right.
   private static Tile[][] rightRotate(Tile[][] inputBoard)
   {
      Tile[][] rotatedBoard = new Tile[BOARD_DIM][BOARD_DIM];
      for (int row = 0; row < BOARD_DIM; row++)
         for (int col = 0; col < BOARD_DIM; col++)
         {
            Tile orig = inputBoard[BOARD_DIM - col - 1][row];
            rotatedBoard[row][col] = orig;
         }
    
      return rotatedBoard;
   }
   
   // Returns new reference.
   // Rotates the 2D Tile array argument to the left.
   private static Tile[][] leftRotate(Tile[][] inputBoard)
   {      
      Tile[][] rotatedBoard = new Tile[BOARD_DIM][BOARD_DIM];
      for (int row = 0; row < BOARD_DIM; row++)
         for (int col = 0; col < BOARD_DIM; col++)
         {
            Tile orig = inputBoard[col][BOARD_DIM - row - 1];
            rotatedBoard[row][col] = orig;
         }
    
      return rotatedBoard;
   }
   
   // Sets all indexes of the main board 2D Tile array to null.
   private boolean clearBoard()
   {
      Tile[][] board = getBoard();
      if (board == null)
         return false;     
      for (int row = 0; row < BOARD_DIM; row++)
         for(int col = 0; col < BOARD_DIM; col++)
            board[row][col] = null;
      return true;
   }
   
   // Places a tile with a value of either 2 or 4 into the main board.
   private boolean placeRandomTile()
   {
      if (isBoardFull())
         return false;
      
      Tile[][] board = getBoard();
      int row, col;
      
      // Get random board location that's empty.
      while (true)
      {
         row = rand.nextInt(BOARD_DIM);
         col = rand.nextInt(BOARD_DIM);
         
         if (board[row][col] == null)
            break;      
      }        
      
      int odds = rand.nextInt(CHANCE_OF_4);
      int value = odds == 0 ? 4 : 2;
      
      Tile tile = new Tile(value);
      tile.setDidMerge(true);
      board[row][col] = tile;
      
      return true;
   }
   
   // Returns a reference to the board representing the actual state
   // of the game board.
   private Tile[][] getBoard()
   {
      return transitions.get(transitions.size() - 1);
   }

   // Encapsulates which rotations are needed to perform on a board
   // in order for a down movement to equal a movement of any other direction.
   // If the reverse flag is true, the rotations undo the initial rotations.
   private static Tile[][] rotateBoard(Tile[][] tempBoard, 
               Direction dir, boolean reverse)
   {
      switch(dir)
      {
         case Right:
            if (reverse)
               tempBoard = leftRotate(tempBoard);
            else
               tempBoard = rightRotate(tempBoard);
            break;
         case Left:
            if (reverse)
               tempBoard = rightRotate(tempBoard);
            else
               tempBoard = leftRotate(tempBoard);
            break;
         case Up:
            if (reverse)
            {
               tempBoard = leftRotate(tempBoard);
               tempBoard = leftRotate(tempBoard);
            }
            else
            {
               tempBoard = rightRotate(tempBoard);
               tempBoard = rightRotate(tempBoard);
            }
            break;
         default:
            break;            
      }
      
      return tempBoard;
   }
   
   // ******************** HELPER FUNCTIONS ********************
   //
   //
   // **************************************************************
   
   // For debugging. Outputs board to console.
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
   }
   
   // For debugging. Sets the main board to all possible tile values.
   public void createTestPattern()
   {
      Tile[][] board = getBoard();
      int value = 2;
      for (int row = 0; row < BOARD_DIM; row++)
         for(int col = 0; col < BOARD_DIM; col++)
         {
            Tile tile = new Tile(value);
            value *= 2;
            if (value > 5000)
               value = 2;
            board[row][col] = tile;
         }
   }

   // Returns true if all tiles are full/have values.
   public boolean isBoardFull()
   {
      Tile[][] board = getBoard();
      for (int row = 0; row < BOARD_DIM; row++)
         for(int col = 0; col < BOARD_DIM; col++)
            if (board[row][col] == null)
               return false;
      return true;
   }
   
   // Checks if any move is possible on the current board.
   public boolean isMovePossible()
   {
      if (!isBoardFull())
         return true;
      
      Tile[][] board = getBoard();
      // Board is full. Check if any adjacent tiles share same value.
      for (int row = 0; row < BOARD_DIM-1; row++)
         for(int col = 0; col < BOARD_DIM-1; col++)
         {
            // Look right.
            if (board[row][col].getValue() == board[row][col + 1].getValue())
               return true;
            // Look down.
            if (board[row][col].getValue() == board[row + 1][col].getValue())
               return true;
         }
      
      // Check last column, look down.
      for (int row = 0; row < BOARD_DIM - 1; row++)
         if (board[row][BOARD_DIM - 1].getValue() 
                     == board[row + 1][BOARD_DIM - 1].getValue())
            return true;
      
      // No move is possible.
      return false;
   }
   
   // Returns a deep copy of a board argument.
   private Tile[][] copyBoard(Tile[][] original)
   {
      if (original == null)
      return null;
      
      Tile[][] copy = new Tile[BOARD_DIM][BOARD_DIM];
      for (int row = 0; row < BOARD_DIM; row++)
         for(int col = 0; col < BOARD_DIM; col++)
         {
            if (original[row][col] == null)
               continue;
            
            Tile tile = null;
            try
            {
               tile = (Tile)original[row][col].clone();
            }
            catch (CloneNotSupportedException ex)
            {
               ex.printStackTrace();
            }
            copy[row][col] = tile;
         }
      return copy;
   }
   
   // Returns a deep copy of the board's current state
   public Tile[][] getBoardState()
   {
      return copyBoard(getBoard());
   }
   
   // Returns the current transitions.
   public ArrayList<Tile[][]> getLastTransitions()
   {
      return transitions;
   }
}
