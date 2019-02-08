package com.acs.clemson.io;

import com.acs.clemson.ordering.graph.Graph;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author emmanuj
 */
public class SerializationUtil {

    public static void save(Graph g) {
        try (ObjectOutputStream oos
                = new ObjectOutputStream(new FileOutputStream(g.getGraph_id()))) {

            oos.writeObject(g);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Graph load(String filename) {
        Graph g = null;
        try (ObjectInputStream ois
                = new ObjectInputStream(new FileInputStream(filename))) {

            g = (Graph) ois.readObject();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return g;
    }
}
