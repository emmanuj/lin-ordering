package com.acs.clemson.ordering.algo;

import com.acs.clemson.ordering.graph.Edge;
import com.acs.clemson.ordering.graph.Graph;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import org.apache.commons.collections4.iterators.PermutationIterator;

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
    public void solve(Graph g) {
        double[] xiVec = new double[g.size()];
        double minCost = Double.MAX_VALUE;
        List<Integer> soln = new ArrayList(g.size());
        for(int i=0;i<g.size();i++){
            soln.add(i);
        }
        
        PermutationIterator<Integer> itr = new PermutationIterator(soln);
        List<Integer> temp = itr.next();
        while(itr.hasNext()){
            computeXi(g, temp, xiVec);
            double cost = getCost(g, temp, xiVec);
            
            if(cost < minCost){
                minCost = cost;
                Collections.copy(soln, temp); //TODO: is there a better way to do this?
            }
            temp = itr.next();
        }
        
        computeXi(g, soln, xiVec);
        g.setXiVec(xiVec);
        g.setSoln(soln);
    }
    private double getCost(Graph g,List<Integer> soln, double []xiVec){
        double xSum = 0;
        for(int i=0;i<soln.size();i++){
            for(Edge e:g.adj(soln.get(i))){
                int v = e.getEndpoint(soln.get(i));
                if(i < v){
                    xSum+= e.getWeight() * Math.pow(xiVec[soln.get(i)] -xiVec[v], 2);
                }
            }
        }
        
        return xSum;
    }
    @Override
    public double getCost() {
        
        return 0;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private void initialize(Graph f, ArrayList<Integer> soln, double xiVec[], int prev_id[]){
        double yi[] = new double[f.size()];
        boolean placed[] = new boolean[f.size()];
        ArrayList<Integer> nodes = new ArrayList(f.size()-soln.size());
        //update the fine node with the xi from the coarse node.
        for(int i=0;i<soln.size();i++){
            yi[prev_id[soln.get(i)]] = xiVec[soln.get(i)];
            placed[prev_id[soln.get(i)]] = true;
        }
        
        for(int i=0;i<f.size();i++){
            if(!placed[i]){
                nodes.add(i);
            }
        }
        
        PlacedComparator comp = new PlacedComparator(placed, f);
        nodes.sort(comp);
        
        int p =0;
        int begin = 0;
        
        while(begin < nodes.size()){
            int u = nodes.get(begin);
            double sum = 0;
            double sum_prod =0;
            for(Edge e: f.adj(u)){
                sum+=e.getWeight();
                sum_prod+=yi[e.getEndpoint(u)] * e.getWeight();
            }
            
            yi[u] = sum/sum_prod; //TODO: Remember to test what happens with zero degree nodes here.
            placed[u] = true;
            
            begin++;
            p++;
            
            if((p%10==0) && begin < nodes.size()){
                Collections.sort(nodes.subList(begin, nodes.size()),comp); //TODO: Test this
            }
        }
        
        pump(f,yi);
    }
    private void pump(Graph f, double[] yi){
        ArrayList<Integer> nodes = new ArrayList(f.size());
        for(int i=0;i<f.size();i++ ){
            nodes.add(i);
        }
        
        nodes.sort((Integer o1, Integer o2) -> Double.compare(yi[o1], yi[o2]));
        
        for(int i=0;i<nodes.size();i++){
            double xi = 0.5 * f.volume(nodes.get(i));
            if(i>0){
                xi+= f.getXi(nodes.get(i-1)) + (0.5 * f.volume(nodes.get(i-1)));
            }
            f.setXi(nodes.get(i), xi);
        }
        
    }
    
    public void computeXi(Graph g, List<Integer> soln, double []xiVec){
        for(int i=1;i<soln.size();i++){
            double xi = 0.5 * g.volume(soln.get(i));
            if(i>0){
                xi+=xiVec[soln.get(i-1)] + (0.5 * g.volume(soln.get(i-1)));
            }
            xiVec[soln.get(i)] =xi;
        }
    }
    
    private class PlacedComparator implements Comparator<Integer>{

        private final boolean[] placed;
        private final Graph f;
        public PlacedComparator(boolean[] placed, Graph f){
            this.placed=placed;
            this.f =f;
        }
        @Override
        public int compare(Integer o1, Integer o2) {
            int u = o1;
            int v = o2;
            double rel_w1 =0;
            for(Edge e: f.adj(u)){
                if(placed[e.getEndpoint(u)]){
                    rel_w1+=e.getWeight();
                }
            }
            rel_w1/=f.weightedDegree(u);
            
            double rel_w2 =0;
            for(Edge e: f.adj(v)){
                if(placed[e.getEndpoint(v)]){
                    rel_w2+=e.getWeight();
                }
            }
            rel_w2/=f.weightedDegree(v);
            
            return Double.compare(rel_w1, rel_w2);
        }
    }
    
}
