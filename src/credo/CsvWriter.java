/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package credo;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 *
 * @author Tomasz Hachaj
 */
public class CsvWriter {
    public static void write(String fileName, double[]x, double[] y) {
        try (PrintWriter writer = new PrintWriter(fileName)) {
            writer.write("x,y\n");
            for (int a = 0; a < x.length; a++)
            {
                writer.write(Double.toString(x[a]) + "," + Double.toString(y[a]) + "\n");
            }
        } catch (FileNotFoundException e) {
          System.out.println(e.getMessage());
        }
  }
    
    public static void write(String fileName, double[][]x) {
        try (PrintWriter writer = new PrintWriter(fileName)) {          
            for (int b = 0; b < x[0].length; b++) {
                if (b > 0)
                    writer.write(",");
                writer.write("x" + Integer.toString(b));
            }
            writer.write("\n");
            
            for (int a = 0; a < x.length; a++)
            {
                for (int b = 0; b < x[0].length; b++) {
                    if (b > 0)
                        writer.write(",");
                    writer.write(Double.toString(x[a][b]));
                }
                writer.write("\n");
            }
        } catch (FileNotFoundException e) {
          System.out.println(e.getMessage());
        }
    }
}
