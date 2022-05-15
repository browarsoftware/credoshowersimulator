/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package credo;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/*
#plt.colorbar()

#####################

#parametry fizyczne#

####################

#rozmiar obszaru m x m

#rozdzielczość czasowa aparatury

#parametry rozkładu pęku (współprzędne sferyczne, liczba cząstek, r0, s), zakładamy, że pęk "spada" jednocześnie

#liczba detektorów, ewentualnie rozkład ich na powierzchni, nie zakładamy krzywizny ziemi

#rozmiar detektora (w cm x cm)

#rozkład szumu tła, i jego parametry (w tym jak dużo cząstek uderza w kwancie czasu)



#---------------------------------------------------------

#Parametry dodatkowe do modelowania istoności uderzeń w detektor#

#################################################################

#romizary jąder gaussa / do splotu, który modeluje prawdopodobieństo uderzenia (miara istotności zdarzenia)

#zdarzenia blikso siebie będą się kumulowały, daleko od siebie - niewlowały



#przykład zastosowania: dla miasta np. Krakowa, zakładamy rozkład telefonów zgodny z gęstością zaludnienia i robi jakiś test

#różne geometrie i patrzeć które są "skuteczne"

#miarą "skuteczności" ma być wykrecie pęku, że rozkład punktów jest zgodny z jakąś charakterystyką statystyczną
*/


/**
 *
 * @author Tomasz Hachaj
 */

/**
 * W rozdziale 30.3.1 tablic cząstek P.A. Zylaet al.(Particle Data Group), Prog. Theor. Exp. Phys.2020, 083C01 (2020)DOI: 10.1093/ptep/ptaa104 piszą, że gęstość strumienia mionów na powierzchni ziemi ma prostą do zapamiętania wartość: 1 mion /(cm^2 minuta). Czyli ten strumień nie jest bardzo gęsty. To jest dla nas dobra wiadomość.

Co do ogólnej zależności liczby mionów w pęku od energii to nie ma jednoznacznej odpowiedzi, bo energia początkowej cząstki jest tylko jednym z parametrów, który wpływa na to ile mionów dolatuje do ziemi. Inne parametry to wysokość na jakiej rozpoczęła się kaskada oraz kąt pod jakim ta kaskada przemierza atmosferę. Oba te parametry łącznie mówią jaką drogę miony przebyły w atmosferze a co za tym idzie ile z nich zdążyło się rozpaść.

Niemniej jednak można chyba przyjąć, że liczby mionów w pęku w przedziale 10^4-10^6 są realistyczne.

Pozdro,
* 
* Symulacja co sekundę
 * @author Tomek
 */

public class main {

    /**
     * @param args the command line arguments
     */
    
    public static double sample_size_x = 1;//in centimeters
    public static double sample_size_y = 1;//in centimeters
    public static double region_size_x = 512 * 100;//in centimeters
    public static double region_size_y = 512 * 100;//in centimeters
    public static double backgroundMezonsPerSquaredCentimeter = 1.0 / 60;//in centimeters per second
    
    public static int grid_size_x = 0;//in centimeters
    public static int grid_size_y = 0;//in centimeters
    /*
    public static short[][]makeArrayShort(int sizeX, int sizyY, boolean verbose) {
        short[][]array = new short[sizyY][];
        long percent = (long)((double)grid_size_x / 100.0);
        int percentCount = 0;
        int percentStep = 10;
        for (int a = 0; a < array.length; a++){
            if (a % (percent * percentStep) == 0 && verbose)
            {
                System.out.println(Integer.toString(percentCount) + "%");
                percentCount+=percentStep;
            }
            array[a] = new short[sizeX];
        }
        return array;
    }
    
    public static int[][]makeArrayInt(int sizeX, int sizyY, boolean verbose) {
        int[][]array = new int[sizyY][];
        long percent = (long)((double)grid_size_x / 100.0);
        int percentCount = 0;
        int percentStep = 10;
        for (int a = 0; a < array.length; a++){
            if (a % (percent * percentStep) == 0 && verbose)
            {
                System.out.println(Integer.toString(percentCount) + "%");
                percentCount+=percentStep;
            }
            array[a] = new int[sizeX];
        }
        return array;
    }
    */
    public static void initializeBackground(short[][]array, long backgroundMionsCount, boolean verbose) {
        long percent = (long)((double)backgroundMionsCount / 100.0);
        int percentCount = 0;
        int percentStep = 10;
        int x, y;
        for (long counter = 0; counter < backgroundMionsCount; counter++){
            if (counter % (percent * percentStep) == 0 && verbose)
            {
                System.out.println(Integer.toString(percentCount) + "%");
                percentCount+=percentStep;
            }
            x = rand.nextInt(grid_size_x);
            y = rand.nextInt(grid_size_y);
            array[x][y] = (short) (array[x][y] + 1);
        }
    }
    
    public static void main(String[] args) throws SQLException {
        grid_size_x = (int)(region_size_x / sample_size_x);
        grid_size_y = (int)(region_size_y / sample_size_y);
        
        long backgroundMionsCount = (int)(region_size_x * region_size_y * backgroundMezonsPerSquaredCentimeter);

        System.out.println("Assigning memory for noise array");
        short[][]background = ArrayUtils.makeArrayShort(grid_size_x, grid_size_y, true);
        System.out.println("Assigning memory for hit array");
        short[][]hit = ArrayUtils.makeArrayShort(grid_size_x, grid_size_y, true);
        System.out.println("Assigning memory detector array");
        short[][]detectors = ArrayUtils.makeArrayShort(grid_size_x, grid_size_y, true);
        

        System.out.println("Initializating background");
        initializeBackground(background, backgroundMionsCount, true);

        System.out.println("Hit generation");
        double th = 45;
        double phi =320;
        int N = 1000000;
        double xmin = 0.5;
        double xmax = 5.0 * r0;
        double [][]hitdata = generateHit(N, xmin, xmax, th, phi, true);
        System.out.println("Assigning hit to array");
        assignHitToArray(hit, hitdata);
       
        double factor = 0.01;
        System.out.println("Resampling hit data");
        int[][]resampledHitData = resampleData(hit, factor);
        System.out.println("Resampling background data");
        int[][]resampledBackgroundData = resampleData(background, factor);
        System.out.println("Generating image");
        
        File lutFile = new File("alut.png");
        BufferedImage alut = null;
        
        try {
            alut = ImageIO.read(lutFile);
        } catch (IOException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        BufferedImage img = generateImage(resampledHitData, resampledBackgroundData, false, alut);
        BufferedImage imgLog = generateImage(resampledHitData, resampledBackgroundData, true, alut);
       
        File outputfile = new File("saved_lut.png");
        File outputfileLog = new File("savedLog_lut.png");
        try {
            ImageIO.write(img, "png", outputfile);
            ImageIO.write(imgLog, "png", outputfileLog);
        } catch (IOException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Done");
    }
    /*
    public static BufferedImage generateImage(int[][]resampledData){
        int xxx = resampledData.length;
        int yyy = resampledData[0].length;
        int[]imageDate = new int[xxx * yyy * 3];
        
        for (int a = 0; a < xxx; a++)
            for (int b = 0; b < yyy; b++)
            {
                //imageDate[3 * a + b * xxx] = 0xff0000ff;//rand.nextInt(256);
                //System.out.println(resampleData[a][b]);
                int x_coord = xxx - a - 1;
                int y_coord = b;
                if (resampledData[x_coord][b] > 0)
                    imageDate[a + b * xxx] = 0x00ffffff;
                //    imageDate[3 * a + b * xxx] = 255;
                //BLUE
                //imageDate[a + 120 * xxx] = 0x00ffffff;
                //GREEN
                //imageDate[(xxx * yyy) + a + 120 * xxx] = 255;
                //RED
                //imageDate[(2 * xxx * yyy) +a + 120 * xxx] = 255;
            }
        BufferedImage img = ArrayUtils.getImageFromArray(imageDate, xxx, yyy);
        return img;
    }
    */
    public static BufferedImage generateImage(int[][]resampledData, int[][]resampledBackgroundData, boolean useLog, BufferedImage lookupTable){
        int xxx = resampledData.length;
        int yyy = resampledData[0].length;
        int[]imageDate = new int[xxx * yyy * 3];
        
        int[][]sumArray = sumArrays(resampledData, resampledBackgroundData);
        if (useLog){
            for (int a = 0; a < sumArray.length; a++)
                for (int b = 0; b < sumArray[0].length; b++)
                    sumArray[a][b] = (int)Math.log(sumArray[a][b] + 1);
        }
        int max = findMax(sumArray);
        int min = findMin(sumArray);
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
                x_coord = xxx - a - 1;
                y_coord = b;
                
                //scalling to 0-255 range
                int value = (int)(aCoef * (double)sumArray[x_coord][b] + bCoef);
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
    
    public static int lookupTable(int value){
        int ret = (255 - value) + 256 * (value) + 256 * 256 * (value);
        return ret;
    }
    
    public static int[][] sumArrays(int[][]array1, int[][]array2){
        int [][]res = ArrayUtils.makeArrayInt(array1.length, array1[0].length, false);
        for (int a = 0; a < array1.length; a++)
            for (int b = 0; b < array1[0].length; b++)
                res[a][b] = array1[a][b] + array2[a][b];
        return res;
    }
    
    public static int findMax(int[][]array1, int[][]array2){
        int maxValue = 0;
        for (int a = 0; a < array1.length; a++)
            for (int b = 0; b < array1[0].length; b++){
                if (maxValue < array1[a][b] + array2[a][b]) {
                    maxValue = array1[a][b] + array2[a][b];
                }
            }
        return maxValue;
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
                int x2 = (int)(a * 0.01);
                int y2 = (int)(b * 0.01);
                if (hit[a][b] > 0)
                    resampledData[x2][y2] = resampledData[x2][y2] + 1;
            }
        return resampledData;
    }
    
    public static void assignHitToArray(short[][]hit, double [][]hitdata) {
        int x = 0;
        int y = 0;
        System.out.println("Assigning hit to array");
        for (int a = 0; a < hitdata.length; a++){
            x = (int)(((double)grid_size_x / 2.0) + 100.0 * hitdata[a][0]);
            y = (int)(((double)grid_size_x / 2.0) + 100.0 * hitdata[a][1]);
            if (x > 0 && x < grid_size_x && y > 0 && y < grid_size_y) {
                hit[x][y] = (short)(hit[x][y] + 1);
            }
        }
    }
    
    /*
    public static BufferedImage getImageFromArray(int[] pixels, int width, int height) {
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            WritableRaster raster = (WritableRaster) image.getRaster();
            raster.setDataElements(0,0,width,height,pixels);
            return image;
        }
    */
    public static double ro(double r, double r0, double s, double N){
        double ret = N / (2 * Math.PI * r0 * r0) * gamma.gamma(4.5 - s) / (gamma.gamma(s) * gamma.gamma(4.5 - 2 * s)) * Math.pow(r / r0, s-2) * Math.pow(1 + r/r0, s-4.5);
        return ret;
    }
 
    public static double r0=100;
    public static double s=1.3;
    public static double N=10000;
    public static Random rand = new Random();
    
    public static double nextDouble(double xmin, double xmax)
    {
        double x = rand.nextDouble();
        double ret_x = (xmax - xmin) * x + xmin;
        return ret_x;
    }
    
    public static double[][] generateHit(int N, double xmin, double xmax, double th, double phi, boolean verbose)
    {
        ArrayList alret = generateHitArrayList(N, xmin, xmax, th, phi, verbose);
        double[]x = (double[])alret.get(0);
        double[]y = (double[])alret.get(1);
        double[][] ret = new double[N][];
        for (int a = 0; a < N; a++){
            ret[a] = new double[2];
            ret[a][0] = x[a];
            ret[a][1] = y[a];
        }
        return ret;
    }
    
    public static ArrayList generateHitArrayList(int N, double xmin, double xmax, double th, double phi, boolean verbose)
    {
        double []rndr = randomVariate(N, 0.5, 5*r0, verbose);
        
        double[]xl = new double[N];
        double[]yl = new double[N];
        double phi_in = 0;
        for (int a =0; a < N; a++)
        {
            phi_in = nextDouble(0, 2 * Math.PI);
            xl[a] = rndr[a] * Math.cos(phi_in);
            yl[a] = rndr[a] * Math.sin(phi_in);
        }
        double th_ = th * Math.PI / 180.0;
        double phi_ = phi * Math.PI / 180.0;
        
        
        double x = 0;
        double y = 0;
        for (int a =0; a < N; a++)
        {
            xl[a] = xl[a] / Math.cos(th_);
            x = xl[a];
            y = yl[a];
            xl[a] = x * Math.cos(phi_) - y * Math.sin(phi);
            yl[a] = x * Math.sin(phi_) + y * Math.cos(phi_);
        }
        ArrayList retal = new ArrayList();
        retal.add(xl);
        retal.add(yl);
        return retal;
    }
    
    public static double[] randomVariate(int n, double xmin, double xmax, boolean verbose)
    {
        long percent = (long)((double)n / 100.0);
        int percentCount = 0;
        int percentStep = 10;
        
        double[]pdf = new double[n];
        double df = (xmax - xmin) / (double)(n-1);
        for (int  a = 0; a < n; a++)
        {
            //double xxxx = a * df + xmin;
            double x = (double)a * df + xmin;
            pdf[a] = ro(x, main.r0, main.s, main.N);
        }
        double pmin = 0;
        double pmax = Double.MIN_VALUE;
        for (int a = 0; a < pdf.length; a++)
            if (pmax < pdf[a])
               pmax =  pdf[a];
        //Counters  
        int naccept = 0;
        int ntrial = 0;
        double []xx = new double[n];
        //double []yy = new double[n];
        double x = 0;
        double y = 0;
        while (naccept < n) {
            x = nextDouble(xmin, xmax);
            y = nextDouble(pmin, pmax);
            if (y < ro(x, main.r0, main.s, main.N)){
                if (naccept % (percent * percentStep) == 0 && verbose) {
                    System.out.println(Integer.toString(percentCount) + "%");
                    percentCount+=percentStep;
                }
                xx[naccept] = x;
                naccept++;
            }
            ntrial++;
        }
        return xx;
    }

}
