package com.acs.clemson.ordering.util;

import com.acs.clemson.ordering.graph.Edge;
import com.acs.clemson.ordering.graph.Graph;
import java.util.Random;

/**
 *
 * @author emmanuj
 */
public class GraphUtil {

    private static final double ALFA = 0.5;

    private static final Random RANDOM = new Random(); //Not thread safe. Consider using ThreadLocalRandom.current().nextInt() in multithread use.
    private static final int R = 10;
    private static final int K = 10;

    public static void computeAlgebraicDist(Graph g) {
        for (int r = 0; r < R; r++) {
            double x_vec[] = new double[g.size()];
            //generate the random numbers
            for (int j = 0; j < x_vec.length; j++) {
                x_vec[j] = randomInRange(-0.5, 0.5);
            }

            for (int k = 0; k < K; k++) {
                double c_vec[] = new double[g.size()];
                for (int u = 0; u < g.size(); u++) {
                    double prod_sum = 0;
                    double sum = 0;
                    for (Edge e : g.adj(u)) {
                        int v = e.getEndpoint(u);
                        sum = e.getWeight();
                        prod_sum += e.getWeight() * x_vec[v];
                    }

                    c_vec[u] = ALFA * x_vec[u];
                    if (sum != 0) {//avoid divide by 0.
                        c_vec[u] += (1 - ALFA) * (prod_sum / sum);
                    }
                }
                x_vec = c_vec;
            }

            //rescale 
            double min = minElement(x_vec);
            double max = maxElement(x_vec);
            for (int i = 0; i < x_vec.length; i++) {
                double a = x_vec[i] - min;
                double b = max - x_vec[i];
                x_vec[i] = (0.5 * (a - b)) / (a + b);
            }
            
            //compute the algebraic distance
            for (int u = 0; u < g.size(); u++) {
                for (Edge e : g.adj(u)) {
                    int v = e.getEndpoint(u);
                    if (u < v) {
                        double ad = 1 / ((Math.abs(x_vec[u] - x_vec[v])) + Constants.EPSILON);
                        //System.out.println(ad+" "+((Math.abs(x_vec[u] - x_vec[v])) + Constants.EPSILON));
                        if (ad < e.getAlgebraicDist()) {
                            e.setAlgebraicDist(ad);
                        }
                    }
                }
            }
        }
    }

    public static double minElement(double a[]) {
        double min = a[0];
        for (int i = 1; i < a.length; i++) {
            if (a[i] < min) {
                min = a[i];
            }
        }
        return min;
    }

    public static double maxElement(double a[]) {
        double max = a[0];
        for (int i = 1; i < a.length; i++) {
            if (a[i] > max) {
                max = a[i];
            }
        }
        return max;
    }

    public static double randomInRange(double min, double max) {
        double range = max - min;
        double scaled = RANDOM.nextDouble() * range;
        double shifted = scaled + min;
        return shifted; // == (rand.nextDouble() * (max-min)) + min;
    }

}
