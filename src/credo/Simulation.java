/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package credo;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Tomasz Hachaj
 */
public class Simulation {
    public double sample_size_x = 1;//in centimeters
    public double sample_size_y = 1;//in centimeters
    public double region_size_x = 128 * 100;//in centimeters
    public double region_size_y = 128 * 100;//in centimeters
    public double backgroundMezonsPerSquaredCentimeter = 1.0 / 60;//in centimeters per second
    
    public static int grid_size_x = 0;//in centimeters
    public static int grid_size_y = 0;//in centimeters
    
    public boolean verbose = true;
    public PrintStream out = System.out; 
    
    double th = 0;
    double phi =0;
    double offsetX = 0;
    double offsetY = 0;
    int N = 100000;
    //public long hitCountInCircle = -1;
    
    BufferedImage alut = null;
    
    public Simulation(){
        this.grid_size_x = (int)(region_size_x / sample_size_x);
        this.grid_size_y = (int)(region_size_y / sample_size_y);
    }
    
    public Simulation(double sample_size_x, double sample_size_y, 
        double region_size_x, double region_size_y,
        double backgroundMezonsPerSquaredCentimeter){
        this.sample_size_x = sample_size_x;
        this.sample_size_y = sample_size_y;
        this.region_size_x = region_size_x;
        this.region_size_y = region_size_y;
        this.backgroundMezonsPerSquaredCentimeter = backgroundMezonsPerSquaredCentimeter;
        
        this.grid_size_x = (int)(region_size_x / sample_size_x);
        this.grid_size_y = (int)(region_size_y / sample_size_y);
    }
    
    public void init(double th, 
            double phi, double offsetX, double offsetY, int N, boolean verbose){
        this.th = th;
        this.phi = phi;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.N = N;
        init();
    }
    
    public short[][]background = null;
    public short[][]hit = null;
    //public short[][]detectors = null;
    public double[][]hitdata = null;
    //short[][] circle = null;
    
    public void init(){
        long backgroundMionsCount = (int)(region_size_x * region_size_y * backgroundMezonsPerSquaredCentimeter / (sample_size_x * sample_size_y));
        //Arrays initialization
        if (verbose && out != null) out.println("Assigning memory for noise array");
        background = ArrayUtils.makeArrayShort(grid_size_x, grid_size_y, verbose);
        if (verbose && out != null) out.println("Assigning memory for hit array");
        hit = ArrayUtils.makeArrayShort(grid_size_x, grid_size_y, verbose);
        //System.out.println("Assigning memory detector array");
        //detectors = ArrayUtils.makeArrayShort(grid_size_x, grid_size_y, verbose);
        
        //Background
        if (verbose && out != null) out.println("Initializating background");
        BackgroundDistribution.initializeBackground(background, backgroundMionsCount, verbose);
        //Hit
        if (verbose && out != null) out.println("Hit generation");       
        hitdata = ShowerDistribution.generateHit(N, th, phi, verbose);
        
        //circle = ShowerDistribution.generateCircle(grid_size_x, grid_size_y, offsetX, offsetY, ShowerDistribution.r0, th, phi);
    }
    
    public void runSimulation() throws SimulatorException {
        if (background == null || hit == null || hitdata == null) 
            throw new SimulatorException("Simulation not initialized. Run init() method first.");
        if (verbose && out != null) out.println("Assigning hit to array");
        //in centimeters
        double offsetXScale = offsetX / sample_size_x;
        double offsetYScale = offsetY / sample_size_y;
        ArrayUtils.assignHitToArray(hit, hitdata, offsetXScale, offsetYScale);
        /*
        if (circle != null) {
            long count = 0;
            for (int a = 0; a < hit.length; a++)
                for (int b = 0; b < hit.length; b++) {
                    if (hit[a][b] > 0 && circle[a][b] > 0) {
                        count++;
                    }
                }
            hitCountInCircle = count;
        }*/
    }
    
    ArrayList<Detector> detectors = new ArrayList<Detector>();
    ArrayList<Detection> detections = new ArrayList<Detection>();

    public void loadDetectors(String fileName, boolean checkOverlapping) throws SimulatorException{
        if (verbose && out != null) out.println("Loading detectors");
        detectors.clear();
        int lineCount = 0;
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();
            while (line != null) {
                if (lineCount == 0) {}//skip header
                else {
                    String[] splitStr = line.split(",");
                    Detector d = new Detector(Integer.parseInt(splitStr[0]), 
                            Double.parseDouble(splitStr[1]), 
                            Double.parseDouble(splitStr[2]), 
                            Double.parseDouble(splitStr[3]), 
                            Double.parseDouble(splitStr[4]));
                    detectors.add(d);                    
                    if (checkOverlapping) {
                        throw new SimulatorException("Detector id: " + Integer.toString(d.id) 
                                + " overlaps with detector " + Integer.toString(1234) );
                    }
                }
                line = reader.readLine();
                lineCount++;
            }
            reader.close();
        } catch (IOException e) {
                e.printStackTrace();
        }
        
        //detectors.add(new Detector(0, 0, 0, 5, 5));
    }
    
    public void runDetection() {
        if (verbose && out != null) out.println("Running detection");
        detections.clear();
        for (Detector d : detectors) {
            double x_left = d.x - (d.w / 2.0);
            double x_right = d.x + (d.w / 2.0);
            
            double y_bottom = d.y - (d.h / 2.0);
            double y_top = d.y + (d.h / 2.0);
            
            int x_start = (int)(((double)hit.length / 2.0) + 100.0 * x_left);
            int x_stop = (int)(((double)hit.length / 2.0) + 100.0 * x_right);
            
            int y_start = (int)(((double)hit.length / 2.0) + 100.0 * y_bottom);
            int y_stop = (int)(((double)hit.length / 2.0) + 100.0 * y_top);
            
            for (int a = x_start; a < x_stop; a++)
                for (int b = y_start; b < y_stop; b++) {
                    if (a > 0 && a < hit.length && b > 0 && b < hit[0].length) {
                        if (hit[a][b] > 0 || background[a][b] > 0) {
                            double x = (a - ((double)hit.length / 2.0)) / 100.0;
                            double y = (b - ((double)hit.length / 2.0)) / 100.0;
                            detections.add(new Detection(x, y, background[a][b], hit[a][b], d.id));
                            d.backgroundCount +=  background[a][b];
                            d.hitCount +=  hit[a][b];
                        }
                    }
                }
        }
    }

    public void saveDetectors(String fileName) throws FileNotFoundException, IOException {
        if (verbose && out != null) out.println("Saving detectors");
        File fout = new File(fileName);
	FileOutputStream fos = new FileOutputStream(fout);
	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        bw.write("id,x,y,w,h,background,hit\n");
        for (Detector d : detectors) {
            bw.write(Integer.toString(d.id) + "," + Double.toString(d.x) + "," + Double.toString(d.y) + "," 
                    + Double.toString(d.w) + "," + Double.toString(d.h) + ","
                    + Integer.toString(d.backgroundCount) + "," + Integer.toString(d.hitCount) + "\n");
        }
	bw.close();
    }
    
    public void saveDetections(String fileName) throws FileNotFoundException, IOException {
        if (verbose && out != null) out.println("Saving detections");
        File fout = new File(fileName);
	FileOutputStream fos = new FileOutputStream(fout);
	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        bw.write("x,y,background,hit,detectorId\n");
        for (Detection d : detections) {
            bw.write(Double.toString(d.x) + "," + Double.toString(d.y) + "," + Integer.toString(d.background)
                + "," + Integer.toString(d.hit) + "," + Integer.toString(d.detectorId) + "\n");
        }
	bw.close();
    }
    
    public BufferedImage getDetectorImgage(double scale, boolean logarithmic, double[]minMax){
        //Debug image
        //double factor = 0.1;
        int xxx = (int)(hit.length);
        int yyy = (int)(hit[0].length);
        int[][]resampledData = ArrayUtils.makeArrayInt(xxx, yyy, false);
        //int[][]fooData = ArrayUtils.makeArrayInt(xxx, yyy, false);
        for (Detector d : detectors) {
            double x_left = d.x - (d.w / 2.0);
            double x_right = d.x + (d.w / 2.0);
            
            double y_bottom = d.y - (d.h / 2.0);
            double y_top = d.y + (d.h / 2.0);
            
            int x_start = (int)(((double)hit.length / 2.0) + 100.0 * x_left);
            int x_stop = (int)(((double)hit.length / 2.0) + 100.0 * x_right);
            
            int y_start = (int)(((double)hit.length / 2.0) + 100.0 * y_bottom);
            int y_stop = (int)(((double)hit.length / 2.0) + 100.0 * y_top);
            
            for (int a = x_start; a < x_stop; a++)
                for (int b = y_start; b < y_stop; b++) {
                    if (a > 0 && a < hit.length && b > 0 && b < hit[0].length) {
                        resampledData[a][b] = d.hitCount + d.backgroundCount;
                    }
                }
        }
        int[][]resampledData2 = ArrayUtils.resampleDataInt(resampledData, scale);
        BufferedImage img = ArrayUtils.generateImage(resampledData2, null, logarithmic, alut, minMax);
        return img;
    }
    
    
    public BufferedImage getDetectionImgage(double scale, boolean logarithmic, double[]minMax){
        //Debug image
        //double factor = 0.1;
        int xxx = (int)(scale * hit.length);
        int yyy = (int)(scale * hit[0].length);
        int[][]resampledData = ArrayUtils.makeArrayInt(xxx, yyy, false);
        int[][]fooData = ArrayUtils.makeArrayInt(xxx, yyy, false);
        for (Detection d : detections) {
            int x = (int)(((double)hit.length / 2.0) + 100.0 * d.x);
            int y = (int)(((double)hit.length / 2.0) + 100.0 * d.y);
            int x2 = (int)(x * scale);
            int y2 = (int)(y * scale);
            
            resampledData[x2][y2] = resampledData[x2][y2] + d.background + d.detectorId;
        }
        if (alut == null) {
            File lutFile = new File("alut.png");
            try {
                alut = ImageIO.read(lutFile);
            } catch (IOException ex) {
                if (out != null)
                    out.println(ex.toString());
            }
        }
        BufferedImage img = ArrayUtils.generateImage(resampledData, fooData, logarithmic, alut, minMax);
        return img;
        /*
        if (verbose) System.out.println("Resampling hit data");
        int[][]resampledHitData = ArrayUtils.resampleData(hit, scale);
        if (verbose) System.out.println("Resampling background data");
        int[][]resampledBackgroundData = ArrayUtils.resampleData(background, scale);
        if (verbose) System.out.println("Generating image");
        if (alut == null) {
            File lutFile = new File("alut.png");
            try {
                alut = ImageIO.read(lutFile);
            } catch (IOException ex) {
                Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        BufferedImage img = ArrayUtils.generateImage(resampledHitData, resampledBackgroundData, logarithmic, alut);
        return img;*/
    }
    
    public BufferedImage getHitImgage(double scale, boolean logarithmic, double[]minMax){
        //Debug image
        //double factor = 0.1;
        if (verbose && out != null) out.println("Resampling hit data");
        int[][]resampledHitData = ArrayUtils.resampleData(hit, scale);
        if (verbose && out != null) out.println("Resampling background data");
        int[][]resampledBackgroundData = ArrayUtils.resampleData(background, scale);
        if (verbose && out != null) out.println("Generating image");
        if (alut == null) {
            File lutFile = new File("alut.png");
            try {
                alut = ImageIO.read(lutFile);
            } catch (IOException ex) {
                Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        BufferedImage img = ArrayUtils.generateImage(resampledHitData, resampledBackgroundData, logarithmic, alut, minMax);
        return img;
    }
    /*
    public BufferedImage getCircleImgage(double scale){
        //Debug image
        //double factor = 0.1;
        if (verbose && out != null) out.println("Resampling hit data");
        int[][]resampledCircleData = ArrayUtils.resampleData(circle, scale);
        if (verbose && out != null) out.println("Resampling background data");
        int[][]resampledBackgroundData = ArrayUtils.resampleData(background, scale);
        if (verbose && out != null) out.println("Generating image");
        if (alut == null) {
            File lutFile = new File("alut.png");
            try {
                alut = ImageIO.read(lutFile);
            } catch (IOException ex) {
                Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        BufferedImage img = ArrayUtils.generateImage(resampledCircleData, null, false, null, null);
        return img;
    }*/
}
