/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package credo;

import static credo.Simulation.grid_size_x;
import static credo.Simulation.grid_size_y;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
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
            out.println("Experiment with id " + Integer.toString(experimentConfiguration.id) + " has been finished.");
        }
    }
    
    public void saveElapsedTime(String fileName, int id, long elapsedTime) throws IOException {
        FileWriter fw = new FileWriter(fileName, true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter out = new PrintWriter(bw);
        out.println(Integer.toString(id) + "," + Long.toString(elapsedTime));
        out.close();
        bw.close();
        fw.close();
    }
    
    public void runExperiment(ExperimentConfiguration experimentConfiguration, PrintStream out) throws SimulatorException, IOException{
        double th = experimentConfiguration.th;
        double phi =experimentConfiguration.phi;
        double offsetX = experimentConfiguration.offsetX;
        double offsetY = experimentConfiguration.offsetY;
        int N = experimentConfiguration.N;
        ShowerDistribution.setR0(experimentConfiguration.r0);
        
        long start = System.currentTimeMillis();
        
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
        
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        saveElapsedTime(experimentConfiguration.outputDir + "/elapsed.csv", experimentConfiguration.id, timeElapsed);        
        
        sim.saveDetections(experimentConfiguration.outputDir + "/" + expId + "detections.csv");
        sim.saveDetectors(experimentConfiguration.outputDir + "/" + expId + "detectors.csv");
        //get images
        if (experimentConfiguration.outputImageScale > 0) {
            double[]minMaxImg = new double[2];
            BufferedImage img = sim.getHitImgage(experimentConfiguration.outputImageScale, false, minMaxImg);
            double[]minMaxImgLog = new double[2];
            BufferedImage imgLog = sim.getHitImgage(experimentConfiguration.outputImageScale, true, minMaxImgLog);

            double[]minMaxImdDet = new double[2];
            BufferedImage imdDet = sim.getDetectionImgage(experimentConfiguration.outputImageScale, false, minMaxImdDet);
            
            double[]minMaxImdDetetector = new double[2];
            BufferedImage imdDetector = sim.getDetectorImgage(experimentConfiguration.outputImageScale, false, minMaxImdDetetector);
            //BufferedImage imgCircle = sim.getCircleImgage(experimentConfiguration.outputImageScale);
            //Saving image
            File outputfile = new File(experimentConfiguration.outputDir + "/" + expId + "saved_lut.png");
            File outputfileLog = new File(experimentConfiguration.outputDir + "/" + expId + "savedLog_lut.png");
            File outputfileDet = new File(experimentConfiguration.outputDir + "/" + expId + "savedDet_lut.png");
            File outputfileDetector = new File(experimentConfiguration.outputDir + "/" + expId + "savedDetector_lut.png");
            //File outputfileCircle = new File(experimentConfiguration.outputDir + "/" + expId + "savedCircle.png");

            ImageIO.write(img, "png", outputfile);
            ImageIO.write(imgLog, "png", outputfileLog);
            ImageIO.write(imdDet, "png", outputfileDet);
            ImageIO.write(imdDetector, "png", outputfileDetector);
            //ImageIO.write(imgCircle, "png", outputfileCircle);
            
            //ImageDetailsData imgDeatils = new ImageDetailsData(minMaxImg[0], minMaxImg[1], sim.hitCountInCircle, experimentConfiguration);
            ImageDetailsData imgDeatils = new ImageDetailsData(minMaxImg[0], minMaxImg[1], experimentConfiguration);
            imgDeatils.saveImageDetails(experimentConfiguration.outputDir + "/" + expId + "imgDeatils.txt");
            
            //ImageDetailsData imgLogDeatils = new ImageDetailsData(minMaxImgLog[0], minMaxImgLog[1], sim.hitCountInCircle, experimentConfiguration);
            ImageDetailsData imgLogDeatils = new ImageDetailsData(minMaxImgLog[0], minMaxImgLog[1], experimentConfiguration);
            imgLogDeatils.saveImageDetails(experimentConfiguration.outputDir + "/" + expId + "imgLogDeatils.txt");
            
            //ImageDetailsData imdDetDeatils = new ImageDetailsData(minMaxImdDet[0], minMaxImdDet[1], sim.hitCountInCircle, experimentConfiguration);
            ImageDetailsData imdDetDeatils = new ImageDetailsData(minMaxImdDet[0], minMaxImdDet[1], experimentConfiguration);
            imdDetDeatils.saveImageDetails(experimentConfiguration.outputDir + "/" + expId + "imgDetDeatils.txt");
            
            ImageDetailsData imdDetetectorDeatils = new ImageDetailsData(minMaxImdDetetector[0], minMaxImdDetetector[1], experimentConfiguration);
            imdDetDeatils.saveImageDetails(experimentConfiguration.outputDir + "/" + expId + "imgDetectorDeatils.txt");
            //out.println("Done");
        }
    }

}
