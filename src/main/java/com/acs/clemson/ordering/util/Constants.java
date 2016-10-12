package com.acs.clemson.ordering.util;

import org.apache.commons.cli.CommandLine;

/**
 *
 * @author emmanuj
 */
public class Constants {
    public static final double EPSILON=0.00000001;
    public static int MIN_NODES = 9;
    public static int IO = 10;
    public static int WIN_SIZE = 20; //#nodes in window
    public static int K1 = 20;//compatibles
    public static int K2 = 20;//GSs
    public static int K3 = 10;//refinements
    public static int K4 = 20;//node minimization
    public static int K5 = 5;// number steps to left and then to right for node minimization
    public static int MU = 2;
    public static int CAP = 2; //Capacity for stable matching
    public static float Q = 0.5f;
    public static int R = 10;
    public static int K = 30;
    public static float ALFA = 0.5f;
    public static boolean DO_STABLE = false; //used for stable matching.
    
    public static void initialize(CommandLine cmd){
        
        //boolean options
        if(cmd.hasOption("stable")){
            DO_STABLE = true;
        }
        
        //options with arguments
        if(cmd.hasOption("cap")){
            CAP = Integer.parseInt(cmd.getOptionValue("cap"));
        }
        if(cmd.hasOption("e")){
            MIN_NODES = Integer.parseInt(cmd.getOptionValue("e"));
        }
        if(cmd.hasOption("r")){
            IO = Integer.parseInt(cmd.getOptionValue("r"));
        }
        if(cmd.hasOption("w")){
            WIN_SIZE = Integer.parseInt(cmd.getOptionValue("w"));
        }
        if(cmd.hasOption("mu")){
            MU = Integer.parseInt(cmd.getOptionValue("mu"));
        }
        if(cmd.hasOption("q")){
            Q = Float.parseFloat(cmd.getOptionValue("q"));
        }
        if(cmd.hasOption("num-comp")){
            K1 = Integer.parseInt(cmd.getOptionValue("num-comp"));
        }
        if(cmd.hasOption("num-gs")){
            K2 = Integer.parseInt(cmd.getOptionValue("num-gs"));
        }
        if(cmd.hasOption("num-ref")){
            K3 = Integer.parseInt(cmd.getOptionValue("num-ref"));
        }
        if(cmd.hasOption("num-node")){
            K4 = Integer.parseInt(cmd.getOptionValue("num-node"));
        }
        if(cmd.hasOption("num-steps")){
            K5 = Integer.parseInt(cmd.getOptionValue("num-node"));
        }
        
    }
    
}
