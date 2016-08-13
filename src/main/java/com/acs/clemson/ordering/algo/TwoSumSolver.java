package com.acs.clemson.ordering.algo;

import com.acs.clemson.ordering.graph.Graph;

/**
 *
 * @author emmanuj
 */
public class TwoSumSolver implements Solver{
    private static TwoSumSolver solver;
    private TwoSumSolver(){}
    public static TwoSumSolver getInstance(){
        if(solver== null){
            solver = new TwoSumSolver();
        }
        return solver;
    }
    @Override
    public void uncoarsen(Graph fine, Graph coarse) {
        System.out.println("Uncoarsening Level: "+fine.getLevel());
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void solve(Graph fine) {
        System.out.println("Solve called");
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
