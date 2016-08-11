package com.acs.clemson.ordering.graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

/**
 *
 * @author Emmanuel John
 */
public class EdgeList implements Iterable<Edge>{
    private final ArrayList<Edge> data= new ArrayList();
    
    public void add(Edge e){
        data.add(e);
    }
    
    @Override
    public Iterator<Edge> iterator() {
        Iterator<Edge> it = new IteratorImpl();
        return it;
    }

    private class IteratorImpl implements Iterator<Edge> {

        public IteratorImpl() {
        }
        private int currentIndex = 0;
        private final int N = data.size();
        @Override
        public boolean hasNext() {
            
            //skip deleted
            while(currentIndex < N && data.get(currentIndex).isDeleted()){
                currentIndex++;
            }
            
            return currentIndex < N;
        }

        @Override
        public Edge next() {
            return data.get(currentIndex++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    public Edge getAt(int idx){
        return data.get(idx);
    }

    public void sort(Comparator<Edge> c){
        data.sort(c);
    }
}
