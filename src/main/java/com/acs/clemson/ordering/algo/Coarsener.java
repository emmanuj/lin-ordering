package com.acs.clemson.ordering.algo;

import com.acs.clemson.ordering.graph.Graph;

/**
 *
 * @author Emmanuel John
 */
public interface Coarsener {
    public Graph coarsen(final Graph g);
}
