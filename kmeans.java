/*** Author:Vibhav Gogate
The University of Texas at Dallas

Additional code by Arianna Smith
*****/

import java.util.*;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.lang.*;
 

public class kmeans{
   public static void main(String [] args){
      // Arguments are image, k, file to write to
   	if (args.length < 3){
   	    System.out.println("Usage: Kmeans <input-image> <k> <output-image>");
   	    return;
	   }
	
      try{
   	    BufferedImage originalImage = ImageIO.read(new File(args[0]));
   	    int k = Integer.parseInt(args[1]);
   	    BufferedImage kmeansJpg = kmeans_helper(originalImage , k);
   	    ImageIO.write(kmeansJpg, "jpg", new File(args[2])); 
   	    
   	}
      catch(IOException e){
   	    System.out.println(e.getMessage());
	   }	
   }
    
   private static BufferedImage kmeans_helper(BufferedImage originalImage, int k){
   	int w = originalImage.getWidth();
   	int h = originalImage.getHeight();
   	BufferedImage kmeansImage = new BufferedImage(w, h, originalImage.getType());
   	Graphics2D g = kmeansImage.createGraphics();
   	g.drawImage(originalImage, 0, 0, w, h , null);
   	
      // Read rgb values from the image
   	int[] rgb = new int[w * h];
   	int count = 0;
   	for(int i = 0; i < w; i++){
         for(int j = 0; j < h; j++){
            rgb[count++] = kmeansImage.getRGB(i, j);
   	    }
   	}
   	
      // Call kmeans algorithm: update the rgb values
   	kmeans(rgb, k);
   
   	// Write the new rgb values to the image
   	count=0;
   	for(int i = 0; i < w; i++){
   	   for(int j = 0; j < h; j++){
   		   kmeansImage.setRGB(i, j, rgb[count++]);
   	    }
   	}
	   
      return kmeansImage;
   }

   // Your k-means code goes here
   // Update the array rgb by assigning each entry in the rgb array to its cluster center
   private static void kmeans(int[] rgb, int k){
      Random rand = new Random();
      rand.setSeed(22);
     
      
      // Initialize centroid matrix- pick k random pixels in image and create matrix with RBG values
      int centers[][] = new int[k][3];
      for(int a = 0; a < k; a++){
         int randK = rand.nextInt(rgb.length - 1);
         int kColor = rgb[randK];
         int blue = kColor & 0xff;
         int green = (kColor & 0xff00) >> 8;
         int red = (kColor & 0xff0000) >> 16;

         centers[a][0] = blue;
         centers[a][1] = green;
         centers[a][2] = red;
        
      }
      
      // Initialize image pixel RGB matrix
      // Last column is for cluster assigment
      int imageColors[][] = new int[rgb.length][4];
      for(int b = 0; b < rgb.length; b++){
         int iColor = rgb[b];
         int iBlue = iColor & 0xff;
         int iGreen = (iColor & 0xff00) >> 8;
         int iRed = (iColor & 0xff0000) >> 16;
         
         imageColors[b][0] = iBlue;
         imageColors[b][1] = iGreen;
         imageColors[b][2] = iRed;
      }  
      
      for(int z = 0; z < 100; z++){
         
         // Matrix to compute average of new centers
         int newCenters[][] = new int[k][4];
         
         // For every pixel in image
         for(int c = 0; c < rgb.length; c++){
            int minK = 0;
            int minDistance = 1000;
            
            // Compute distance to each k using Euclidian distance
            for(int d = 0; d < k; d++){          
               double blueD = Math.pow((Math.abs(imageColors[c][0] - centers[d][0])), 2);
               double greenD = Math.pow((Math.abs(imageColors[c][1] - centers[d][1])), 2);
               double redD = Math.pow((Math.abs(imageColors[c][2] - centers[d][2])), 2);
               
               double thisDistance = Math.sqrt(blueD + greenD + redD);
               if(thisDistance < minDistance){
                  minDistance = (int) thisDistance;
                  minK = d;
               }
            }
            
            
            // Assignment to minimum distance
            imageColors[c][3] = minK;
            
            // Add each value and keep track of count for later average
            for(int e = 0; e < 3; e++){
               newCenters[minK][e] += imageColors[c][e];            
            }
            newCenters[minK][3] += 1;
            
         }
         
         
         
         // Compute new centers
         for(int f = 0; f < k; f++){
            for(int g = 0; g < 3; g++){
               centers[f][g] = newCenters[f][g] / newCenters[f][3];
            }
         }
      }


      
      // After n iterations, change rbg values of each pixel to same as assigned cluster
      // Convert back to rgb 
      for(int i = 0; i < rgb.length; i++){
          int kAssignment = imageColors[i][3];
          int Blue = centers[kAssignment][0];
          int Green = centers[kAssignment][1];
          int Red = centers[kAssignment][2];
          
          int newrgb = 255;
          newrgb = (newrgb << 8) + Red;
          newrgb = (newrgb << 8) + Green;
          newrgb = (newrgb << 8) + Blue;
          rgb[i] = newrgb;
      }
      
      
   }

}