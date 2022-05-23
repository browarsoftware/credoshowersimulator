/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package credo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import javax.imageio.ImageIO;

/**
 *
 * @author Tomasz Hachaj
 */

public class main {

    /**
     * @param args the command line arguments
     */

    public static void main(String[] args) throws IOException, SimulatorException{
        double th = 65;
        double phi =180;
        double offsetX = 0;
        double offsetY = 0;
        int N = 100000;
        Simulation sim = new Simulation();
        PrintStream out = System.out;
        ArrayUtils.out = out;
        sim.out = out;
        BackgroundDistribution.out = out;
        ShowerDistribution.out = out;
        
        sim.init(th, phi, offsetX, offsetY, N, true);
        sim.runSimulation();
        //find detection
        sim.loadDetectors("detectors.conf", false);
        sim.runDetection();
        sim.saveDetections("detections.csv");
        //get images
        BufferedImage img = sim.getHitImgage(0.1, false, null);
        BufferedImage imgLog = sim.getHitImgage(0.1, true, null);
        
        BufferedImage imdDet = sim.getDetectionImgage(0.1, false, null);
        //Saving image
        File outputfile = new File("saved_lut.png");
        File outputfileLog = new File("savedLog_lut.png");
        File outputfileDet = new File("savedDet_lut.png");

        ImageIO.write(img, "png", outputfile);
        ImageIO.write(imgLog, "png", outputfileLog);
        ImageIO.write(imdDet, "png", outputfileDet);
        out.println("Done");
    }
}
