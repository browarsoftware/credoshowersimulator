/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package credo;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Tomek
 */
public class main2 {
    public static void main(String args[]) throws SimulatorException, IOException{
        ArrayList<ExperimentConfiguration> al = ExperimentConfiguration.ParseExperiment("experiments/0/parameters.conf");
        Experiment exp = new Experiment(al);
        exp.runExperiments(System.out);
    }
}
