/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package credo;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 *
 * @author Tomek
 */
public class ArrayUtils {
        public static short[][]makeArrayShort(int sizeX, int sizyY, boolean verbose) {
        short[][]array = new short[sizeX][];
        long percent = (long)((double)sizeX / 100.0);
        int percentCount = 0;
        int percentStep = 10;
        for (int a = 0; a < array.length; a++){
            if (a % (percent * percentStep) == 0 && verbose)
            {
                System.out.println(Integer.toString(percentCount) + "%");
                percentCount+=percentStep;
            }
            array[a] = new short[sizyY];
        }
        return array;
    }
    
    public static int[][]makeArrayInt(int sizeX, int sizyY, boolean verbose) {
        int[][]array = new int[sizeX][];
        long percent = (long)((double)sizeX / 100.0);
        int percentCount = 0;
        int percentStep = 10;
        for (int a = 0; a < array.length; a++){
            if (a % (percent * percentStep) == 0 && verbose)
            {
                System.out.println(Integer.toString(percentCount) + "%");
                percentCount+=percentStep;
            }
            array[a] = new int[sizyY];
        }
        return array;
    }
    
    public static BufferedImage getImageFromArray(int[] pixels, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = (WritableRaster) image.getRaster();
        raster.setDataElements(0,0,width,height,pixels);
        return image;
    }
}
