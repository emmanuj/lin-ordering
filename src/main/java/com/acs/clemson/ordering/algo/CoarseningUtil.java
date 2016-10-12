package com.acs.clemson.ordering.algo;

import com.acs.clemson.ordering.graph.Edge;
import com.acs.clemson.ordering.graph.Graph;
import com.acs.clemson.ordering.util.Constants;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Emmanuel John
 */
public class CoarseningUtil {
    private CoarseningUtil() {
    }

    public static ArrayList<Integer> selectSeeds(Graph g) {

        ArrayList<Integer> seeds = new ArrayList((int) 0.5 * g.size() + 10);
        ArrayList<Integer> fineNodes = new ArrayList((int) 0.5 * g.size() + 10);
        final ArrayList<Double> fvols = new ArrayList(Collections.nCopies(g.size(), 1.0));

        //calculate future volumes
        double total = 0;
        for (int u = 0; u < g.size(); u++) {
            double fv = g.volume(u);
            for (Edge e : g.adj(u)) {
                int v = e.getEndpoint(u);
                fv += g.volume(v) * (e.getWeight() / g.weightedDegree(v));
            }
            fvols.set(u, fv);
            total += fv;
        }
        
        final double thresh = Constants.MU * (total / g.size()*1.0);
        for (int u = 0; u < g.size(); u++) {
            if (fvols.get(u) > thresh) {
                seeds.add(u);
                g.setCoarse(u, true);
            } else {
                fineNodes.add(u);
            }
        }

        //sort by future volume
        fineNodes.sort((Integer o1, Integer o2) -> {
            int compVal = fvols.get(o2).compareTo(fvols.get(o1)); //using o2.compareTo(o1) for reverse order
            if(compVal == 0){
                return o1.compareTo(o2);
            }
            return compVal; //
        });
                
        //make nodes seeds
        for (int i = 0; i < fineNodes.size(); i++) {
            int u = fineNodes.get(i);
            if(g.degree(u) == 0){
                System.out.println("Contains zero degree node: "+u+" Level: "+ g.getLevel());
                continue; //avoid divide by zero below
            }
            final double T = g.weightedCoarseDegree(u) / g.weightedDegree(u);
            if (T <= Constants.Q) {
                seeds.add(u);
                g.setCoarse(u, true);
            }
        }
        
        if(seeds.size()<Constants.MIN_NODES){
            int k = seeds.size();
            for (int i = 0; i < fineNodes.size(); i++) {
                if(!g.isC(fineNodes.get(i)) && k < Constants.MIN_NODES){
                    seeds.add(fineNodes.get(i));
                    g.setCoarse(fineNodes.get(i), true);
                    k++;
                }
            }
        }
        
        return seeds;
    }

    public static void computeAMGInterpolation(Graph g, int io) {
        //io: interpolation order.
        //interpolation of -1 means the connections are not filtered by io

        //Compute Pij
        for (int u=0;u<g.size();u++) {
            if(!g.isC(u)){
                /*
                * Note: Although Pij for this edge u-v when set is equal to edge Pij(v,u)
                * The value 0 should be used instead when u is coarse or both u and v are coarse
                */
                
                for (Edge e : g.cAdj(u)) {
                    //TODO: replace calls with g.setPij(...) and g.getPij(..)
                    e.setPij((e.getWeight() / g.weightedCoarseDegree(u)));
                }
                
                //now filter by interpolation (io)
                //Sort By Pij (min to max)
                g.sortCoarseAdj(u, (Edge e1, Edge e2) -> {
                    int res = Double.compare(e1.getPij(), e2.getPij());
                    return res;
                });
                
                if(io < g.cDegree(u)){
                    int k = 0;
                    int lim = g.cDegree(u) - io;
                    double total = 0;
                    for (Edge e : g.cAdj(u)) {
                        if(k < lim){
                            e.setPij(0); //edge should be filtered
                        }else{
                            total+=e.getPij(); //edge should be filtered but we get the sum for normalizing
                        }
                        k++;
                    }
                    k =0;
                    for(Edge e: g.cAdj(u)){
                        if(k>=lim){
                            e.setPij(e.getPij()/total);
                        }
                        k++;
                    }
                }
                
            }
        }
    }
}
