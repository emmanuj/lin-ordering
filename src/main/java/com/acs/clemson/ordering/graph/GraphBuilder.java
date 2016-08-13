package com.acs.clemson.ordering.graph;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Emmanuel John
 */
public class GraphBuilder {
    public static Graph buildByTriples(Graph g, ArrayList<Integer> seeds) {
        ArrayList<Edge> edges = new ArrayList(g.getEdgeCount());
        for (int i = 0; i < g.size(); i++) {
            for (Edge e : g.adj(i)) {
                int v = e.getEndpoint(i);
                if (i < v) {
                    edges.add(e);
                }
            }
        }
        
        BiMap<Integer, Integer> idMap = HashBiMap.create();
        int id =0;
        for(Integer seed:seeds){
            idMap.put(seed, id++);
        }
        //Table<Long, Long, Double> coarseEdges = HashBasedTable.create(seeds.size(), seeds.size());
        List< HashMap<Integer,Float>> coarseEdges = Collections.synchronizedList(new ArrayList(seeds.size()));
        seeds.parallelStream().forEach((_item) -> {
            coarseEdges.add(new HashMap());
        });
        
        //compute coarse weights
        for(Edge e:edges){
            int v = e.getV();
            int u = e.getU();
            if (g.isC(u) && g.isC(v)) {
                addToTable(coarseEdges, idMap.get(u), idMap.get(v), e.getWeight());
            } else if (!g.isC(u) && g.isC(v)) { //F-C
                for (Edge cEdge : g.cAdj(u)) {//get coarse neighbors of the fine edges
                    int nb = cEdge.getEndpoint(u);
                    if (nb != v) {
                        addToTable(coarseEdges, idMap.get(v), idMap.get(nb), cEdge.getPij() * e.getWeight());
                    }
                }
            } else if (g.isC(u) && !g.isC(v)) {//C-F
                for (Edge cEdge : g.cAdj(v)) {//get coarse neighbors of the fine edges
                    int nb = cEdge.getEndpoint(v);
                    if (nb != u) {
                        addToTable(coarseEdges, idMap.get(u), idMap.get(nb), cEdge.getPij() * e.getWeight());
                    }
                }
            } else {//F-F
                for (Edge cEdgeU : g.cAdj(u)) {//get coarse neighbors of the fine edges
                    int uNb = cEdgeU.getEndpoint(u);
                    for (Edge cEdgeV : g.cAdj(v)) {
                        int vNb = cEdgeV.getEndpoint(v);
                        if (uNb != vNb) {
                            addToTable(coarseEdges, idMap.get(uNb), idMap.get(vNb), cEdgeU.getPij() * e.getWeight() * cEdgeV.getPij());
                        }
                    }
                }
            }
        }

        edges = null;
        return new Graph(g,coarseEdges,idMap,seeds.size());
    }

    private static void addToTable(Table<Long, Long, Double> tbl, int r, int c, double val) {
        long mn = Math.min(r, c);//the smaller of the two nodeIds
        long mx = Math.min(r, c);//the largest of the two nodeId
        if (tbl.contains(mn, mx)) {
            tbl.put(mn, mx, tbl.get(mn, mx) + val);
        } else {
            tbl.put(mn, mx, val);
        }
    }
    
    private static void addToTable(List<HashMap<Integer, Float>> tbl, int r, int c, double val) {
        int mn = Math.min(r, c);//the smaller of the two nodeIds
        int mx = Math.max(r, c);//the largest of the two nodeId
        Float obj = tbl.get(mn).get(mx);
        float w = (obj==null?0:obj) + (float)val;
        tbl.get(mn).put(mx,w);
    }


    
}
