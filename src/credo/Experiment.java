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
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 *
 * @author Tomek
 */
public class Experiment {
    public ArrayList<ExperimentConfiguration> experimentConfigurations = null;
    public Experiment(ArrayList<ExperimentConfiguration> experimentConfigurations){
        this.experimentConfigurations = experimentConfigurations;
    }
    
    public void runExperiments(PrintStream out) throws SimulatorException, IOException{
        for (ExperimentConfiguration experimentConfiguration : experimentConfigurations) {
            out.println("Starting experiment with id " + Integer.toString(experimentConfiguration.id));
            runExperiment(experimentConfiguration, out);
        }
    }
    
    public void runExperiment(ExperimentConfiguration experimentConfiguration, PrintStream out) throws SimulatorException, IOException{
        double th = experimentConfiguration.th;
        double phi =experimentConfiguration.phi;
        double offsetX = experimentConfiguration.offsetX;
        double offsetY = experimentConfiguration.offsetY;
        int N = experimentConfiguration.N;
        ShowerDistribution.setR0(experimentConfiguration.r0);
        Simulation sim = new Simulation(experimentConfiguration.sampleSizeX, 
                                        experimentConfiguration.sampleSizeY, 
                                        experimentConfiguration.regionSizeX, 
                                        experimentConfiguration.regionSizeY, 
                                        experimentConfiguration.backgroundMezonsPerSquaredCentimeter);
        //PrintStream out = System.out;
        ArrayUtils.out = out;
        sim.out = out;
        BackgroundDistribution.out = out;
        ShowerDistribution.out = out;

        String expId = Integer.toString(experimentConfiguration.id);
        
        sim.init(th, phi, offsetX, offsetY, N, true);
        sim.runSimulation();
        //find detection
        sim.loadDetectors(experimentConfiguration.detectorsFile, false);
        sim.runDetection();
        sim.saveDetections(experimentConfiguration.outputDir + "/" + expId + "detections.csv");
        //get images
        BufferedImage img = sim.getHitImgage(0.1, false);
        BufferedImage imgLog = sim.getHitImgage(0.1, true);
        
        BufferedImage imdDet = sim.getDetectionImgage(0.1, false);
        //Saving image
        File outputfile = new File(experimentConfiguration.outputDir + "/" + expId + "saved_lut.png");
        File outputfileLog = new File(experimentConfiguration.outputDir + "/" + expId + "savedLog_lut.png");
        File outputfileDet = new File(experimentConfiguration.outputDir + "/" + expId + "savedDet_lut.png");

        ImageIO.write(img, "png", outputfile);
        ImageIO.write(imgLog, "png", outputfileLog);
        ImageIO.write(imdDet, "png", outputfileDet);
        out.println("Done");
    }

}
