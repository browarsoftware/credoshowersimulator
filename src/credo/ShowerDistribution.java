/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package credo;

import java.io.PrintStream;
import java.util.ArrayList;

/**
 *
 * @author Tomasz Hachaj based on Python code of Łukasz Bibrzycki
 */
public class ShowerDistribution {
    public static PrintStream out = System.out;
    public static double r0=100;
    public static double s=1.3;
    public static double NRO=10000;

    public static double xmin = 0.5;
    public static double xmax = 5.0 * ShowerDistribution.r0;
    
    public static void setR0(double r0)
    {
        ShowerDistribution.r0 = r0;
        ShowerDistribution.xmax = 5.0 * ShowerDistribution.r0;
    }
    
    public static double ro(double r, double r0, double s, double N){
        double ret = N / (2 * Math.PI * r0 * r0) * gamma.gamma(4.5 - s) / (gamma.gamma(s) * gamma.gamma(4.5 - 2 * s)) * Math.pow(r / r0, s-2) * Math.pow(1 + r/r0, s-4.5);
        return ret;
    }
    
    public static short[][] generateCircle(int sizeX, int sizeY, double offsetX, double offsetY, double r0, double th, double phi)
    {
        short[][] circle = ArrayUtils.makeArrayShort(sizeX, sizeY, false);
        double r02sqrt = r0 * 100;
        double th_ = th * Math.PI / 180.0;
        double phi_ = phi * Math.PI / 180.0;
        
        double xx, yy, xx2, yy2;
        for (double x = -sizeX/2; x < sizeX/2; x+=0.5)
            for (double y = -sizeY/2; y < sizeY/2; y+=0.5) {
                if (Math.sqrt((x * x) + (y * y)) <= r02sqrt) {
                    //xx = x + offsetX;
                    //yy = y + offsetY;
                    //xx = x * Math.cos(phi_);
                    //yy = y * Math.sin(phi_);
                    xx = x;
                    yy = y;
                    
                    
                    
                    xx = xx / Math.cos(th_);
                    xx2 = xx;
                    yy2 = yy;
                    xx = (double)(sizeX / 2.0) +  xx2 * Math.cos(phi_) - yy2 * Math.sin(phi);
                    yy = (double)(sizeY / 2.0) +  xx2 * Math.sin(phi_) + yy2 * Math.cos(phi_);
                    if ((int)xx >= 0 && (int)xx < circle.length && (int)yy >= 0 && (int)yy < circle[0].length)
                    circle[(int)xx][(int)yy] = 1;
                }
            }
        return circle;
    }
    
    public static double[][] generateHit(int N, double th, double phi, boolean verbose)
    {
        ArrayList alret = generateHitArrayList(N, xmin, xmax, th, phi, verbose);
        double[]x = (double[])alret.get(0);
        double[]y = (double[])alret.get(1);
        double[][] ret = new double[N][];
        for (int a = 0; a < N; a++){
            ret[a] = new double[2];
            ret[a][0] = x[a];
            ret[a][1] = y[a];
        }
        return ret;
    }
    
    public static ArrayList generateHitArrayList(int N, double xmin, double xmax, double th, double phi, boolean verbose)
    {
        double []rndr = randomVariate(N, 0.5, 5*r0, verbose);
        
        double[]xl = new double[N];
        double[]yl = new double[N];
        double phi_in = 0;
        for (int a =0; a < N; a++)
        {
            phi_in = RandomUtils.nextDouble(0, 2 * Math.PI);
            xl[a] = rndr[a] * Math.cos(phi_in);
            yl[a] = rndr[a] * Math.sin(phi_in);
        }
        double th_ = th * Math.PI / 180.0;
        double phi_ = phi * Math.PI / 180.0;
        
        
        double x = 0;
        double y = 0;
        for (int a =0; a < N; a++)
        {
            xl[a] = xl[a] / Math.cos(th_);
            x = xl[a];
            y = yl[a];
            xl[a] = x * Math.cos(phi_) - y * Math.sin(phi_);
            yl[a] = x * Math.sin(phi_) + y * Math.cos(phi_);
        }
        ArrayList retal = new ArrayList();
        retal.add(xl);
        retal.add(yl);
        return retal;
    }
    
    public static double[] randomVariate(int n, double xmin, double xmax, boolean verbose)
    {
        long percent = (long)((double)n / 100.0);
        int percentCount = 0;
        int percentStep = 10;
        
        double[]pdf = new double[n];
        //xmin = 0; 
        //double df = (xmax - xmin) / (double)(n-1);
        double xminHelp = xmin; 
        xmin = 0; 
        double df = (xmax - xmin) / (double)(n-1);
        for (int  a = 0; a < n; a++)
        {
            //double xxxx = a * df + xmin;
            double x = (double)a * df + xmin;
            if (x < xminHelp) pdf[a] = ro(xminHelp, r0, s, NRO);
            else pdf[a] = ro(x, r0, s, NRO);
        }
        double pmin = 0;
        double pmax = Double.MIN_VALUE;
        for (int a = 0; a < pdf.length; a++)
            if (pmax < pdf[a])
               pmax =  pdf[a];
        //Counters  
        int naccept = 0;
        int ntrial = 0;
        double []xx = new double[n];
        //double []yy = new double[n];
        double x = 0;
        double y = 0;
        while (naccept < n) {
            x = RandomUtils.nextDouble(xmin, xmax);
            y = RandomUtils.nextDouble(pmin, pmax);
            if (y < ro(x, r0, s, NRO)){
                if (out != null && naccept % (percent * percentStep) == 0 && verbose) {
                    out.println(Integer.toString(percentCount) + "%");
                    percentCount+=percentStep;
                }
                xx[naccept] = x;
                naccept++;
            }
            ntrial++;
        }
        return xx;
    }

}
