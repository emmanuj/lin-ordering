
import com.acs.clemson.ordering.util.StringParser;
import java.util.ArrayList;

/**
 *
 * @author emmanuj
 */
public class Playground {

    public static void main(String[] args) {
        
        System.out.println("Before: ");
        float a[] = new float[5];
        for(int i=0;i<5;i++){
           a[i] = (float)Math.random();
           System.out.println(a[i]+" ");
        }
        System.out.println();
        
       
        
        for(int i=0;i<3;i++){
           float b[] = new float[5];
           for(int j=0;j<b.length;j++){
               b[j] = a[j]+1;
           }
           a = b;
        }
        
        System.out.println("After: ");
        for(int i=0;i<5;i++){
           System.out.println(a[i]+" ");
        }
        System.out.println();
        
        System.exit(0);
        
        Boy b = new Boy();
        b.x = 10;
        ArrayList<Boy> s = new ArrayList();
        s.add(new Boy());
        s.get(0).x=4;
        ArrayList<Boy> s1 = new ArrayList();
        s1.add(s.get(0));
        
        System.out.println(b.x + " - " + s.get(0).x+" - "+s1.get(0).x);
        
        s1.get(0).x = 15;

        System.out.println(b.x + " - " + s.get(0).x+" - "+s1.get(0).x);
        System.out.println(StringParser.toInt("45"));
    }

}

class Boy {

    int x;
}
