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
public class DebugTimeSeries {
    public static void main(String[]args) throws FileNotFoundException, IOException {
        double r0=100;
        double s=1.3;
        double NR0=10000;
        double y = 0;
        double xminHelp = 0.5;
        //randomVariate(N, 0.5, 5*r0, verbose);
        
        File file = new File("pdf.csv");
	FileOutputStream fos = new FileOutputStream(file);
	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        bw.write("r,s1.0,s1.1,s1.2,s1.3,s1.4,s1.5,s1.6\n");
        for (double r = 0; r < 1000; r+=0.1) {
            //System.out.println(r);
            bw.write(Double.toString(r));
            for (double ss = 1; ss < 1.65; ss += 0.1) {
                if (r < xminHelp) y = ShowerDistribution.ro(xminHelp, r0, ss, NR0);
                else y = ShowerDistribution.ro(r, r0, ss, NR0);
                bw.write("," + Double.toString(y));
            }
            bw.write("\n");
        }
        bw.close();
    }
}
