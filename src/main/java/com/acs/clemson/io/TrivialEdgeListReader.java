package com.acs.clemson.io;

import com.acs.clemson.ordering.graph.Graph;
import com.acs.clemson.ordering.util.StringParser;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author emmanuj
 */
public class TrivialEdgeListReader implements GraphReader {

    private final String filename;
    private final String delimeter;
    private final int firstnode;
    private static BiMap idMap; //maps OriginalID -> new ID
    private final String gname;
    public TrivialEdgeListReader(String file, String delimeter, int firstnode, String gname) {
        this.filename = file;
        this.delimeter = delimeter;
        this.firstnode = firstnode;
        this.gname=gname;
    }
    @Override
    public Graph read(boolean shuffleNodes) {
        Graph g=null;
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filename))) {
            String firstline = br.readLine().trim();
            String []graphinfo = firstline.split(delimeter);
            if(firstline.toLowerCase().contains("nodes")){
                g = new Graph(Integer.parseInt(graphinfo[2].trim()),gname);
            }else if(firstline.contains("p")){
                g = new Graph(Integer.parseInt(graphinfo[2]),gname);
            }else if(firstline.contains("ghct")){
                g = new Graph(Integer.parseInt(graphinfo[1]),gname);
            }
            
            if(g ==null ) throw new UnsupportedOperationException("Number of nodes not defined in the file");
            
            ArrayList<Integer> nodes = new ArrayList();
            idMap = HashBiMap.create(g.size());
            for(int i=0;i<g.size();i++){
                nodes.add(i);
            }
            if(shuffleNodes){
                Collections.shuffle(nodes);
            }
            
            for(int i=0;i<g.size();i++){
                idMap.put(i, nodes.get(i));//maps OriginalID -> new ID
            }
            
            String line;
            while((line = br.readLine()) !=null){
                if(!line.contains("#")){
                    String d[] = line.trim().split(" ");
                    switch (firstnode) {
                        case 0:
                            if(line.startsWith("e")){
                                g.addEdge(nodes.get(StringParser.toInt(d[1])), nodes.get(StringParser.toInt(d[2])), 1);
                            }else{
                                g.addEdge(nodes.get(StringParser.toInt(d[0])), nodes.get(StringParser.toInt(d[1])), 1);
                            }
                            break;
                        case 1:
                            if(line.startsWith("e")){
                                g.addEdge(nodes.get(StringParser.toInt(d[1])-1), nodes.get(StringParser.toInt(d[2])-1), 1);
                            }else{
                                g.addEdge(nodes.get(StringParser.toInt(d[0])-1), nodes.get(StringParser.toInt(d[1])-1), 1);
                            }
                            
                            break;
                        default:
                            throw new UnsupportedOperationException("Node Ids must begin from 0 or 1");
                    }
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }


        return g;
    }
    /**
     * To retrieve originalId using newID, first do idMap.inverse()
     * @return idMap OriginalID -> new ID
     */
    public static BiMap getIdMap() {
        return idMap;
    }

    public int getFirstnode() {
        return firstnode;
    }
}
