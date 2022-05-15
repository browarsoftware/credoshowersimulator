/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package credo;

import static credo.MainWindow.errorColor;

/**
 *
 * @author Tomek
 */
public class MemoryUpdaterThread extends Thread{
    MainWindow mw = null;
    public boolean endThread = false;
    public MemoryUpdaterThread(MainWindow mw){
        this.mw = mw;
    }
    public void run(){
        while(!endThread){
            try {
                mw.updatevmMemoryLabel();
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                mw.addTextToSimulationPane(errorColor, "Memory updater thread stopped working.");
            }
        }
    }

    
}
