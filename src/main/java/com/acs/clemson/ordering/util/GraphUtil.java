package com.acs.clemson.ordering.util;

import com.acs.clemson.ordering.graph.Edge;
import com.acs.clemson.ordering.graph.Graph;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author emmanuj
 */
public class GraphUtil {
    public static void computeAlgebraicDist(Graph g) {
        for (int r = 0; r < Constants.R; r++) {
            double x_vec[] = new double[g.size()];
            //generate the random numbers
            for (int j = 0; j < x_vec.length; j++) {
                x_vec[j] = randomInRange(-0.5, 0.5);
            }

            for (int k = 0; k < Constants.K; k++) {
                double c_vec[] = new double[g.size()];
                for (int u = 0; u < g.size(); u++) {
                    double prod_sum = 0;
                    double sum = 0;
                    for (Edge e : g.adj(u)) {
                        int v = e.getEndpoint(u);
                        sum = e.getWeight();
                        prod_sum += e.getWeight() * x_vec[v];
                    }

                    c_vec[u] = Constants.ALFA * x_vec[u];
                    if (sum != 0) {//avoid divide by 0.
                        c_vec[u] += (1 - Constants.ALFA) * (prod_sum / sum);
                    }
                }
                x_vec = c_vec;
            }

            //rescale 
            double min = minElement(x_vec);
            double max = maxElement(x_vec);
            for (int i = 0; i < x_vec.length; i++) {
                double a = x_vec[i] - min;
                double b = max - x_vec[i];
                x_vec[i] = (0.5 * (a - b)) / (a + b);
            }
            
            //compute the algebraic distance
            for (int u = 0; u < g.size(); u++) {
                for (Edge e : g.adj(u)) {
                    int v = e.getEndpoint(u);
                    if (u < v) {
                        double ad = 1 / ((Math.abs(x_vec[u] - x_vec[v])) + Constants.EPSILON);
                        if (ad < e.getAlgebraicDist()) {
                            e.setAlgebraicDist(ad);
                        }
                    }
                }
            }
        }
    }
    public static void computeAlgebraicDistPar(Graph g) {
        ExecutorService es = Executors.newFixedThreadPool(Constants.R);
        //ExecutorService es = Executors.newSingleThreadExecutor();
        
        for (int r = 0; r < Constants.R; r++) {
            es.execute(() -> {
                doAlgDist(g);
            });
        }
        es.shutdown();
        try {
            es.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            Logger.getLogger(GraphUtil.class.getName()).log(Level.SEVERE, "Interupted before end of execution", ex);
        }
    }
    
    public static void doAlgDist(Graph g){
        double x_vec[] = new double[g.size()];
        //generate the random numbers
        for (int j = 0; j < x_vec.length; j++) {
            x_vec[j] = randomInRange(-0.5, 0.5);
        }

        for (int k = 0; k < Constants.K; k++) {
            double c_vec[] = new double[g.size()];
            for (int u = 0; u < g.size(); u++) {
                double prod_sum = 0;
                double sum = 0;
                for (Edge e : g.adj(u)) {
                    int v = e.getEndpoint(u);
                    sum = e.getWeight();
                    prod_sum += e.getWeight() * x_vec[v];
                }

                c_vec[u] = Constants.ALFA * x_vec[u];
                if (sum != 0) {//avoid divide by 0.
                    c_vec[u] += (1 - Constants.ALFA) * (prod_sum / sum);
                }
            }
            x_vec = c_vec;
        }

        //rescale 
        double min = minElement(x_vec);
        double max = maxElement(x_vec);
        for (int i = 0; i < x_vec.length; i++) {
            double a = x_vec[i] - min;
            double b = max - x_vec[i];
            x_vec[i] = (0.5 * (a - b)) / (a + b);
        }

        //compute the algebraic distance
        for (int u = 0; u < g.size(); u++) {
            for (Edge e : g.adj(u)) {
                int v = e.getEndpoint(u);
                if (u < v) {
                    double ad = 1 / ((Math.abs(x_vec[u] - x_vec[v])) + Constants.EPSILON);
                    synchronized(g){
                        if (ad < e.getAlgebraicDist()) {
                            e.setAlgebraicDist(ad);
                        }
                    }
                }
            }
        }
    }
    public static int doStableMatching(Graph g, ArrayList<Integer> seeds, int cap, boolean isCombined){
        
        //renumber fine and coarse nodes from 0 to n-1
        int fCount = 0;
        int cCount = 0;
        final BiMap<Integer, Integer> cNodeIdx = HashBiMap.create(g.size());
        final BiMap<Integer, Integer> fNodeIdx = HashBiMap.create(g.size());
        
        for(int i=0;i<g.size();i++){
            //Create new ids for fine node and coarse nodes
            if(g.isC(i)){
                cNodeIdx.put(i, cCount++); //nodeid -> label
            }else{
                fNodeIdx.put(i, fCount++); //nodeid -> label
            }
        }
        
        final List<List<Integer> > cPref = new ArrayList(cNodeIdx.size());
        final List< Map<Integer, Double> > fWeights = new ArrayList(fNodeIdx.size());
        
        for(int i=0;i<g.size();i++){
            
            double sumalg =0;
            for(Edge e: g.adj(i)){
                sumalg+=e.getAlgebraicDist();
            }
               
            if(g.isC(i)){
                cPref.add(new ArrayList());
                
                List<Edge> nbs = new ArrayList();
                int k = cNodeIdx.get(i);
                for(Edge e: g.adj(i)){
                    if(!g.isC(e.getEndpoint(i))){//collect the fine neighbors
                        nbs.add(e);
                    }
                }
                
                final double sum = sumalg;
                //rank them by relative alg. distance
                nbs.sort((Edge e1, Edge e2) -> {
                    return Double.compare(e2.getAlgebraicDist()/sum, e1.getAlgebraicDist()/sum); //sort neighbors in descending order
                });
                
                //add their labels to i's preference list
                for(Edge e: nbs){
                    cPref.get(k).add(fNodeIdx.get(e.getEndpoint(i)));
                }
                
            }else{
                Map<Integer, Double> weights = new HashMap();
                for(Edge e: g.cAdj(i)){ //collect coarse neighbor
                    weights.put(cNodeIdx.get(e.getEndpoint(i)), e.getAlgebraicDist()/sumalg);
                }
                fWeights.add(weights);
            }
        }
        int [] matches = matchNodes(cPref, fWeights, cap); // returns the matches of fine nodes. 
        //Note that a fine node can only be matched with one seed node while seed nodes can have multiple fine nodes
        BiMap<Integer, Integer> fNodeIdInv = fNodeIdx.inverse();
        BiMap<Integer, Integer> cNodeIdInv = cNodeIdx.inverse();
        int unmatchedFineCount = 0;
        for(int i =0;i<matches.length; i++){
            if(matches[i] == -1){// No match found
                unmatchedFineCount++;
            }
        }
        
        int newSize = unmatchedFineCount+seeds.size();
        
        double diff = (double)(g.size() - newSize)/g.size();
        if(!isCombined || (diff >= Constants.BETA)){
            computeStableMatchInterpolation(g, matches, fNodeIdInv, cNodeIdInv, seeds);
        }
        
        return newSize;
    }
    private static void computeStableMatchInterpolation(Graph g, int [] matches, BiMap<Integer, Integer> fNodeIdInv,
            BiMap<Integer, Integer> cNodeIdInv, ArrayList<Integer> seeds){
        for(int i =0;i<matches.length; i++){
            int nodeid = fNodeIdInv.get(i);
            if(matches[i] == -1){// No match found
                //Make into seed node
                seeds.add(nodeid);
                g.setCoarse(nodeid, true);
            }else{//match exists
                int coarseMatchId = cNodeIdInv.get(matches[i]);
                for(Edge e: g.adj(nodeid)){
                    if(e.getEndpoint(nodeid) == coarseMatchId){ //fine nodes are matched to only one coarse node by default.
                        e.setPij(1.0); //TODO: change to g.setPij
                        break; //the rest Pij is 0 by default
                    }
                }
            }
        }
    }
    
    private static int[] matchNodes(List<List<Integer>>cnodes, List< Map<Integer, Double> >fWeights, int capacity){
        final ArrayDeque<Integer> unmatched = new ArrayDeque(cnodes.size()); //all c nodes are unmatched at first
        final int[] matching = new int[fWeights.size()]; // matching of fine -> coarse, -1 by default
        final int[] p_counter = new int[cnodes.size()]; //store proposal count for c nodes
        final int[] caps = new int[cnodes.size()]; //store capacity of each coarse node
        //Map<Integer, Integer> cnodePos
        
        for(int i=0;i<cnodes.size();i++){
            unmatched.add(i);
            caps[i] = cnodes.get(i).size();
            if(capacity != -1 ){
                caps[i]= Math.min(caps[i], capacity);
            }
        }
        
        //set all matching to -1 by default
        for(int i=0;i<fWeights.size();i++){
            matching[i] = -1;
        }
        
        while(!unmatched.isEmpty()){
            int a = unmatched.getFirst();
            int num_nbs = cnodes.get(a).size();
            if(num_nbs == 0 || p_counter[a] == num_nbs || caps[a] ==0 ){
                unmatched.removeFirst();
            }else{
                int w = cnodes.get(a).get(p_counter[a]);
                int cur_match = matching[w];
                
                if(cur_match == -1){
                    matching[w] = a;
                    caps[a]--;
                }else if(fWeights.get(w).get(cur_match) < fWeights.get(w).get(a)){ //w prefers a to current mate
                    if(caps[cur_match] == 0){ //increase capacity of formaer mate
                        unmatched.add(cur_match);// add divorced partner back to the end of the queue
                    }
                    
                    caps[cur_match]++;// this guy just lost his partner. He's available
                    
                    matching[w] = a; //w gets matched to a
                    caps[a]--;
                } //else b rejects a
                
                //increment proposal counter
                p_counter[a]++;
            }
            
        }
        
        return matching;
    }

    public static double minElement(double a[]) {
        return Arrays.stream(a).parallel().min().getAsDouble();
    }

    public static double maxElement(double a[]) {
        return Arrays.stream(a).parallel().max().getAsDouble();
    }

    public static double randomInRange(double min, double max) {
        double range = max - min; //
        double scaled = ThreadLocalRandom.current().nextDouble() * range;
        double shifted = scaled + min;
        return shifted; // == (rand.nextDouble() * (max-min)) + min;
    }

}
