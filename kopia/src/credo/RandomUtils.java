/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package credo;

import java.util.Random;

/**
 *
 * @author Tomek
 */
public class RandomUtils {
    public static Random rand = new Random();
    
    public static double nextDouble(double xmin, double xmax)
    {
        double x = rand.nextDouble();
        double ret_x = (xmax - xmin) * x + xmin;
        return ret_x;
    }
}
