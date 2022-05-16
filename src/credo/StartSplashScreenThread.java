/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package credo;

/**
 *
 * @author Tomek
 */
public class StartSplashScreenThread extends Thread{
    MainWindow mw = null;
    public StartSplashScreenThread(MainWindow mw){
        this.mw = mw;
    }
    public void run(){
        SplashScreen ss = new SplashScreen(mw);
        ss.setModal(true);
        ss.setLocationRelativeTo(null);
        ss.setVisible(true);
    }
}
