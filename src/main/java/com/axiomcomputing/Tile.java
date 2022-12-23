package com.axiomcomputing;

/**
 * @author Christopher Caldwell
 *  For CST-338 Final
 *  Date: 8/7/2018
 *
 */

public class Tile implements Cloneable
{
   public static final int MIN_VALUE = 2;
   public static final int MAX_VALUE = 2048;
   private int value;
   private boolean didMerge;
   public boolean getDidMerge() { return didMerge; }
   public boolean setDidMerge(boolean didMerge)
   {
      this.didMerge = didMerge;
      return true;
   }
   public int getValue() { return value; }
   private Direction direction;
   public Direction getDirection() { return direction; }
   public boolean setDirection(Direction dir)
   {
      this.direction = dir;
      return true;
   }

   public Tile()
   {
      init();
   }
   
   public Tile(int value)
   {
      init();
      this.value = value;
   }
   
   public Tile(int value, Direction direction)
   {
      init();
      this.value = value;
      this.direction = direction;
   }
   
   
   private void init()
   {
      this.value = MIN_VALUE;
      this.direction = Direction.None;
      this.setDidMerge(false);
   }  
   
   // A tile can merge with another tile only if neither
   // have merged during this movement, and both tile values are equal.
   public boolean canMerge(Tile other)
   {
      if (this.getValue() == other.getValue()  &&
                  other.getDidMerge() == false &&
                  this.getDidMerge() == false)
      {
            return true;
      }

      return false;
   }

   // Deep clone of the Tile object.
   public Tile clone() throws CloneNotSupportedException
   {
      Tile tile = new Tile(this.value, this.direction);
      tile.setDidMerge(this.getDidMerge());
      return tile;
   }
   
   // For debugging. Print the tile's value
   // and surrounds the value with asterixes if the 
   // direction of the tile is None.
   public String toString()
   {
      if (direction == Direction.None)
         return "*" + getValue() + "*";
      else
         return getValue() + "";
   }
   
}
