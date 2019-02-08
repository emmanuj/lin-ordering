package com.acs.clemson.ordering.graph;

import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Emmanuel John
 */
public class Graph implements java.io.Serializable{
    private final int SIZE;
    private EdgeList[] nodes;
    private double[] volumes;
    private double[] weightedSum;
    private double[] weightedCoarseSum;
    private int[] nodeDegrees;
    private int[] c_nodeDegrees;
    private int edge_count=0;
    private boolean[] coarse;
    private EdgeList[] coarse_neighbors;
    private int[] prev_id;
    private int level=0;
    private List<Integer> soln;
    private double xiVec[];
    private String graphName;
    private static final long serialVersionUID = 5067155957993140108L;
    public Graph(int SIZE, String name){
        this.SIZE =SIZE;
        this.graphName = name;
        nodes = new EdgeList[SIZE];
        coarse_neighbors = new EdgeList[SIZE];
        
        volumes = new double[SIZE];
        coarse = new boolean[SIZE];
        prev_id =new int[SIZE];
        //initialize data
        weightedSum = new double[SIZE];
        weightedCoarseSum = new double[SIZE];
        nodeDegrees= new int[SIZE];
        c_nodeDegrees = new int[SIZE];
        xiVec = new double[SIZE];
        
        for(int i=0;i<SIZE;i++){
            nodes[i]=new EdgeList();
            coarse_neighbors[i] = new EdgeList();
            volumes[i]=1;
            prev_id[i]=-1;
        }
        soln = new ArrayList();
    }

    public Graph(Graph g, Int2DoubleMap[] gData, ArrayList<Integer> seeds) {
        this(seeds.size(),g.getGraphName());
        int[] new_id = new int[g.size()];
        
        for(int i=0;i<seeds.size();i++){
            prev_id[i] = seeds.get(i);
            volumes[i] = g.volume(prev_id[i]);
            new_id[seeds.get(i)] = i;
        }
        
        seeds.forEach((id) -> {
            gData[id].entrySet().forEach((nb) -> {
                this.addEdge(new_id[id], new_id[nb.getKey()], nb.getValue());
            });
        });
        
        //compute new volume
        for(int previd=0;previd<g.size();previd++){//loop through the older graph
            if(!g.isC(previd)){//fine nodes only
                for(Edge e:g.cAdj(previd)){
                    double vol = (e.getPij() * g.volume(previd));
                    int curId = new_id[e.getEndpoint(previd)]; //prev neigbor id -> id
                    volumes[curId] = volumes[curId] + vol;
                }
            }
		
	}
        
        level=g.getLevel()+1;
    }
    
    public String getGraph_id() {
        return graphName+"_"+level;
    }

    public String getGraphName() {
        return graphName;
    }

    public void setGraphName(String graphName) {
        this.graphName = graphName;
    }
    
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
    
    public final void addEdge(int u, int v, double w){
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
    
    public double volume(int u){
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

    public List<Integer> getSoln() {
        return soln;
    }

    public void setSoln(List<Integer> soln) {
        this.soln = soln;
    }
    
    public void updateSoln(int node, int idx){
        soln.set(idx, node);
    }

    public double[] getXiVec() {
        return xiVec;
    }

    public void setXiVec(double[] xiVec) {
        this.xiVec = xiVec;
    }
    
    public double getXi(int u){
        return xiVec[u];
    }
    public void setXi(int u, double xi){
        xiVec[u] = xi;
    }
    public int getNodeAtSoln(int pos){
        return soln.get(pos);
    }
    public int prevId(int node){
        return prev_id[node];
    }

    public int[] getPrev_id() {
        return prev_id;
    }

    public int getEdge_count() {
        return edge_count;
    }

    public void setEdge_count(int edge_count) {
        this.edge_count = edge_count;
    }

    public void setVolumes(double[] volumes) {
        this.volumes = volumes;
    }

    public void setPrev_id(int[] prev_id) {
        this.prev_id = prev_id;
    }
    
    public void printGraph(){
        System.out.printf("#nodes: %d #edges: %d \n",SIZE,edge_count);
        for(int i=0;i<SIZE;i++){
            if(degree(i) == 0){
               // System.out.println("Zero node: v:"+i);
                //System.exit(0);
            }
            for(Edge e: adj(i)){
                int v = e.getEndpoint(i);
                
                if(i<v){
                    System.out.println(e.getU()+" "+e.getV()+" "+e.getWeight() +" Vol: "+volumes[i]);
                }
            }
        }
    }
    public Graph filterEdges(double thresh){
        Graph g = new Graph(SIZE, graphName);
        for(int u=0;u<SIZE;u++){
            double Ki = thresh * this.weightedDegree(u);
            for(Edge e: this.adj(u)){
                int v = e.getEndpoint(u);
                if(u < v){
                    double Kj = thresh * this.weightedDegree(v);
                    if(!(e.getWeight() < Ki && e.getWeight() < Kj)){
                        g.addEdge(u, v, e.getWeight());
                    }
                }
            }
        }
        g.setPrev_id(prev_id);
        g.setLevel(this.level);
        g.setVolumes(volumes);
        
        return g;
    }
    public void print(){
        double total=0;
        for(int i=0;i<volumes.length;i++){
            total+=volumes[i];
        }
        System.out.printf("Level: %d #nodes: %d #edges: %d Total Volume: %.1f\n",level,SIZE,edge_count,total);
        //System.out.printf("Level: %d #nodes: %d #edges: %d\n",level,SIZE,edge_count);
    }
    
    public void dispose(){
        //let the GC do it's work
        nodes =null;
        volumes=null;
        weightedSum=null;
        weightedCoarseSum = null;
        nodeDegrees =null;
        c_nodeDegrees = null;
        edge_count =0;
        coarse =null;
        coarse_neighbors=null;
        prev_id =null;
        level =0;
        soln = null;
        xiVec = null;
                
    }

    @Override
    public String toString() {
        return "Graph[nodes:"+SIZE+",edges:"+edge_count+"]";
    }
    
}
