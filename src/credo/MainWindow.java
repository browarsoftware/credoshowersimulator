/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package credo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;

//https://docs.oracle.com/javase/tutorial/uiswing/components/menu.html
//http://www.cs.fsu.edu/~jtbauer/cis3931/tutorial/ui/swing/menu.html
//https://jenkov.com/tutorials/java-concurrency/creating-and-starting-threads.html
/**
 *
 * @author Tomasz Hachaj
 */
public class MainWindow extends JFrame implements ActionListener, WindowListener{
    String appName = "CREDO showers simulator 1.0";
    public static final int messageColor = 0;
    public static final int warningColor = 1;
    public static final int errorColor = 2;
    public static final int infoColor = 3;
    public static final int specialColor = 4;
    
    
    JMenuBar mainMenu = null;
    JMenu fileMenu = null;
    JMenu simulationMenu = null;
    JMenu helpMenu = null;
    
    JMenuItem fileMenuClose = null;
    
    JMenuItem simulationMenuVirtualMachineInfo = null;
    JMenuItem simulationMenuPlanSummary = null;
    JMenuItem simulationMenuRun = null;
    
    JMenuItem helpMenuAbout = null;
    
    
    JPanel mainPanel = null;
    JTabbedPane mainTabbedPane = null;
    JPanel mainaPanelSimulation = null;
    
    public ColorPane simulationTextPane = null;
    JScrollPane simulationTextPaneScrollPane = null;
    JButton simulationClearTextPane = null;

    JPanel filePanel = null;
    JButton openExperimentFileButton = null;
    JTextField experimentFileNameTextField = null;
    
    JLabel vmMemoryLabel = null;
    
    MemoryUpdaterThread mut = null;
    
    String roundOffTo2DecPlaces(double val){
    return String.format("%.2f", val);
    }
    
    public void updatevmMemoryLabel() {
        String vmLabelString = "";
        VMInfo.update();
        String allocatedMemory = roundOffTo2DecPlaces((double)VMInfo.allocatedMemory / (1024.0 * 1024)) + "MB";
        String presumableFreeMemory = roundOffTo2DecPlaces((double)VMInfo.presumableFreeMemory / (1024.0 * 1024)) + "MB";
        vmLabelString = "VM memory usage: " + allocatedMemory + ", avilable memory: "  + presumableFreeMemory;
        vmMemoryLabel.setText(vmLabelString);
    }
    
    public void addTextToSimulationPane(int type, String text){
        Color textColor = Color.BLACK;
        if (type == messageColor)
            textColor = Color.BLACK;
        else if (type == warningColor)
            textColor = Color.MAGENTA;
        else if (type == errorColor)
            textColor = Color.RED;
        else if (type == infoColor)
            textColor = Color.BLUE;
        else if (type == specialColor)
            textColor = Color.GREEN;
        
        simulationTextPane.append(textColor, text);
    }
    //https://stackoverflow.com/questions/6068398/how-to-add-text-different-color-on-jtextpane
    
    
    
    public MainWindow(){
        super("Credo shower simulator v. 1.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        mainPanel = new JPanel(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        
        mainTabbedPane = new JTabbedPane();
        mainaPanelSimulation = new JPanel(new BorderLayout());
        mainTabbedPane.addTab("Simulation", mainaPanelSimulation);
        //mainaPanelSimulation.add(new JLabel("akukukuku!"));
        simulationTextPane = new ColorPane(){
            public boolean getScrollableTracksViewportWidth(){
                return getUI().getPreferredSize(this).width 
                    <= getParent().getSize().width;
            }
        };
        simulationClearTextPane = new JButton("Clear text");
        simulationClearTextPane.addActionListener(this);
        mainaPanelSimulation.add(simulationClearTextPane, BorderLayout.SOUTH);

        //simulationTextPane.setEditable(false);
        
        simulationTextPane.setContentType("text/html");
        simulationTextPaneScrollPane = new JScrollPane(simulationTextPane);
        simulationTextPaneScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        simulationTextPaneScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
        mainaPanelSimulation.add(simulationTextPaneScrollPane, BorderLayout.CENTER);
        
        mainPanel.add(mainTabbedPane, BorderLayout.CENTER);
        
        vmMemoryLabel = new JLabel(":-)");
        updatevmMemoryLabel();
        mainPanel.add(vmMemoryLabel, BorderLayout.SOUTH);
        
        filePanel = new JPanel(new FlowLayout());
        openExperimentFileButton = new JButton("Choose experiment file");
        openExperimentFileButton.addActionListener(this);
        filePanel.add(openExperimentFileButton);
        experimentFileNameTextField = new JTextField("                                  <NONE>                                  ");
        experimentFileNameTextField.setEditable(false);
        filePanel.add(experimentFileNameTextField);
        
        mainPanel.add(filePanel, BorderLayout.NORTH);
        //JLabel jl = new JLabel("akukukuku!");
        //getContentPane().add(jl, BorderLayout.CENTER);
        
        //Menu
        mainMenu = new JMenuBar();
        fileMenu = new JMenu("File");
        simulationMenu = new JMenu("Simulation");
        helpMenu = new JMenu("Help");
        
        //File menu
        fileMenuClose = new JMenuItem("Close");
        fileMenuClose.addActionListener(this);
        fileMenu.add(fileMenuClose);
        
        //Simulation menu
        simulationMenuVirtualMachineInfo = new JMenuItem("VM info");
        simulationMenuVirtualMachineInfo.addActionListener(this);
        simulationMenu.add(simulationMenuVirtualMachineInfo);
        
        simulationMenuPlanSummary = new JMenuItem("Simulation plan summary");
        simulationMenuPlanSummary.addActionListener(this);
        simulationMenu.add(simulationMenuPlanSummary);
        
        simulationMenuRun = new JMenuItem("Run");
        simulationMenuRun.addActionListener(this);
        simulationMenu.add(simulationMenuRun);
        
        //Help  menu
        helpMenuAbout = new JMenuItem("About");
        helpMenu.add(helpMenuAbout);
        helpMenuAbout.addActionListener(this);
                
        mainMenu.add(fileMenu);
        mainMenu.add(simulationMenu);
        mainMenu.add(helpMenu);
        this.setJMenuBar(mainMenu);
        pack();
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        addTextToSimulationPane(infoColor, "Welcome to " + appName + "\n");
        
        mut = new MemoryUpdaterThread(this);
        mut.start();
        
        StartSplashScreenThread ssst = new StartSplashScreenThread(this);
        ssst.start();
        /*
        addTextToSimulationPane("Akuku!\n", 1);
        addTextToSimulationPane("Akuku!\n", 2);
        addTextToSimulationPane("Akuku!\n", 3);
        addTextToSimulationPane("Akuku!\n", 4);
        */
        //setVisible(true);
    }
    public static void main(String[]args) {
        MainWindow mw = new MainWindow();
        mw.setVisible(true);
    }

    // https://localcoder.org/jtextarea-in-jscrollpane-wrapping-words-but-missing-letters
    private void showAbout() {
        addTextToSimulationPane(warningColor, "*********************************************************\n");
        addTextToSimulationPane(infoColor, appName + "\n");
        addTextToSimulationPane(warningColor, "Copyright ©  ");
        addTextToSimulationPane(errorColor, "Tomasz Hachaj");
        addTextToSimulationPane(infoColor, ", 2022\n");
        addTextToSimulationPane(warningColor, "Contact ");
        addTextToSimulationPane(specialColor, "tomekhachaj at o2.pl\n");
        //JOptionPane.showMessageDialog(this, "Author: Tomasz Hachaj, 2022", "About", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void setUIEnable(boolean enable) {
        mainMenu.setEnabled(enable);
        fileMenu.setEnabled(enable);
        simulationMenu.setEnabled(enable);
        helpMenu.setEnabled(enable);
    }
    
    private void runSimulation(){
        RunSimulation rs = new RunSimulation(this);
        rs.start();
    }
    
    private void vmInfo(){
        VMInfo.update();
        //String allocatedMemory = roundOffTo2DecPlaces((double)VMInfo.allocatedMemory / (1024.0 * 1024)) + "MB";
        //String presumableFreeMemory = roundOffTo2DecPlaces((double)VMInfo.presumableFreeMemory / (1024.0 * 1024)) + "MB";
        //vmLabelString = "VM memory usage: " + allocatedMemory + ", avilable memory: "  + presumableFreeMemory;
        //vmMemoryLabel.setText(vmLabelString);
        addTextToSimulationPane(warningColor, "*********************************************************\n");
        addTextToSimulationPane(infoColor, "VM information\n");
        addTextToSimulationPane(infoColor, "Available processors: " + Integer.toString(VMInfo.availableProcessors) + "\n");
        addTextToSimulationPane(infoColor, "Free memory: " + roundOffTo2DecPlaces((double)VMInfo.freeVMMemory / (1024.0 * 1024)) + " MB\n");
        addTextToSimulationPane(infoColor, "Max memory: " + roundOffTo2DecPlaces((double)VMInfo.maxVMMemory / (1024.0 * 1024)) + " MB\n");
        addTextToSimulationPane(infoColor, "Total memory: " + roundOffTo2DecPlaces((double)VMInfo.totalVMMemory / (1024.0 * 1024)) + " MB\n");
        addTextToSimulationPane(infoColor, "Allocate memory: " + roundOffTo2DecPlaces((double)VMInfo.allocatedMemory / (1024.0 * 1024)) + " MB\n");
        addTextToSimulationPane(infoColor, "Presumable free memory: " + roundOffTo2DecPlaces((double)VMInfo.presumableFreeMemory / (1024.0 * 1024)) + " MB\n");
        addTextToSimulationPane(infoColor, "File system info:\n");
        addTextToSimulationPane(infoColor, VMInfo.fileSystemInfo);
    }
    
    public void chooseExperimentFile() {
        JFileChooser fileChooser = new JFileChooser();
        //fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setCurrentDirectory(new File("."));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            experimentFileNameTextField.setText(selectedFile.getAbsolutePath());
            //System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        }
    }
    
    public void simmulationPlanSummary(){
        addTextToSimulationPane(warningColor, "*********************************************************\n");
        addTextToSimulationPane(warningColor, "Not yet implemented\n");
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == helpMenuAbout) {
            showAbout();
        }
        if (ae.getSource() == fileMenuClose) {
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
        if (ae.getSource() == simulationClearTextPane) {
            simulationTextPane.setText("");
        }
        
        if (ae.getSource() == simulationMenuVirtualMachineInfo) {
            vmInfo();
        }
        
        if (ae.getSource() == simulationMenuPlanSummary) {
            simmulationPlanSummary();
        }
        
        
        if (ae.getSource() == openExperimentFileButton) {
            chooseExperimentFile();
        }
        if (ae.getSource() == simulationMenuRun) {
            runSimulation();
        }
        
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void windowOpened(WindowEvent we) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void windowClosing(WindowEvent we) {
        //exit thread on window closing
        mut.endThread = true;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void windowClosed(WindowEvent we) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void windowIconified(WindowEvent we) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void windowDeiconified(WindowEvent we) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void windowActivated(WindowEvent we) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void windowDeactivated(WindowEvent we) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
