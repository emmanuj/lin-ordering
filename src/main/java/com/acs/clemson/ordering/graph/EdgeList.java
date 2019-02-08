package com.acs.clemson.ordering.graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author Emmanuel John
 */
public class EdgeList implements java.io.Serializable, Iterable<Edge>{
    private final ArrayList<Edge> data= new ArrayList();
    private static final long serialVersionUID = 5257488434893175524L;
    
    public void add(Edge e){
        data.add(e);
    }
    
    @Override
    public Iterator<Edge> iterator() {
        Iterator<Edge> it = new IteratorImpl(data, data.size());
        return it;
    }

    private static final class IteratorImpl implements Iterator<Edge> {
        private final ArrayList<Edge> data;
        private final int N;
        public IteratorImpl(ArrayList<Edge> data, int N) {
            this.data = data;
            this.N = N;
        }
        private int currentIndex = 0;
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
            if(!this.hasNext()) throw new NoSuchElementException();
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
