package com.acs.clemson.io;

import com.acs.clemson.ordering.graph.Graph;

/**
 *
 * @author {Emmanuel John
 */
public interface GraphReader {
    public Graph read(boolean shuffle);
}
