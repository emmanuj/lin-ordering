package com.acs.clemson.ordering.algo;

import com.acs.clemson.ordering.graph.Edge;
import com.acs.clemson.ordering.graph.Graph;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Emmanuel John
 */
public class CoarseningUtil {

    private static final int MU = 2;
    private static final float Q = 0.5f;
    private static final int MINIMUM = 10;

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

        final double thresh = MU * (total / g.size());
        for (int u = 0; u < fvols.size(); u++) {
            if (fvols.get(u) > thresh) {
                seeds.add(u);
                g.setCoarse(u, true);
            } else {
                fineNodes.add(u);
            }
        }

        //TODO: WE SKIP RECOMPUTING FV FOR NOW
        //sort by future volume
        fineNodes.sort((Integer o1, Integer o2) -> {
            return fvols.get(o1).compareTo(fvols.get(o2));
        });

        //make nodes seeds
        for (int i = fineNodes.size() - 1; i >= 0; i--) {
            int u = fineNodes.get(i);
            final double T = g.weightedCoarseDegree(u) / g.weightedDegree(u);
            if (T <= Q) {
                seeds.add(u);
                g.setCoarse(u, true);
            }
        }

        //Top up the number of seeds to ensure we get the minimum
        if (seeds.size() < MINIMUM) {
            int i = fineNodes.size() - 1;
            while (!g.isC(i) && i >= 0) {
                seeds.add(i);
                g.setCoarse(i, true);
                if (seeds.size() == MINIMUM) {
                    break;
                }
                --i;
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
                
//                System.out.println("Before filtering. [c neighbors="+g.weightedCoarseDegree(u)+"]: ");
                for (Edge e : g.cAdj(u)) {
                    //TODO: replace calls with g.setPij(...) and g.getPij(..)
                    e.setPij((e.getWeight() / g.weightedCoarseDegree(u)));
//                    System.out.print(e.getPij()+" ");
                }
//                System.out.println();
                
                //Sort By Pij (min to max)
                g.sortCoarseAdj(u, (Edge e1, Edge e2) -> {
                    int res = Double.compare(e1.getPij(), e2.getPij());
                    
                    
                    return res;
                });
                
                //now filter by interpolation (io)
                if(io < g.cDegree(u)){
                    int k = 0;
                    int lim = g.cDegree(u) - io;
                    double total = 0;
                    for (Edge e : g.cAdj(u)) {
                        if(k < lim){
                            e.setPij(0); //edge should be filtered
                        }else{
                            total+=e.getPij(); //edge should be filterd but we get the some for normalizing
                        }
                        k++;
                    }
                    k =0;
//                    System.out.println("After filtering: ");
                    for(Edge e: g.cAdj(u)){
                        if(k>=lim){
                            e.setPij(e.getPij()/total);
                        }
                        k++;
//                        System.out.print(e.getPij()+" ");
                    }
                    //System.out.println("\n");
                }
            }
        }

    }
}
