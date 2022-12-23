package com.axiomcomputing;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
/**
 * @author Christopher Caldwell
 *  For CST-338 Final
 *  Date: 8/7/2018
 *
 */

// Returns the font used in the application.
public class FontFetcher
{
   private static final String fontDir = "fonts\\";
   private static Font font = null;
   
   // Gets the font once, stores it to be served if called again.
   public static Font getFont()
   {
      if (font != null)
         return font;
      
      try 
      {
         String path = fontDir + "Lato-Bold.ttf";

         // Get absolute path
         File file = new File(path);
         String absolutePath = file.getAbsolutePath();

         InputStream stream = new FileInputStream(path);
         font = Font.createFont(Font.TRUETYPE_FONT, stream);
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
      }
      
      return font;
   }
}
