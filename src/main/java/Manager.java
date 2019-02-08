
import com.acs.clemson.io.Config;
import static com.acs.clemson.io.Config.cmd;
import com.acs.clemson.io.DotGraphReader;
import com.acs.clemson.io.GraphReader;
import com.acs.clemson.io.TrivialEdgeListReader;
import com.acs.clemson.ordering.algo.Coarsener;
import com.acs.clemson.ordering.algo.Solver;
import com.acs.clemson.ordering.graph.Graph;
import com.acs.clemson.ordering.util.Constants;
import com.acs.clemson.ordering.util.StringParser;

/**
 *
 * @author Emmanuel John
 */
public class Manager {

    private final Coarsener coarsener;
    private final Solver solver;

    public Manager(Coarsener coarsener, Solver solver) {
        this.coarsener = coarsener;
        this.solver = solver;
    }

    public void ML() {
        System.out.println("Reading graph ...");
        String fileName = cmd.getOptionValue('i');
        if (!cmd.hasOption('i') || !cmd.hasOption("first-node")) {
            Config.printUsage();
            System.exit(0);
        }

        Integer fn = StringParser.toInt(cmd.getOptionValue("first-node"));

        GraphReader reader;
        String gname = "graph";
        switch (cmd.getOptionValue("format")) {
            case "edgelist":
                reader = new TrivialEdgeListReader(fileName, " ", fn, gname);
                break;
            case "dotgraph":
                reader = new DotGraphReader(fileName, " ", fn, gname);
                break;
            default:
                reader = new TrivialEdgeListReader(fileName, " ", fn, gname);
        }

        Graph g;
        if (cmd.hasOption("shuffle")) {
            g = reader.read(true);
        } else {
            g = reader.read(false);
        }
        
        long start = System.currentTimeMillis();
        recursiveML(g);
        long end = System.currentTimeMillis();
        
        System.out.println("Total runtime: "+(end - start) +" ms");
        /*ArrayDeque<GraphProperty> gstore = new ArrayDeque();
        //make it iterative
        //coarsen the graph
        
        
        while (g.size() > Constants.MIN_NODES) {
        System.out.println("=================================================================================================");
        g.print();
        GraphStats.print(g);
        System.out.println("=================================================================================================");
        GraphProperty gp = new GraphProperty();
        gp.extractProps(g);
        gstore.push(gp);
        SerializationUtil.save(g);
        g = coarsener.coarsen(g);
        }
        
        //solve it
        solver.solve(g);
        System.out.printf("Level: %d Cost: %.4e\n", g.getLevel(), solver.getCost(g));
        
        //uncoarsen it:
        while(!gstore.isEmpty()){
        GraphProperty gp = gstore.pop();
        Graph fineG = SerializationUtil.load(gp.getGraphId());
        fineG.print();
        solver.uncoarsen(fineG, g);//(fine,coarse)
        g = fineG; //becomes coarse of the next level
        }*/
    }
    
    /**
     * Recursively solve the graph
     * @param g Graph to solve 
     */
    public void recursiveML(Graph g) {
        if (g.size() <= Constants.MIN_NODES) {
            solver.solve(g);
        } else {
            Graph coarseG = coarsener.coarsen(g);
            recursiveML(coarseG);
            solver.uncoarsen(g, coarseG);
        }
    }
}
