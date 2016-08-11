package com.acs.clemson.ordering.graph;

import java.util.ArrayList;

/**
 *
 * @author Emmanuel John
 */
public class GraphBuilder {
    public static Graph buildByTriples(Graph g, ArrayList<Integer> seeds){
        
        ArrayList<Edge> edges = new ArrayList(g.getEdgeCount());
        for(int i=0;i<g.size();i++){
            for(Edge e: g.adj(i)){
                int v = e.getEndpoint(i);
                if(i<v){
                    edges.add(e);
                }
            }
        }
        
        
        //compute coarse weights
        //edges.
        
        return null;
    }
}
