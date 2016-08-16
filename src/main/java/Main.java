
import com.acs.clemson.io.TrivialEdgeListReader;
import com.acs.clemson.ordering.algo.AmgCoarsener;
import com.acs.clemson.ordering.algo.TwoSumSolver;
import com.acs.clemson.ordering.graph.Graph;
import com.acs.clemson.ordering.util.StringParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
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
public class Main {

    private static final Options options = new Options();
    private static final HelpFormatter formatter = new HelpFormatter();
    public static void main(String[] args) {

        CommandLineParser parser = new DefaultParser();
        configureOptions();
        try {
            // parse the command line arguments
            CommandLine cmd = parser.parse(options, args);
            
            System.out.println("Reading graph ...");
            if(!cmd.hasOption('i') || !cmd.hasOption("first-node")){
                formatter.printHelp( "ant", options );
                System.exit(0);
            }
            String fileName = cmd.getOptionValue('i');
            Integer fn = StringParser.toInt(cmd.getOptionValue("first-node"));
            
            System.out.println(fileName + " - "+fn);
            //System.exit(0);
            
            TrivialEdgeListReader reader = new TrivialEdgeListReader(fileName, " ", fn);
            Graph g = reader.read();
            System.out.println("Done reading graph | " + g);

            Manager mgr = new Manager(AmgCoarsener.getInstance(), TwoSumSolver.getInstance());
            mgr.ML(g);
            
        } catch (ParseException exp) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
            // automatically generate the help statement
            formatter.printHelp( "ant", options );
            
        }
        
        
        

    }

    private static void configureOptions() {
        //you can add option like this
        Option file = new Option("i", true, "Graph input file. Only edgelist supported at the moment");
        options.addOption(file);
        //You can also add like this:
        options.addOption("h", "Usage description");
        options.addOption("", "first-node", true, "The node id of the first node");
        //see https://commons.apache.org/proper/commons-cli/usage.html for more

    }

    private static void graphReader(String fileName, Graph g) {
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach((String line)
                    -> {
                if (!line.contains("#")) {
                    String d[] = line.split(" "); //Find potential way to make this better for CPU and speed.
                    //System.out.println(d[0]+" "+d[1]);
                    g.addEdge(StringParser.toInt(d[0]), StringParser.toInt(d[1]), 1);
                }
            });
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
