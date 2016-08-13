
import com.acs.clemson.ordering.algo.AmgCoarsener;
import com.acs.clemson.ordering.algo.TwoSumSolver;
import com.acs.clemson.ordering.graph.Graph;
import com.acs.clemson.ordering.util.StringParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 *
 * @author emmanuj
 */
public class Main {

    public static void main(String[] args) {

        Graph g = new Graph(62);//3997962

        //read file into stream, try-with-resources
        System.out.println("Reading graph ...");
        String fileName = "/Users/emmanuj/test_graphs/test_graphs/gd95c.edges";
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
        System.out.println("Done reading graph | " + g);
        
        
        Manager mgr = new Manager(AmgCoarsener.getInstance(), TwoSumSolver.getInstance());
        mgr.ML(g);
        
//        //System.out.println("Coarsening graph...");
//        
//        
//        ArrayList<Integer> seeds = CoarseningUtil.selectSeeds(g);
//        System.out.println("# Cnodes: "+ seeds.size());
//        
//        System.out.println("Commputing interpolation");
//        CoarseningUtil.computeAMGInterpolation(g, 2);
//        
//        //GraphUtil.computeAlgebraicDist(g);
//        System.out.println("Building tripples..");
//        GraphBuilder.buildByTriples(g, seeds).printGraph();
//        System.out.println("Done");
    }
}
