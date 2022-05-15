/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package credo;

import java.io.File;

/**
 *
 * @author Tomek
 */
public class VMInfo {
    // https://stackoverflow.com/questions/12807797/java-get-available-memory
    public static int availableProcessors = 0;
    public static long freeVMMemory = 0;
    public static long maxVMMemory = 0;
    public static long totalVMMemory = 0;
    public static long allocatedMemory = 0;
    public static long presumableFreeMemory = 0;
    public static String fileSystemInfo = "";
    public static void update() {
        //System.out.println("Available processors (cores): " + 
        availableProcessors = Runtime.getRuntime().availableProcessors();
        /* Total amount of free memory available to the JVM */
        //System.out.println("Free memory (bytes): " + 
        freeVMMemory = Runtime.getRuntime().freeMemory();
        maxVMMemory = Runtime.getRuntime().maxMemory();
        /* Maximum amount of memory the JVM will attempt to use */
        //System.out.println("Maximum memory (bytes): " + 
        //(maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory));

        /* Total memory currently in use by the JVM */
        //System.out.println("Total memory (bytes): " + 
        totalVMMemory = Runtime.getRuntime().totalMemory();

        allocatedMemory = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
        presumableFreeMemory = Runtime.getRuntime().maxMemory() - allocatedMemory;
        
        /* Get a list of all filesystem roots on this system */
        File[] roots = File.listRoots();

        /* For each filesystem root, print some info */
        fileSystemInfo = "";
        for (File root : roots) {
            fileSystemInfo += "File system root: " + root.getAbsolutePath() + "\n";
            fileSystemInfo += "Total space (bytes): " + root.getTotalSpace() + "\n";
            fileSystemInfo += "Free space (bytes): " + root.getFreeSpace() + "\n";
            fileSystemInfo += "Usable space (bytes): " + root.getUsableSpace() + "\n";
        }
    }
}
