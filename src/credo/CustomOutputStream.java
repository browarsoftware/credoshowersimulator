/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package credo;

import java.awt.Color;
import java.io.OutputStream;

/**
 *
 * @author Tomek
 */
public class CustomOutputStream extends OutputStream {
    //private ColorPane textArea;
    private MainWindow mw = null;

    public CustomOutputStream(MainWindow mw) {
        this.mw = mw;
    }

    @Override
    public void write(int b){
        // redirects data to the text area
        //textArea.append(Color.BLACK, String.valueOf((char)b));
        mw.addTextToSimulationPane(mw.messageColor, String.valueOf((char)b));
        /*
        // scrolls the text area to the end of data
        textArea.setCaretPosition(textArea.getDocument().getLength());
        // keeps the textArea up to date
        textArea.update(textArea.getGraphics());
        */
    }
}
