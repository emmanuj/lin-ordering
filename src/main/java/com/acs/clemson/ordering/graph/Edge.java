package com.acs.clemson.ordering.graph;

import com.acs.clemson.ordering.util.Constants;

/**
 *
 * @author Emmanuel John
 */
public class Edge {
    private boolean deleted=false;
    private int u;
    private int v;
    private double weight;
    private double pij;
    private double algebraicDist = (1/Constants.EPSILON);
    
    public Edge(int u, int v, double weight) {
        this.u = u;
        this.v = v;
        this.weight = weight;
    }

    public Edge() {
    }

    public int getU() {
        return u;
    }

    public void setU(int u) {
        this.u = u;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }
    
    public int getEndpoint(int from){
        if(from == v){
            return u;
        }
        
        return v;
    }

    public double getPij() {
        return pij;
    }

    public void setPij(double pij) {
        this.pij = pij;
    }

    public double getAlgebraicDist() {
        return algebraicDist;
    }

    public void setAlgebraicDist(double algebraicDist) {
        this.algebraicDist = algebraicDist;
    }
    
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Edge[u:"+u+", v:"+v+"]";
    }
}
