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
    
    //parametry:
    //region_size_x, region_size_y, sample_size_x, sample_size_y, backgroundMezonsPerSquaredCentimeter, 
    //        double th = 65;
        //double phi =180;
        //double offsetX = 0;
        //double offsetY = 0;
        //int N = 100000;
    public static double sample_size_x = 1;//in centimeters
    public static double sample_size_y = 1;//in centimeters
    public static double region_size_x = 128 * 100;//in centimeters
    public static double region_size_y = 128 * 100;//in centimeters
    public static double backgroundMezonsPerSquaredCentimeter = 1.0 / 60;//in centimeters per second
    
    public static int grid_size_x = 0;//in centimeters
    public static int grid_size_y = 0;//in centimeters
    

    public static void main(String[] args) throws SQLException {
        grid_size_x = (int)(region_size_x / sample_size_x);
        grid_size_y = (int)(region_size_y / sample_size_y);
        
        double th = 65;
        double phi =180;
        double offsetX = 0;
        double offsetY = 0;
        int N = 100000;
        
        long backgroundMionsCount = (int)(region_size_x * region_size_y * backgroundMezonsPerSquaredCentimeter / (sample_size_x * sample_size_y));
        
        //Arrays initialization
        System.out.println("Assigning memory for noise array");
        short[][]background = ArrayUtils.makeArrayShort(grid_size_x, grid_size_y, true);
        System.out.println("Assigning memory for hit array");
        short[][]hit = ArrayUtils.makeArrayShort(grid_size_x, grid_size_y, true);
        System.out.println("Assigning memory detector array");
        short[][]detectors = ArrayUtils.makeArrayShort(grid_size_x, grid_size_y, true);
        
        //Bacground
        System.out.println("Initializating background");
        BackgroundDistribution.initializeBackground(background, backgroundMionsCount, true);
        //Hit
        System.out.println("Hit generation");       
        double [][]hitdata = ShowerDistribution.generateHit(N, th, phi, true);
        System.out.println("Assigning hit to array");
        //in centimeters
        double offsetXScale = -3000 / sample_size_x;
        double offsetYScale = 3000 / sample_size_y;
        ArrayUtils.assignHitToArray(hit, hitdata, offsetXScale, offsetYScale);
        
        //Debug image
        double factor = 0.1;
        System.out.println("Resampling hit data");
        int[][]resampledHitData = ArrayUtils.resampleData(hit, factor);
        System.out.println("Resampling background data");
        int[][]resampledBackgroundData = ArrayUtils.resampleData(background, factor);
        System.out.println("Generating image");
        
        //Saving image
        File lutFile = new File("alut.png");
        BufferedImage alut = null;
        
        try {
            alut = ImageIO.read(lutFile);
        } catch (IOException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        BufferedImage img = ArrayUtils.generateImage(resampledHitData, resampledBackgroundData, false, alut);
        BufferedImage imgLog = ArrayUtils.generateImage(resampledHitData, resampledBackgroundData, true, alut);
       
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


}
