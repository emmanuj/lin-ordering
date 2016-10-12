package com.acs.clemson.ordering.algo;

import com.acs.clemson.ordering.graph.Graph;
import com.acs.clemson.ordering.graph.GraphBuilder;
import com.acs.clemson.ordering.util.Constants;
import com.acs.clemson.ordering.util.GraphUtil;
import java.util.ArrayList;

/**
 *
 * @author Emmanuel John
 */
public class AmgCoarsener implements Coarsener {
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
        ArrayList<Integer> seeds = CoarseningUtil.selectSeeds(g);
        GraphUtil.computeAlgebraicDist(g);
        
        if(Constants.DO_STABLE){
            //System.out.println("Using stable with capacity= "+Constants.CAP);
            GraphUtil.doStableMatching(g, seeds, Constants.CAP);
        }else{
            //System.out.println("Using amg");
            CoarseningUtil.computeAMGInterpolation(g, Constants.IO);
        }
        return GraphBuilder.buildByTriples(g, seeds);
    }
    
}
