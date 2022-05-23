/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package credo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 *
 * @author Tomek
 */
public class ImageDetailsData {
    double minValue = 0;
    double maxValue = 0;
    //long hitCountInCircle = 0;
    ExperimentConfiguration ec = null;
    
    //public ImageDetailsData(double minValue, double maxValue, long hitCountInCircle, ExperimentConfiguration ec){
    public ImageDetailsData(double minValue, double maxValue, ExperimentConfiguration ec){
        this.minValue = minValue;
        this.maxValue = maxValue;
        //this.hitCountInCircle = hitCountInCircle;
        this.ec = ec;
    }
    
    public void saveImageDetails(String fileName) throws FileNotFoundException, IOException {
        File fout = new File(fileName);
	FileOutputStream fos = new FileOutputStream(fout);
	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        String resString = "";
        
        
        resString += "id=" + Integer.toString(ec.id) + "\n";
        resString += "min=" + Double.toString(minValue) + "\n";
        resString += "max=" + Double.toString(maxValue) + "\n";
        //resString += "hitCountInCircle=" + Long.toString(hitCountInCircle) + "\n";
        
        double cmPerPixelX = ec.sampleSizeX / ec.outputImageScale; 
        double cmPerPixelY = ec.sampleSizeY / ec.outputImageScale;
        
        resString += "cmPerPixelX=" + Double.toString(cmPerPixelX) + "\n";
        resString += "cmPerPixelY=" + Double.toString(cmPerPixelY) + "\n";
        resString += "th=" + Double.toString(ec.th) + "\n";
        resString += "phi=" + Double.toString(ec.phi) + "\n";
        resString += "r0=" + Double.toString(ec.r0) + "\n";
        resString += "offsetX=" + Double.toString(ec.offsetX) + "\n";
        resString += "offsetY=" + Double.toString(ec.offsetY) + "\n";
        resString += "N=" + Double.toString(ec.N) + "\n";
        resString += "sampleSizeX=" + Double.toString(ec.sampleSizeX) + "\n";
        resString += "sampleSizeY=" + Double.toString(ec.sampleSizeY) + "\n";
        resString += "regionSizeX=" + Double.toString(ec.regionSizeX) + "\n";
        resString += "regionSizeY=" + Double.toString(ec.regionSizeY) + "\n";
        resString += "backgroundMezonsPerSquaredCentimeter=" + Double.toString(ec.backgroundMezonsPerSquaredCentimeter) + "\n";
        resString += "calculateBackground=" + Boolean.toString(ec.calculateBackground) + "\n";
        resString += "calculateHit=" + Boolean.toString(ec.calculateHit) + "\n";
        resString += ec.detectorsFile + "\n";
        resString += ec.outputDir + "\n";
        resString += "outputImageScale=" + Double.toString(ec.outputImageScale) + "\n";
        bw.write(resString);
	bw.close();
    }
}
