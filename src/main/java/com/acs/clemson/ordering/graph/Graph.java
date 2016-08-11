package com.acs.clemson.ordering.graph;

import java.util.Comparator;

/**
 *
 * @author Emmanuel John
 */
public class Graph{
    private final int SIZE;
    private final EdgeList[] nodes;
    private final float[] volumes;
    private final double[] weightedSum;
    private final double[] weightedCoarseSum;
    private final int[] nodeDegrees;
    private final int[] c_nodeDegrees;
    private int edge_count=0;
    private final boolean[] coarse;
    private final EdgeList[] coarse_neighbors;
    public Graph(int SIZE){
        this.SIZE =SIZE;
        nodes = new EdgeList[SIZE];
        coarse_neighbors = new EdgeList[SIZE];
        
        volumes = new float[SIZE];
        coarse = new boolean[SIZE];
        
        //initialize data
        weightedSum = new double[SIZE];
        weightedCoarseSum = new double[SIZE];
        nodeDegrees= new int[SIZE];
        c_nodeDegrees = new int[SIZE];
        
        for(int i=0;i<SIZE;i++){
            nodes[i]=new EdgeList();
            coarse_neighbors[i] = new EdgeList();
            volumes[i]=1;
        }
    }
    
    public void addEdge(int u, int v, double w){
        //graph is undirected
        //In order to traverse edges in order such that u < v. We store edge u,v such that u<v
        Edge e=null;
        if(u<v){
            e = new Edge(u,v,w);
        }else if(u>v){
            e = new Edge(v,u,w);
        }else{
            throw new UnsupportedOperationException("Self loops not allowed in graph"); //TODO: Need a graph validation routine
        }
        
        nodes[u].add(e);
        nodes[v].add(e);
        
        //update the weighted sum of each edge
        weightedSum[u] += w;
        weightedSum[v] += w;
        
        //update the degree of each edge
        ++nodeDegrees[u];
        ++nodeDegrees[v];
        
        ++edge_count;
    }
    
    public int size(){
        return SIZE;
    }
    
    public EdgeList adj(int v){
        return nodes[v];
    }
    
    public EdgeList cAdj(int v){
        return coarse_neighbors[v];
    }
    
    public void sortAdj(int u, Comparator<Edge> c){
        nodes[u].sort(c);
    }
    
    public void sortCoarseAdj(int u, Comparator<Edge> c){
        coarse_neighbors[u].sort(c);
    }
    
    public void setCoarse(int node, boolean c){
        coarse[node] = c;
        if(c){
            //update the neighborHood of node
            for(Edge e: adj(node)){
                int v = e.getEndpoint(node);
                coarse_neighbors[v].add(e);
                weightedCoarseSum[v] += e.getWeight();
                ++c_nodeDegrees[v];
            }
        }
    }
    
    public int getEdgeCount(){
        return edge_count;
    }
    
    public boolean isC(int id){
        return coarse[id];
    }
    
    public double weightedDegree(int node){
        return weightedSum[node];
    }
    
    public double weightedCoarseDegree(int node){
        return weightedCoarseSum[node];
    }
    
    public int degree(int u){
        return nodeDegrees[u];
    }
    
    public int cDegree(int u){
        return c_nodeDegrees[u];
    }
    
    public Edge getCNeighborAt(int u,int idx){
        return coarse_neighbors[u].getAt(idx);
    }
    
    public float volume(int u){
        return volumes[u];
    }
    
    /**
     * Prevent users from using Pij the wrong.
     * Given the nature of our datastructure, PiJ == PJi 
     * However PJi should equal 0 so this function prevent users from using a non zero value for PJi
     * @param node
     * @param e
     * @return 
     */
    public double getPij(int node, final Edge e){
        int v = e.getEndpoint(node);
        if(isC(node)||(!isC(node) && !isC(v))) return 0; //Pij for F-F and C-F is 0
        return e.getPij();
    }
    
    public void setPij(int node, Edge e, double val){
        int v = e.getEndpoint(node);
        if(isC(node)||(!isC(node) && !isC(v))){
            throw new UnsupportedOperationException("Can only set Pij on Fine to Coarse edges");
        }else{
            e.setPij(val);
        }
    }
    
    public void setVolume(int node, float v){
        volumes[node] = v;
    }
    
    public void printGraph(){
        System.out.printf("#nodes: %d #edges: %d \n",SIZE,edge_count);
        for(int i=0;i<SIZE;i++){
            for(Edge e: adj(i)){
                int v = e.getEndpoint(i);
                if(i<v){
                    System.out.println(e.getU()+" "+e.getV());
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Graph[nodes:"+SIZE+",edges:"+edge_count+"]";
    }
    
}
