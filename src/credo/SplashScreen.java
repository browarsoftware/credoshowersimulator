/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package credo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Tomek
 */
public class SplashScreen extends JDialog implements ActionListener{
    private JPanel contentPane;
    BufferedImage backgroundImg = null;
    JButton closeButton = null;
    JLabel titleLabel = null;
    MainWindow mw = null;
    public SplashScreen(MainWindow mw){
        super(mw);
        this.mw = mw;
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        //setBounds(100, 100, 450, 300);
        setSize(640, 480);
        contentPane = new JPanel();
        File lutFile = new File("intro.png");
        try {
            backgroundImg = ImageIO.read(lutFile);
        } catch (IOException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
        contentPane = new JPanel() {  
            public void paintComponent(Graphics g) {
                g.drawImage(backgroundImg, 0, 0, this.getWidth(), this.getHeight(), this);  
            }  
        }; 
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));  
        contentPane.setLayout(new BorderLayout(0, 0));  
        closeButton = new JButton("Start");
        closeButton.addActionListener(this);
        closeButton.setOpaque(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        Font buttonFont = closeButton.getFont();
        int fontSizeToUse = 24;
        closeButton.setFont(new Font(buttonFont.getName(), Font.PLAIN, fontSizeToUse));
        closeButton.setForeground(Color.white);
        
        
        
        contentPane.add(closeButton, BorderLayout.SOUTH);
        
        titleLabel = new JLabel(mw.appName);
        Font labelFont = titleLabel.getFont();
        fontSizeToUse = 24;
        titleLabel.setFont(new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse));
        
        titleLabel.setForeground(Color.white);
        contentPane.add(titleLabel, BorderLayout.NORTH);
        
        setContentPane(contentPane);
        setUndecorated(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == closeButton) {
            this.dispose();
        }
    }
}
