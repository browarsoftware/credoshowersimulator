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
 * @author Tomasz Hachaj
 */
public class ArrayUtils {
        public static short[][]makeArrayShort(int sizeX, int sizyY, boolean verbose) {
        short[][]array = new short[sizeX][];
        long percent = (long)((double)sizeX / 100.0);
        if (percent < 1)
            verbose = false;
        int percentCount = 0;
        int percentStep = 10;
        for (int a = 0; a < array.length; a++){
            if (verbose && a % (percent * percentStep) == 0)
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
        if (percent < 1)
            verbose = false;
        int percentCount = 0;
        int percentStep = 10;
        for (int a = 0; a < array.length; a++){
            if (verbose && a % (percent * percentStep) == 0)
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
    
        public static int[][] sumArrays(int[][]array1, int[][]array2){
        int [][]res = ArrayUtils.makeArrayInt(array1.length, array1[0].length, false);
        for (int a = 0; a < array1.length; a++)
            for (int b = 0; b < array1[0].length; b++)
                res[a][b] = array1[a][b] + array2[a][b];
        return res;
    }
    
    public static int findMax(int[][]array){
        int maxValue = 0;
        for (int a = 0; a < array.length; a++)
            for (int b = 0; b < array[0].length; b++){
                if (maxValue < array[a][b]) {
                    maxValue = array[a][b];
                }
            }
        return maxValue;
    }
    
    public static int findMin(int[][]array){
        int minValue = Integer.MAX_VALUE;
        for (int a = 0; a < array.length; a++)
            for (int b = 0; b < array[0].length; b++){
                if (minValue > array[a][b]) {
                    minValue = array[a][b];
                }
            }
        return minValue;
    }
    
        public static int[][] resampleData(short[][]hit, double factor){
        int xxx = (int)(factor * hit.length);
        int yyy = (int)(factor * hit[0].length);
        
        int[][]resampledData = ArrayUtils.makeArrayInt(xxx, yyy, false);
        for (int a = 0; a < hit.length; a++)
            for (int b = 0; b < hit[0].length; b++){
                int x2 = (int)(a * factor);
                int y2 = (int)(b * factor);
                if (hit[a][b] > 0)
                    resampledData[x2][y2] = resampledData[x2][y2] + 1;
            }
        return resampledData;
    }
    
    public static void assignHitToArray(short[][]hit, double [][]hitdata, double offsetX, double offsetY) {
        int x = 0;
        int y = 0;
        for (int a = 0; a < hitdata.length; a++){
            x = (int)(((double)hit.length / 2.0) + 100.0 * hitdata[a][0] + offsetX);
            y = (int)(((double)hit[0].length / 2.0) + 100.0 * hitdata[a][1] + offsetY);
            if (x > 0 && x < hit.length && y > 0 && y < hit[0].length) {
                hit[x][y] = (short)(hit[x][y] + 1);
            }
        }
    }   
    
    public static BufferedImage generateImage(int[][]resampledData, int[][]resampledBackgroundData, boolean useLog, BufferedImage lookupTable){
        int xxx = resampledData.length;
        int yyy = resampledData[0].length;
        int[]imageDate = new int[xxx * yyy * 3];
        
        int[][]sumArray = ArrayUtils.sumArrays(resampledData, resampledBackgroundData);
        if (useLog){
            for (int a = 0; a < sumArray.length; a++)
                for (int b = 0; b < sumArray[0].length; b++)
                    sumArray[a][b] = (int)Math.log(sumArray[a][b] + 1);
        }
        int max = ArrayUtils.findMax(sumArray);
        int min = ArrayUtils.findMin(sumArray);
        /*int maxData = findMax(resampledData);
        int maxBackground = findMax(resampledBackgroundData);
        int max = Math.max(maxData, maxBackground);*/
        double aCoef = (double)255.0 / (max - min);
        double bCoef = -min * aCoef;
        
        int x_coord = 0;
        int y_coord = 0; 
        for (int a = 0; a < xxx; a++)
            for (int b = 0; b < yyy; b++)
            {
                //x_coord = xxx - a - 1;
                //y_coord = b;
                x_coord = a;
                y_coord = yyy - b - 1;
                
                //scalling to 0-255 range
                int value = (int)(aCoef * (double)sumArray[x_coord][y_coord] + bCoef);
                if (value > 255) value = 255;
                if (value < 0) value = 0;
                //lookup table coloring
                if (lookupTable != null) imageDate[a + b * xxx] = lookupTable.getRGB(0,255 - value);
                else imageDate[a + b * xxx] = value + 256 * value + 256 * 256 * value;
                
                //    imageDate[3 * a + b * xxx] = 255;
                //BLUE
                //imageDate[a + 120 * xxx] = 0x00ffffff;
                //GREEN
                //imageDate[(xxx * yyy) + a + 120 * xxx] = 255;
                //RED
                //imageDate[(2 * xxx * yyy) +a + 120 * xxx] = 255;
            }
        //saving to buffered image
        BufferedImage img = ArrayUtils.getImageFromArray(imageDate, xxx, yyy);
        return img;
    }
}
