package com.acs.clemson.ordering.graph;

/**
 *
 * @author emmanuj
 */
public class GraphProperty {
    private String graphId;
    private long size;
    
    //for debugging purposes
    private long edgeCount;
    private int level;

    public String getGraphId() {
        return graphId;
    }

    public void setGraphId(String graphId) {
        this.graphId = graphId;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public GraphProperty(String graphId, long size) {
        this.graphId = graphId;
        this.size = size;
    }

    public GraphProperty() {
    }

    public long getEdgeCount() {
        return edgeCount;
    }

    public void setEdgeCount(long edgeCount) {
        this.edgeCount = edgeCount;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void extractProps(Graph g) {
        this.graphId = g.getGraph_id();
        this.size = g.size();
        this.edgeCount = g.getEdgeCount();
        this.level = g.getLevel();
    }
}
