package com.acs.clemson.ordering.graph;

import com.acs.clemson.ordering.util.Constants;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import java.util.ArrayList;

/**
 *
 * @author Emmanuel John
 */
public class GraphBuilder {
     public static Graph buildByTriplesFast(Graph g, ArrayList<Integer> seeds) {
        
        ArrayList<Edge> edges = new ArrayList(g.getEdgeCount());
        for (int i = 0; i < g.size(); i++) {
            for (Edge e : g.adj(i)) {
                int v = e.getEndpoint(i);
                if (i < v) {
                    edges.add(e);
                }
            }
        }
        
        Int2DoubleMap[] coarseEdges = new Int2DoubleOpenHashMap[g.size()];
        for(int i=0;i<seeds.size();i++){
            coarseEdges[seeds.get(i)] = new Int2DoubleOpenHashMap();
        }

         //compute coarse weights
         edges.forEach((e) -> {
             int v = e.getV();
             int u = e.getU();
             if (g.isC(u) && g.isC(v)) {
                 addToTable(coarseEdges, u, v, e.getWeight());
             } else if (!g.isC(u) && g.isC(v)) { //F-C
                 for (Edge cEdge : g.cAdj(u)) {//get coarse neighbors of the fine edges
                     if(cEdge.getPij() == 0) continue;
                     int nb = cEdge.getEndpoint(u);
                     if (nb != v) {
                         double w = cEdge.getPij() * e.getWeight();
                         if(w !=0){
                             addToTable(coarseEdges, v, nb, w);
                         }
                     }
                 }
             } else if (g.isC(u) && !g.isC(v)) {//C-F
                 for (Edge cEdge : g.cAdj(v)) {//get coarse neighbors of the fine edges
                     if(cEdge.getPij() == 0) continue;
                     int nb = cEdge.getEndpoint(v);
                     if (nb != u) {
                         double w = cEdge.getPij() * e.getWeight();
                         if(w !=0){
                             addToTable(coarseEdges, u, nb, w);
                         }
                     }
                 }
             } else {//F-F
                 for (Edge cEdgeU : g.cAdj(u)) {//get coarse neighbors of the fine edges
                     if(cEdgeU.getPij() == 0) continue;
                     int uNb = cEdgeU.getEndpoint(u);
                     for (Edge cEdgeV : g.cAdj(v)) {
                         if(cEdgeV.getPij() == 0) continue;
                         int vNb = cEdgeV.getEndpoint(v);
                         if (uNb != vNb) {
                             double w = cEdgeU.getPij() * e.getWeight() * cEdgeV.getPij();
                             if(w !=0){
                                 addToTable(coarseEdges, uNb, vNb, w);
                             }
                         }
                     }
                 }
             }
         });
        
        Graph cg = new Graph(g,coarseEdges,seeds);
        //System.out.println("Before: ");
        //cg.print();
        cg = cg.filterEdges(Constants.ETA * Math.pow(0.9, round(Math.log(cg.getLevel()+1), 5)));
        //System.out.println("After: ");
        //cg.print();
        return cg;
    }
    /**
     * 
     * @param x The number to round
     * @param dp number of decimal places
     * @return x rounded to the desired number of decimal places
     */
    public static double round(double x, int dp){
        int i =1;
        int c = dp;
        while(c > 0){
            i*=10;
            c--;
        }
        
        return (double) Math.round(x * i) / i;
    }
    private static void addToTable(Int2DoubleMap[] tbl, int r, int c, double val) {
        int mn = Math.min(r, c);//the smaller of the two nodeIds
        int mx = Math.max(r, c);//the largest of the two nodeId
        tbl[mn].put(mx, tbl[mn].get(mx)+val);
    }
}
