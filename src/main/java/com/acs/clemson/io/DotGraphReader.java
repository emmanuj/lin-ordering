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
public class DotGraphReader implements GraphReader {

    private final String filename;
    private final String delimeter;
    private final int firstnode;
    private static BiMap idMap; //maps OriginalID -> new ID
    private final String gname;
    public DotGraphReader(String file, String delimeter, int firstnode, String gname) {
        this.filename = file;
        this.delimeter = delimeter;
        this.firstnode = firstnode;
        this.gname = gname;
        if(firstnode == 0) throw new UnsupportedOperationException("First node has to be either 0 or 1");
    }

    @Override
    public Graph read(boolean shuffleNodes) {
        Graph g=null;
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filename))) {
            String firstline = br.readLine();
            String []graphinfo = firstline.split(delimeter);
            g = new Graph(Integer.parseInt(graphinfo[0].trim()),gname);
            
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
            int u = 0;
            while((line = br.readLine()) !=null){
                if(line.trim().isEmpty()) throw new UnsupportedOperationException("Blank line in graph file");
                
                String d[] = line.trim().split(" ");
                for(String n: d){
                    int v = StringParser.toInt(n);
                    if(firstnode == 1){
                        v-=1;
                    }
                    if(u < v){
                        g.addEdge(nodes.get(u), nodes.get(v), 1);
                    }
                }
                u++;
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
