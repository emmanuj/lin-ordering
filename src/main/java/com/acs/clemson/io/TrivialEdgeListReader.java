package com.acs.clemson.io;

import com.acs.clemson.ordering.graph.Graph;
import com.acs.clemson.ordering.util.StringParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author emmanuj
 */
public class TrivialEdgeListReader {

    private final String filename;
    private final String delimeter;
    private final int firstnode;

    public TrivialEdgeListReader(String file, String delimeter, int firstnode) {
        this.filename = file;
        this.delimeter = delimeter;
        this.firstnode = firstnode;
    }

    public Graph read() {
        Graph g=null;
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filename))) {
            String firstline = br.readLine();
            String []graphinfo = firstline.split(delimeter);
            if(firstline.toLowerCase().contains("nodes")){
                g = new Graph(Integer.parseInt(graphinfo[2].trim()));
            }else if(firstline.contains("p")){
                g = new Graph(Integer.parseInt(graphinfo[2]));
            }else if(firstline.contains("ghct")){
                g = new Graph(Integer.parseInt(graphinfo[1]));
            }
            
            if(g ==null ) throw new UnsupportedOperationException("Number of nodes not defined in the file");
            String line;
            while((line = br.readLine()) !=null){
                if(!line.contains("#")){
                    String d[] = line.split(" "); //Find potential way to make this better for CPU and speed.
                    switch (firstnode) {
                        case 0:
                            if(line.startsWith("e")){
                                g.addEdge(StringParser.toInt(d[1]), StringParser.toInt(d[2]), 1);
                            }else{
                                g.addEdge(StringParser.toInt(d[0]), StringParser.toInt(d[1]), 1);
                            }
                            break;
                        case 1:
                            if(line.startsWith("e")){
                                g.addEdge(StringParser.toInt(d[1])-1, StringParser.toInt(d[2])-1, 1);
                            }else{
                                g.addEdge(StringParser.toInt(d[0])-1, StringParser.toInt(d[1])-1, 1);
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
}
