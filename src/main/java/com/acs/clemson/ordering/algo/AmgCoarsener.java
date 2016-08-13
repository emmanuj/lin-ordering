package com.acs.clemson.ordering.algo;

import com.acs.clemson.ordering.graph.Graph;
import com.acs.clemson.ordering.graph.GraphBuilder;
import com.acs.clemson.ordering.util.Constants;
import java.util.ArrayList;

/**
 *
 * @author Emmanuel John
 */
public class AmgCoarsener implements Coarsener {
    //TODO: Make it a singleton
    private static AmgCoarsener instance;
    private AmgCoarsener(){
    }
    
    public static AmgCoarsener getInstance() {
        if(instance == null){
            instance = new AmgCoarsener();
        }
        return instance;
    }

    @Override
    public Graph coarsen(Graph g) {
        if(g == null) throw new UnsupportedOperationException("Graph cannot be null");
        //TODO Compute algebraic distance
        ArrayList<Integer> seeds = CoarseningUtil.selectSeeds(g);
        CoarseningUtil.computeAMGInterpolation(g, Constants.IO);
        
        return GraphBuilder.buildByTriples(g, seeds);
    }
    
}
