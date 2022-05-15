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
public class Detection {
    public double x = 0;
    public double y = 0;
    public int background = 0;
    public int hit = 0;
    public int detectorId = 0;
    public Detection(double x, double y, int background, int hit, int detectorId) {
        this.x = x;
        this.y = y;
        this.background = background;
        this.hit = hit;
        this.detectorId = detectorId;
    }
}
