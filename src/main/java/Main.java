
import com.acs.clemson.io.Config;
import com.acs.clemson.ordering.algo.AmgCoarsener;
import com.acs.clemson.ordering.algo.TwoSumSolver;

/**
 *
 * @author Emmanuel John
 */
public class Main {
    public static void main(String[] args) {
        
        Config.initialize(args);
        
        Manager mgr = new Manager(AmgCoarsener.getInstance(), TwoSumSolver.getInstance());
        mgr.ML();
    }
}
