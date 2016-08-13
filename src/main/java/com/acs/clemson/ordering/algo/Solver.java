package com.acs.clemson.ordering.algo;

import com.acs.clemson.ordering.graph.Graph;

/**
 *
 * @author emmanuj
 */
public interface Solver {
    public void uncoarsen(Graph fine, Graph coarse);
    public void solve(Graph fine);
}
