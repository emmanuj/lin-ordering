
import java.util.Arrays;

class HashList {

    final int SIZE = 20;
    int[] heads;
    long[] data = new long[SIZE];
    boolean[] used = new boolean[SIZE];
    int[] next = new int[SIZE];

    // n - number of vertices 
    HashList(int n) {
        heads = new int[n];
        Arrays.fill(heads, -1);
    }

    // adds new edge (x, y)
    boolean add(int x, int y) {
        long code = code(x, y);
        int hash = hash(x, y);
        while (used[hash]) {
            if (data[hash] == code) {
                return false;
            } else {
                hash = (hash + 1) % SIZE;
            }
        }
        data[hash] = code;
        used[hash] = true;
        next[hash] = heads[x];
        heads[x] = hash;
        return true;
    }

    boolean contains(int x, int y) {
        long code = code(x, y);
        int hash = hash(x, y);
        while (used[hash]) {
            if (data[hash] == code) {
                return true;
            } else {
                hash = (hash + 1) % SIZE;
            }

        }
        return false;
    }

    void enumerate(int x) { //The key to understanding the data structure is understanding what is going on here
        for (int i = heads[x]; i != -1; i = next[i]) {
            int y = (int) data[i];
            System.out.println(y);
        }
    }

    int hash(int x, int y) {
        return Math.abs((x + 111) * (y - 333) % SIZE);
    }

    long code(int x, int y) {
        return ((1L * x) << 32) | y;
    }
    
    public void visualizeData(){
        
        ArrayPrinter p = new ArrayPrinter();
        System.out.println("heads: ");
        p.printArray(heads);
        System.out.println("next: ");
        p.printArray(next);
        System.out.println("data: ");
        p.printArray(data);
        System.out.println("used: ");
        p.printArray(used);
        
    }
    
    static class ArrayPrinter{
        public void printArray(long a[]){
            for(long t:a){
                System.out.print(t+" ");
            }
            System.out.println("");
        }
        public void printArray(int a[]){
            for(int t:a){
                System.out.print(t+" ");
            }
            System.out.println("");
        }
        public void printArray(boolean a[]){
            for(boolean t:a){
                System.out.print((t?"1":"0")+" ");
            }
            System.out.println("");
        }
    }
}
