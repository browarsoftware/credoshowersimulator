/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package credo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Tomek
 */
public class ExperimentConfiguration {
    int id = 0;
    double th = 0;
    double phi = 0;
    double offsetX = 0;
    double offsetY = 0;
    int N = 100000;
    double sampleSizeX = 1;
    double sampleSizeY = 1;
    double regionSizeX = 12800;
    double regionSizeY = 12800;
    double backgroundMezonsPerSquaredCentimeter = 0.0166666666666;
    boolean calculateBackground = true;
    boolean calculateHit = true;
    String detectorsFile = "detectors0.conf";
    String outputDir = "results";
    
    public ExperimentConfiguration() {}
    public ExperimentConfiguration(String dataToParse, String myPath){
        String[]splitted = dataToParse.split(",");
        id = Integer.parseInt(splitted[0]);
        th = Double.parseDouble(splitted[1]);
        phi = Double.parseDouble(splitted[2]);
        offsetX = Double.parseDouble(splitted[3]);
        offsetY = Double.parseDouble(splitted[4]);
        N = Integer.parseInt(splitted[5]);
        //Always 1 cm^2, different values not supported yet
        sampleSizeX = 1;//Double.parseDouble(splitted[6]);
        sampleSizeY = 1;//Double.parseDouble(splitted[7]);
        regionSizeX = Double.parseDouble(splitted[8]);
        regionSizeY = Double.parseDouble(splitted[9]);
        backgroundMezonsPerSquaredCentimeter = Double.parseDouble(splitted[10]);
        if (Integer.parseInt(splitted[11]) == 0) calculateBackground = false;
        else calculateBackground = true;
        if (Integer.parseInt(splitted[12]) == 0) calculateHit = false;
        else calculateHit = true;
        detectorsFile = myPath + "/" + splitted[13];
        outputDir = myPath + "/" + splitted[14];
    }
    
    public static ArrayList<ExperimentConfiguration> ParseExperiment(String fileName) throws FileNotFoundException, IOException{
        ArrayList<ExperimentConfiguration>ret = new ArrayList<ExperimentConfiguration>();
        int lineCount = 0;
        BufferedReader reader;
        reader = new BufferedReader(new FileReader(fileName));
        String line = reader.readLine();
        while (line != null) {
            if (lineCount == 0) {}//skip header
            else {
                //String aaaa = new File(fileName).getAbsolutePath();
                //aaaa = new File(fileName).getCanonicalPath();
                //aaaa = new File(fileName).getPath();
                String myPath = new File(fileName).getParent();
                ExperimentConfiguration ec = new ExperimentConfiguration(line, myPath);
                ret.add(ec);
            }
            line = reader.readLine();
            lineCount++;
        }
        reader.close();
        return ret;
    }
}
