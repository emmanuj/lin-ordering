import com.acs.clemson.ordering.algo.Coarsener;
import com.acs.clemson.ordering.algo.Solver;
import com.acs.clemson.ordering.graph.Graph;
import com.acs.clemson.ordering.util.Constants;
/**
 *
 * @author emmanuj
 */
public class Manager {
    private final Coarsener coarsener;
    private final Solver solver;
    //TODO: Make singleton
    public Manager(Coarsener coarsener, Solver solver) {
        this.coarsener = coarsener;
        this.solver = solver;
    }
    
    public void ML(Graph g){
        System.out.println("Coarsening...");
        g.print();
        if(g.size() <= Constants.MIN_NODES){
            solver.solve(g);
        }else{
            Graph coarseG = coarsener.coarsen(g);
            ML(coarseG);
            solver.uncoarsen(g, coarseG);
        }
    }
}
