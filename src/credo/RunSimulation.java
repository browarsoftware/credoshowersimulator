/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package credo;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;


/**
 *
 * @author Tomek
 */
public class RunSimulation extends Thread{
    MainWindow mw = null;
    public RunSimulation(MainWindow mw){
        this.mw = mw;
    }
    public void run(){
        PrintStream out = new PrintStream(new CustomOutputStream(mw));
        try {
            ArrayList<ExperimentConfiguration> al = ExperimentConfiguration.ParseExperiment(mw.experimentFileNameTextField.getText());
            Experiment exp = new Experiment(al);
            exp.runExperiments(out);
            //cleanup
            System.gc();
        } catch (SimulatorException ex) {
            mw.addTextToSimulationPane(MainWindow.errorColor, ex.toString() + "\n");
        } catch (IOException ex) {
            mw.addTextToSimulationPane(MainWindow.errorColor, ex.toString() + "\n");
        }
    }
    /*
    public void run(){
        //PrintStream out = System.out;
        PrintStream out = new PrintStream(new CustomOutputStream(mw));
        mw.setUIEnable(false);
        try {
            //try {
            //System.out.println("MyThread running");
            mw.addTextToSimulationPane(mw.warningColor, "*********************************************************\n");
            double th = 65;
            double phi =180;
            double offsetX = 0;
            double offsetY = 0;
            int N = 1000000;
            Simulation sim = new Simulation();
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
            BufferedImage img = sim.getHitImgage(0.1, false);
            BufferedImage imgLog = sim.getHitImgage(0.1, true);

            BufferedImage imdDet = sim.getDetectionImgage(0.1, false);
            //Saving image
            File outputfile = new File("saved_lut.png");
            File outputfileLog = new File("savedLog_lut.png");
            File outputfileDet = new File("savedDet_lut.png");

            ImageIO.write(img, "png", outputfile);
            ImageIO.write(imgLog, "png", outputfileLog);
            ImageIO.write(imdDet, "png", outputfileDet);
            out.println("Done");
            //}
            //catch 
        } catch (SimulatorException ex) {
            out.println(ex.getMessage());
            //Logger.getLogger(RunSimulation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            out.println(ex.getMessage());
            //Logger.getLogger(RunSimulation.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            mw.setUIEnable(true);
        }
    }*/
}
