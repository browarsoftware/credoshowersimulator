/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package credo;

import static credo.ArrayUtils.out;
import java.io.PrintStream;

/**
 *
 * @author Tomasz Hachaj
 */
public class BackgroundDistribution {
    
    public static PrintStream out = System.out;
    
    public static void initializeBackground(short[][]array, long backgroundMionsCount, boolean verbose) {
        long percent = (long)((double)backgroundMionsCount / 100.0);
        int percentCount = 0;
        int percentStep = 10;
        int x, y;
        for (long counter = 0; counter < backgroundMionsCount; counter++){
            if (out != null && counter % (percent * percentStep) == 0 && verbose)
            {
                out.println(Integer.toString(percentCount) + "%");
                percentCount+=percentStep;
            }
            x = RandomUtils.rand.nextInt(array.length);
            y = RandomUtils.rand.nextInt(array[0].length);
            array[x][y] = (short) (array[x][y] + 1);
        }
    }
}
