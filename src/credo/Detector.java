/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package credo;

/**
 *
 * @author Tomasz Hachaj
 */
public class Detector {
    public int id = 0;
    public double x = 0;//in meters
    public double y = 0;//in meters
    public double w = 0;//in meters
    public double h = 0;//in meters
    public int hitCount = 0;
    public int backgroundCount = 0;

    public Detector(int id, double x, double y, double w, double h) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }
}
