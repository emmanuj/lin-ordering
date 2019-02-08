package com.acs.clemson.ordering.algo;

import com.acs.clemson.ordering.graph.Graph;
import java.util.ArrayList;

/**
 *
 * @author emmanuj
 */
public class GraphStats {
    public static double averageDegree(Graph g){
        return g.getEdgeCount()/g.size() * 1.0;
    }
    public static int[] degreeStats(Graph g){
        ArrayList<Integer> degrees = new ArrayList();
        for(int i=0;i<g.size();i++){
            degrees.add(g.degree(i));
        }
        
        degrees.sort((Object o1, Object o2) -> {
            return ((Integer)o1).compareTo(((Integer)o2));
        });
        
        int m = (int) (degrees.size()/2.0);
        int median;
        if(degrees.size()%2!=0){
            m = (int) ((degrees.size()+1)/2.0);
            median = (degrees.get(m) + degrees.get(m+1))/2;
        }else{
            median = degrees.get(m);
        }
        
        return new int[]{degrees.get(0), median, degrees.get(degrees.size()-1)};
    }
    
    public static void print(Graph g){
        int stats[] = degreeStats(g);
        
        System.out.printf("Min: %d, Median: %d, Max: %d, Average: %.3f\n",stats[0],stats[1],stats[1],averageDegree(g));
    }
    
}
