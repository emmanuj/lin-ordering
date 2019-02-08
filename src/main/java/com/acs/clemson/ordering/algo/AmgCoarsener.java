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
        GraphUtil.computeAlgebraicDistPar(g);
        if(Constants.DO_STABLE){
            int newSize = GraphUtil.doStableMatching(g, seeds, Constants.CAP, Constants.COMBINE);
            double diff = (double)(g.size() - newSize)/g.size();
            if(diff < Constants.BETA ){ // graph is no longer reducing so we combine methods
                if(Constants.COMBINE){
                    CoarseningUtil.computeAMGInterpolation(g, Constants.IO);
                }else if( Constants.COMBINE_STABLE ){
                    GraphUtil.doStableMatching(g, seeds, Constants.CAP2, Constants.COMBINE_STABLE);
                }
            }
            
        }else{
            CoarseningUtil.computeAMGInterpolation(g, Constants.IO);
        }
        Graph cg = GraphBuilder.buildByTriplesFast(g, seeds);
        return cg;
    }
}
