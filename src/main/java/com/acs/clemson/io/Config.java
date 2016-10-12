package com.acs.clemson.io;

import com.acs.clemson.ordering.util.Constants;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author emmanuj
 */
public class Config {
    private static final Options options = new Options();
    private static final HelpFormatter formatter = new HelpFormatter();
    public static CommandLine  cmd;
    
    public static void initialize(String args[]){
        try {
            final CommandLineParser parser = new DefaultParser();
            configureOptions();
            
            // parse the command line arguments
            cmd = parser.parse(options, args);
            Constants.initialize(cmd);
            
        } catch (ParseException ex) {
            printUsage();
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void printUsage(){
        formatter.printHelp( "run.sh", options );
    }
    
    
    private static void configureOptions() {
        //you can add option like this
        
        Option file = new Option("i","input", true, "Graph input file. Only edgelist, metis, and .graph formats supported at the moment");
        options.addOption(file);
        //You can also add like this:
        options.addOption("h", "Usage description");
        options.addOption("f", "first-node", true, "The node id of the first node");
        options.addOption(Option.builder("e")
                .longOpt("min-nodes")
                .desc("The minimum number of nodes before coarsening is returned")
                .hasArg()
                .required(false)
                .build());
        options.addOption(Option.builder("r")
                .argName("interpolation")
                .desc("The interpolation order used to filter the edges (default=1)")
                .hasArg()
                .required(false)
                .build());
        options.addOption(Option.builder("w")
                .argName("Window size")
                .longOpt("win-size")
                .desc("Specify the window size for window minimization (default=10)")
                .hasArg()
                .required(false)
                .build());
        
        options.addOption(Option.builder("c")
                .argName("Capacity")
                .longOpt("cap")
                .desc("Specify the maximum number of matches for each coarse node during stable matching")
                .hasArg()
                .required(false)
                .build());
        
        options.addOption(Option.builder()
                .longOpt("num-comp")
                .desc("The number of iterations of compatible relaxation")
                .hasArg()
                .required(false)
                .build());
        options.addOption(Option.builder()
                .longOpt("num-gs")
                .desc("The of iterations of gauss-Seidel relaxations")
                .hasArg()
                .required(false)
                .build());
        options.addOption(Option.builder()
                .longOpt("num-ref")
                .desc("The number of iterations of refinements for window minimization")
                .hasArg()
                .required(false)
                .build());
        options.addOption(Option.builder()
                .longOpt("num-node")
                .desc("The interpolation order used to filter the edges")
                .hasArg()
                .required(false)
                .build());
        options.addOption(Option.builder()
                .longOpt("num-steps")
                .desc("The number of steps to the left and to the right for node by node minimization")
                .hasArg()
                .required(false)
                .build());
        options.addOption(Option.builder()
                .longOpt("mu")
                .desc("Extra filter used in seed creation (default=2)")
                .hasArg()
                .required(false)
                .build());
        options.addOption(Option.builder("q")
                .desc("Extra filter used in seed creation (default=0.5)")
                .hasArg()
                .required(false)
                .build());
        
        options.addOption(Option.builder("t")
                .longOpt("format")
                .desc("The format of the input graph. Currently available: edgelist, dotgraph")
                .hasArg()
                .required(true)
                .build());
        
        options.addOption(Option.builder()
                .longOpt("shuffle")
                .desc("Shuffle the nodes of the graph ")
                .hasArg(false)
                .required(false)
                .build());
        options.addOption(Option.builder()
                .longOpt("stable")
                .desc("Use stable matching during coarsening")
                .hasArg(false)
                .required(false)
                .build());
        
        //see https://commons.apache.org/proper/commons-cli/usage.html for more

    }
}
