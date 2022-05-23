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
 * @author Tomasz Hachaj
 */
public class ExperimentConfiguration {
    int id = 0;
    double th = 0;
    double phi = 0;
    double r0 = 100;
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
    double outputImageScale = 0;
    
    public ExperimentConfiguration() {}
    public ExperimentConfiguration(String dataToParse, String myPath){
        String[]splitted = dataToParse.split(",");
        id = Integer.parseInt(splitted[0]);
        th = Double.parseDouble(splitted[1]);
        phi = Double.parseDouble(splitted[2]);
        r0 = Double.parseDouble(splitted[3]);
        offsetX = Double.parseDouble(splitted[4]);
        offsetY = Double.parseDouble(splitted[5]);
        N = Integer.parseInt(splitted[6]);
        //Always 1 cm^2, different values not supported yet
        sampleSizeX = 1;//Double.parseDouble(splitted[7]);
        sampleSizeY = 1;//Double.parseDouble(splitted[8]);
        regionSizeX = Double.parseDouble(splitted[9]);
        regionSizeY = Double.parseDouble(splitted[10]);
        backgroundMezonsPerSquaredCentimeter = Double.parseDouble(splitted[11]);
        if (Integer.parseInt(splitted[12]) == 0) calculateBackground = false;
        else calculateBackground = true;
        if (Integer.parseInt(splitted[13]) == 0) calculateHit = false;
        else calculateHit = true;
        detectorsFile = myPath + "/" + splitted[14];
        outputDir = myPath + "/" + splitted[15];
        outputImageScale = Double.parseDouble(splitted[16]);
    }
    
    public static boolean validateExperimentFile(String fileName, MainWindow mw){
        boolean result = true;
        try {
            ArrayList<ExperimentConfiguration>al = ParseExperiment(fileName);
            mw.addTextToSimulationPane(MainWindow.infoColor,"Number of hits: " + al.size() + "\n");
            double memory = 0;
            ArrayList<ExperimentConfiguration> notExistingDetectorsFiles = new ArrayList<ExperimentConfiguration>();
            for (ExperimentConfiguration experimentConfiguration : al) {
                double memoryEstimation = experimentConfiguration.regionSizeX * experimentConfiguration.regionSizeY / (experimentConfiguration.sampleSizeX * experimentConfiguration.sampleSizeX);
                if (memory < memoryEstimation)
                    memory = memoryEstimation;
                if (!new File(experimentConfiguration.detectorsFile).exists())
                    notExistingDetectorsFiles.add(experimentConfiguration);
            }
            //two arrays of shorts = 2.0 * 2.0
            String memUsage = mw.roundOffTo2DecPlaces(2.0 * 2.0 * memory / (1024.0 * 1024)) + " MB";
            mw.addTextToSimulationPane(MainWindow.infoColor,"Minimal expected memory usage: " + memUsage + "\n");
            
            
            VMInfo.update();
            if (VMInfo.presumableFreeMemory > (long)(4 * memory)) {
                mw.addTextToSimulationPane(MainWindow.infoColor,"There *SHOULD* be enough memory on VM to run experiment.\n");
            } else {
                String memAvil  = mw.roundOffTo2DecPlaces(VMInfo.presumableFreeMemory / (1024.0 * 1024));
                mw.addTextToSimulationPane(MainWindow.errorColor,"Not enough VM memory to run experiment (approximately " + (memAvil) + " MB is avilable).\n");
                result = false;
            }
            
            
            if (notExistingDetectorsFiles.size() == 0)
                mw.addTextToSimulationPane(MainWindow.infoColor,"All detectos configuration files exists\n");
            else {
                mw.addTextToSimulationPane(MainWindow.errorColor,"Following detectos configuration files does not exists:\n");
                for (ExperimentConfiguration notExistingDetectorsFile : notExistingDetectorsFiles) {
                    mw.addTextToSimulationPane(MainWindow.errorColor,"Experiment id: " + notExistingDetectorsFile.id + ", file name: " + notExistingDetectorsFile.detectorsFile + "\n");
                }
                result = false;
            }
        } catch (IOException ex) {
            mw.addTextToSimulationPane(MainWindow.errorColor,"Experiment file cannot be oppened. JAVA returned exception:\n");
            mw.addTextToSimulationPane(MainWindow.errorColor,ex.toString() + "\n");
            result = false;
        } catch (ArrayIndexOutOfBoundsException aioube) {
            mw.addTextToSimulationPane(MainWindow.errorColor,"Parser error, it seems that experiment file is in the wrong format. JAVA returned exception:\n");
            mw.addTextToSimulationPane(MainWindow.errorColor,aioube.toString() + "\n");
            result = false;
        } catch (NumberFormatException nfe) {
            mw.addTextToSimulationPane(MainWindow.errorColor,"Parser error, it seems that experiment file is in the wrong format. JAVA returned exception:\n");
            mw.addTextToSimulationPane(MainWindow.errorColor,nfe.toString() + "\n");
            result = false;            
        }
        return result;
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
