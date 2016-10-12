
import com.acs.clemson.io.Config;
import static com.acs.clemson.io.Config.cmd;
import com.acs.clemson.io.DotGraphReader;
import com.acs.clemson.io.GraphReader;
import com.acs.clemson.io.TrivialEdgeListReader;
import com.acs.clemson.ordering.algo.AmgCoarsener;
import com.acs.clemson.ordering.algo.TwoSumSolver;
import com.acs.clemson.ordering.graph.Graph;
import com.acs.clemson.ordering.util.StringParser;

/**
 *
 * @author Emmanuel John
 */
public class Main {

    public static void main(String[] args) {
        
        //System.out.println("Reading graph ...");
        Config.initialize(args);
        /*
            Test options parser
        */
        //booleans
       /* if(cmd.hasOption("shuffle")){
            System.out.println("shuffle: [working]");
        }
        
        if(cmd.hasOption("stable")){
            System.out.println("stable: [working]");
        }
        
        //argument options
        for(Option opt: cmd.getOptions()){
            if(opt.getOpt() == null){
                System.out.println(opt.getLongOpt() + " : "+ opt.getValue());
            }else{
                System.out.println(opt.getOpt() + " : "+ opt.getValue());
            }
        }
        
        System.exit(0);*/
        
        /*  End options testing*/
        
        
        String fileName = cmd.getOptionValue('i');
        if(!cmd.hasOption('i') || !cmd.hasOption("first-node")){
            Config.printUsage();
            System.exit(0);
        }

        Integer fn = StringParser.toInt(cmd.getOptionValue("first-node"));

        //System.out.println(fileName + " - " + fn);
        
        GraphReader reader;

        switch(cmd.getOptionValue("format")){
            case "edgelist":
                reader = new TrivialEdgeListReader(fileName, " ", fn);
                break;
            case "dotgraph":
                reader = new DotGraphReader(fileName, " ", fn);
                break;
            default:
                reader = new TrivialEdgeListReader(fileName, " ", fn);
        }

        Graph g;
        if (cmd.hasOption("shuffle")) {
            g = reader.read(true);
        } else {
            g = reader.read(false);
        }

        //System.out.println("Done reading graph | " + g);

        Manager mgr = new Manager(AmgCoarsener.getInstance(), TwoSumSolver.getInstance());
        mgr.ML(g);
    }
}
