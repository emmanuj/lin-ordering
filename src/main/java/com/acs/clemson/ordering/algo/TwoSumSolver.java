package com.acs.clemson.ordering.algo;

import com.acs.clemson.ordering.graph.Edge;
import com.acs.clemson.ordering.graph.Graph;
import com.acs.clemson.ordering.util.Constants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.iterators.PermutationIterator;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author emmanuj
 */
public class TwoSumSolver implements Solver {

    private static TwoSumSolver solver;
    private double mCost=0;
    private enum Relaxation {
        GS,
        COMPATIBLE
    }

    private TwoSumSolver() {
    }

    public static TwoSumSolver getInstance() {
        if (solver == null) {
            solver = new TwoSumSolver();
        }
        return solver;
    }

    @Override
    public void uncoarsen(Graph fine, Graph coarse) {
        initialize(fine, coarse.getSoln(), coarse.getXiVec(), coarse.getPrev_id());
        coarse.dispose();
        
        mCost = getCost(fine);
        
        doNodeMinimization(fine);
        doRelaxation(fine, Relaxation.COMPATIBLE);
        doNodeMinimization(fine);
        doRelaxation(fine, Relaxation.GS);
        doNodeMinimization(fine);
        
        for (int i = 0; i < Constants.K3; i++) {
            double c = refine(fine);
            if (c == mCost) {//no change
                break;
            }
            mCost = c;
        }
        System.out.printf("Level: %d Cost: %.5e\n", fine.getLevel(), getCost(fine));
    }
    private void doNodeMinimization(Graph g){
        //System.out.println("Cost before minimization: "+ mCost);
        for(int i =0; i < Constants.K4; i++){
            double c = minimizeNodes(g,Constants.K5);
            if(c == mCost ){//no change
                break;
            }
            mCost = c;
        }
        //System.out.println("Cost after minimization: "+ mCost);
    }
    private void doRelaxation(Graph fine, Relaxation type) throws IllegalArgumentException {
        double cost = mCost;
        for (int i = 0; i < Constants.K1; i++) {
            List<Integer> tmpSoln = new ArrayList(fine.getSoln());
            double xivec[] = new double[fine.size()];
            System.arraycopy(fine.getXiVec(), 0, xivec, 0, fine.size());
            switch (type) {
                case COMPATIBLE:
                    relaxCompatible(fine, xivec, tmpSoln);
                    break;
                case GS:
                    relaxGS(fine, xivec, tmpSoln);
                    break;
                default:
                    throw new IllegalArgumentException("Specified type not Supported");
            }
            double c = getCost(fine, tmpSoln, xivec);
            if (c < cost) {//If the cost is better, update the graph
                cost = c;
                fine.setSoln(tmpSoln);
                fine.setXiVec(xivec);
            } else {
                break;
            }
        }
    }

    @Override
    public void solve(Graph g) {
        double[] xiVec = new double[g.size()];
        double minCost = Double.MAX_VALUE;
        List<Integer> soln = new ArrayList(g.size());
        for (int i = 0; i < g.size(); i++) {
            soln.add(i);
        }

        PermutationIterator<Integer> itr = new PermutationIterator(soln);
        List<Integer> temp = itr.next();
        while (itr.hasNext()) {
            computeXi(g, temp, xiVec);
            double cost = getCost(g, temp, xiVec);

            if (cost < minCost) {
                minCost = cost;
                Collections.copy(soln, temp); //TODO: is there a better way to do this?
            }
            temp = itr.next();
        }

        computeXi(g, soln, xiVec);
        g.setXiVec(xiVec);
        g.setSoln(soln);
        //g.printGraph();
        System.out.printf("In Coarsest level. Cost: %.5e", getCost(g));
        System.out.println(" | Ordering: " + g.getSoln());
    }

    private void initialize(Graph f, List<Integer> soln, double xiVec[], int prev_id[]) {
        double yi[] = new double[f.size()];
        boolean placed[] = new boolean[f.size()];
        ArrayList<Integer> nodes = new ArrayList(f.size() - soln.size());
        //update the fine node with the xi from the coarse node.
        for (int i = 0; i < soln.size(); i++) {
            yi[prev_id[soln.get(i)]] = xiVec[soln.get(i)];
            placed[prev_id[soln.get(i)]] = true;
        }

        for (int i = 0; i < f.size(); i++) {
            if (!placed[i]) {
                nodes.add(i);
            }
        }

        PlacedComparator comp = new PlacedComparator(placed, f);
        nodes.sort(comp);

        int p = 0;
        int begin = 0;

        while (begin < nodes.size()) {
            int u = nodes.get(begin);
            double sum = 0;
            double sum_prod = 0;
            for (Edge e : f.adj(u)) {
                sum += e.getWeight();
                sum_prod += yi[e.getEndpoint(u)] * e.getWeight();
            }

            yi[u] = sum_prod / sum; //TODO: Remember to test what happens with zero degree nodes here.
            placed[u] = true;

            begin++;
            p++;

            if ((p % 10 == 0) && begin < nodes.size()) {
                Collections.sort(nodes.subList(begin, nodes.size()), comp); //TODO: Test this
            }
        }

        pump(f, yi);
    }

    private void relaxGS(Graph f, double xivec[], List<Integer> soln) {
        double yi[] = new double[f.size()];
        //update the fine node with the xi from the coarse node.
        System.arraycopy(xivec, 0, yi, 0, f.size());

        for (int i = 0; i < f.size(); i++) {
            double sum = 0;
            double sum_prod = 0;
            int u = soln.get(i); //get node at index i
            for (Edge e : f.adj(u)) {
                sum += e.getWeight();
                sum_prod += yi[e.getEndpoint(u)] * e.getWeight();
            }

            //avoid divide by zero as a result of zero degree fine nodes
            if (f.degree(u) != 0) {//TODO: review this
                yi[u] = sum_prod / sum;
            } else {
                yi[u] = -1; //all the zero degree nodes go at the beginning
            }
        }

        pump(f, yi, xivec, soln);
    }

    private void relaxCompatible(Graph f, double xivec[], List<Integer> soln) {
        double yi[] = new double[f.size()];
        //update the fine node with the xi from the coarse node.
        System.arraycopy(xivec, 0, yi, 0, f.size());

        for (int i = 0; i < f.size(); i++) {
            int u = soln.get(i);
            if (!f.isC(u)) { //process only fine nodes
                double sum = 0;
                double sum_prod = 0;

                for (Edge e : f.adj(u)) {
                    sum += e.getWeight();
                    sum_prod += yi[e.getEndpoint(u)] * e.getWeight();
                }

                //avoid divide by zero as a result of zero degree fine nodes
                if (f.degree(u) != 0) {//TODO: review this
                    yi[u] = sum_prod / sum;
                } else {
                    yi[u] = -1; //all the zero degree nodes go at the beginning
                }
            }
        }
        pump(f, yi, xivec, soln);
    }

    private double refine(Graph g) {
        double m[][];
        double b[];
        double curMinCost = mCost;
        int step = (int) Math.floor(Constants.WIN_SIZE / 2) + 1;
        for (int wStart = 0; wStart < g.size(); wStart += step) {
            int wEnd = Math.min(wStart + Constants.WIN_SIZE - 1, g.size() - 1);
            m = new double[Constants.WIN_SIZE + 2][Constants.WIN_SIZE + 2];
            b = new double[Constants.WIN_SIZE + 2];

            Map<Integer, NodeBundle> nodes = new HashMap();
            for (int i = Math.max(0, wStart - 1); i <= wEnd; i++) { //We include the node before start of the window in order to pump window correctly (i.e pumping needs the node before the start of the window)
                nodes.put(g.getNodeAtSoln(i), new NodeBundle(i, g.getXi(g.getNodeAtSoln(i))));
            }
            buildMatrices(g, nodes, m, b, wStart, wEnd);

            RealMatrix mat = MatrixUtils.createRealMatrix(m);
            RealVector bvec = MatrixUtils.createRealVector(b);

            DecompositionSolver d = new LUDecomposition(mat).getSolver();

            if (!d.isNonSingular()) {//no solution (Check before solving otherwise an exception will be thrown
                continue;
            }

            RealVector soln = new LUDecomposition(mat).getSolver().solve(bvec);

            boolean iszeros = true;

            for (int i = 0; i < soln.getDimension(); i++) {
                if (soln.getEntry(i) != 0) {
                    iszeros = false;
                    break;
                }
            }

            if (iszeros) { //positions of nodes are unchanged
                continue;
            }

            final double QCostOld = windowCost(g, wStart, wEnd);
            final double QCostNew = windowCost(g, nodes, soln, wStart, wEnd);
            if (QCostNew < QCostOld) { //update graph

                for (Integer node : nodes.keySet()) { //TODO: use map iterator from commons collections
                    g.setXi(node, nodes.get(node).xi);
                    g.updateSoln(node, nodes.get(node).pos);
                }
                
                curMinCost = (curMinCost - QCostOld)+QCostNew;
            }
        }
        
        return curMinCost;
    }

    public double minimizeNodes(Graph g, int k) {

        double minc = mCost;
        for (int i = 0; i < g.size(); i++) {
            int leftBound = Math.max(0, i - k);
            int rightBound = Math.min(g.size() - 1, i + 1);
            
            minc = minimizeNode(g, i, leftBound, rightBound, minc);
        }
        return minc;
    }

    public double minimizeNode(Graph g, int curPos, int lftBnd, int rgtBnd, double minCost) {
        double curMinCost = minCost;
        int minPos = curPos;
        double curCost = minCost;

        //move node leftward
        int p = curPos - 1;
        int q = curPos;

        while (p >= lftBnd) {
            double bcost = localCost(g, p, q);
            swapNodes(g, p, q);
            double acost = localCost(g, p, q);
            curCost = (curCost - bcost) + acost;
            if (curCost < curMinCost) {
                curMinCost = curCost;
                minPos = p;
            }
            p--;
            q--;
        }

        //reset back to the original ordering
        p = lftBnd;
        q = p + 1;
        while (q <= curPos) {
            swapNodes(g, p, q);
            p++;
            q++;
        }

        //go right
        p = curPos;
        q = p + 1;

        curCost = minCost;
        while (q <= rgtBnd) {
            double bcost = localCost(g, p, q);
            swapNodes(g, p, q);
            double acost = localCost(g, p, q);
            curCost = (curCost - bcost) + acost;
            if (curCost < curMinCost) {
                curMinCost = curCost;
                minPos = q;
            }
            p++;
            q++;
        }

        //reset the ordering to the minimum
        q = rgtBnd;
        p = q - 1;
        while (p >= minPos) {
            swapNodes(g, p, q);
            p--;
            q--;
        }

        return curMinCost;
    }

    private double localCost(Graph g, int i, int j) {
        double aSum = 0;
        double bSum = 0;
        int nodeA = g.getNodeAtSoln(i);
        int nodeB = g.getNodeAtSoln(j);

        double intersect = 0;
        for (Edge e : g.adj(nodeA)) {
            int nb = e.getEndpoint(nodeA);
            double s = e.getWeight() * Math.pow(g.getXi(nodeA) - g.getXi(nb), 2);
            if (nb == nodeB) {
                intersect += s;
            }
            aSum += s;
        }
        for (Edge e : g.adj(nodeB)) {
            bSum += e.getWeight() * Math.pow(g.getXi(nodeB) - g.getXi(e.getEndpoint(nodeB)), 2);
        }

        return (aSum + bSum) - intersect;
    }

    public void swapNodes(Graph g, int i, int j) {
        int tmpNode = g.getNodeAtSoln(i);
        g.updateSoln(g.getNodeAtSoln(j), i);
        g.updateSoln(tmpNode, j);
        int nodeA = g.getNodeAtSoln(i);
        int nodeB = g.getNodeAtSoln(j);
        double xi = 0.5 * g.volume(nodeA);
        if (i > 0) {
            int predecesor = g.getNodeAtSoln(i - 1);
            xi += g.getXi(predecesor) + (0.5 * g.volume(predecesor));
        }
        g.setXi(nodeA, xi);
        g.setXi(nodeB, (0.5 * g.volume(nodeB)) + xi + (0.5 * g.volume(nodeA))); //TODO: same as 0.5 * (Vb + Va) + Xi
    }

    public void exit(int t) {
        System.exit(t);
    }

    public void print(String s) {
        System.out.print(s);
    }

    private void buildMatrices(Graph g, Map<Integer, NodeBundle> nodes, double m[][], double b[], int wStart, int wEnd) {
        //System.out.println(nodes);
        for (int i = wStart; i <= wEnd; i++) {
            int node = g.getNodeAtSoln(i);
            m[i - wStart][i - wStart] = g.weightedDegree(node); //fill the diagonal
            m[(wEnd - wStart) + 1][i - wStart] = g.volume(node);
            m[(wEnd - wStart) + 2][i - wStart] = g.volume(node) + g.getXi(node);

            //fill volumes and x_bar
            m[i - wStart][(wEnd - wStart) + 1] = g.volume(node);
            m[i - wStart][(wEnd - wStart) + 2] = g.volume(node) * g.getXi(node);

            for (Edge e : g.adj(node)) {
                int u = e.getEndpoint(node);
                b[i - wStart] = b[i - wStart] + e.getWeight() * (g.getXi(u) - g.getXi(node));
                if (g.getXi(u) >= g.getXi(g.getNodeAtSoln(wStart)) && g.getXi(u) <= g.getXi(g.getNodeAtSoln(wEnd))) {
                    final NodeBundle n = nodes.get(u);
                    m[i - wStart][n.pos - wStart] = -e.getWeight();
                    m[(wEnd - wStart) + 1][n.pos - wStart] = g.volume(u);
                    m[(wEnd - wStart) + 2][n.pos - wStart] = g.volume(u) * g.getXi(u);
                }
            }
        }
    }

    private void pump(Graph f, double[] yi) {
        ArrayList<Integer> nodes = new ArrayList(f.size());
        for (int i = 0; i < f.size(); i++) {
            nodes.add(i);
        }

        nodes.sort((Integer o1, Integer o2) -> Double.compare(yi[o1], yi[o2]));
        for (int i = 0; i < nodes.size(); i++) {
            double xi = 0.5 * f.volume(nodes.get(i));
            if (i > 0) {
                xi += f.getXi(nodes.get(i - 1)) + (0.5 * f.volume(nodes.get(i - 1)));
            }
            f.setXi(nodes.get(i), xi);
        }
        f.setSoln(nodes);
    }

    private void pump(Graph f, double[] yi, double xivec[], List<Integer> soln) {
        soln.sort((Integer o1, Integer o2) -> Double.compare(yi[o1], yi[o2]));
        for (int i = 0; i < soln.size(); i++) {
            double xi = 0.5 * f.volume(soln.get(i));
            if (i > 0) {
                xi += xivec[soln.get(i - 1)] + (0.5 * f.volume(soln.get(i - 1)));
            }
            xivec[soln.get(i)] = xi;
        }
    }

    private double windowCost(Graph g, int wStart, int wEnd) {
        double xSum = 0;
        for (int i = wStart; i <= wEnd; i++) {
            int node = g.getNodeAtSoln(i);
            for (Edge e : g.adj(node)) {
                int u = e.getEndpoint(node);
                //If the neighbor is outside of the window, the edge contributes to the cost.
                if ((g.getXi(u) < g.getXi(g.getNodeAtSoln(wStart)) || g.getXi(u) > g.getXi(g.getNodeAtSoln(wEnd))) || node < u) {
                    xSum += e.getWeight() * Math.pow(g.getXi(node) - g.getXi(u), 2);
                }
            }
        }

        return xSum;
    }

    private double windowCost(Graph g, Map<Integer, NodeBundle> nodes, RealVector soln, int wStart, int wEnd) {

        ArrayList<Integer> tmp = new ArrayList((wEnd - wStart) + 1);

        for (int i = wStart; i <= wEnd; i++) {
            tmp.add(g.getNodeAtSoln(i));
        }

        tmp.sort((Integer i, Integer j) -> {
            final NodeBundle n = nodes.get(i);
            final NodeBundle m = nodes.get(j);
            final double d1 = n.xi + soln.getEntry(n.pos - wStart);
            final double d2 = m.xi + soln.getEntry(m.pos - wStart);
            return Double.compare(d1, d2);
        });

        for (int i = wStart; i <= wEnd; i++) {
            int idx = i - wStart;
            double xi = 0.5 * g.volume(tmp.get(idx));

            if (i > 0) {
                if (idx == 0) { //use the node before window start in the graph
                    xi += nodes.get(g.getNodeAtSoln(wStart - 1)).xi + (0.5 * g.volume(g.getNodeAtSoln(wStart - 1)));
                } else {
                    xi += (nodes.get(tmp.get(idx - 1)).xi + (0.5 * g.volume(tmp.get(idx - 1))));
                }
            }
            nodes.get(tmp.get(idx)).xi = xi;
            nodes.get(tmp.get(idx)).pos = i;
        }

        double xSum = 0;
        for (int i = 0; i < tmp.size(); i++) {
            int node = tmp.get(i);
            for (Edge e : g.adj(node)) {
                int u = e.getEndpoint(node);
                if (nodes.get(u) == null) {//neighbor not in window for sure.
                    xSum += e.getWeight() * Math.pow(nodes.get(node).xi - g.getXi(u), 2);
                } else if ((!(nodes.get(u).xi >= nodes.get(tmp.get(0)).xi //check neighbor is not node before wStart
                        && nodes.get(u).xi <= nodes.get(tmp.get(tmp.size() - 1)).xi)) || node < u) {
                    xSum += e.getWeight() * Math.pow(nodes.get(node).xi - nodes.get(u).xi, 2);
                }
            }
        }

        return xSum;
    }

    public void computeXi(Graph g, List<Integer> soln, double[] xiVec) {
        for (int i = 1; i < soln.size(); i++) {
            double xi = 0.5 * g.volume(soln.get(i));
            if (i > 0) {
                xi += xiVec[soln.get(i - 1)] + (0.5 * g.volume(soln.get(i - 1)));
            }
            xiVec[soln.get(i)] = xi;
        }
    }

    @Override
    public double getCost() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private double getCost(Graph g, List<Integer> soln, double[] xiVec) {
        double xSum = 0;
        for (int i = 0; i < soln.size(); i++) {
            for (Edge e : g.adj(soln.get(i))) {
                int v = e.getEndpoint(soln.get(i));
                if (soln.get(i) < v) {
                    xSum += e.getWeight() * Math.pow(xiVec[soln.get(i)] - xiVec[v], 2);
                }
            }
        }

        return xSum;
    }

    public double getCost(Graph g) {
        double xSum = 0;
        for (int i = 0; i < g.size(); i++) {
            int u = g.getNodeAtSoln(i);
            for (Edge e : g.adj(u)) {
                int v = e.getEndpoint(u);
                if (u < v) {
                    xSum += e.getWeight() * Math.pow(g.getXi(u) - g.getXi(v), 2);
                }
            }
        }
        return xSum;
    }

    private class PlacedComparator implements Comparator<Integer> {

        private final boolean[] placed;
        private final Graph f;

        public PlacedComparator(boolean[] placed, Graph f) {
            this.placed = placed;
            this.f = f;
        }

        @Override
        public int compare(Integer o1, Integer o2) {
            int u = o1;
            int v = o2;
            double rel_w1 = 0;
            for (Edge e : f.adj(u)) {
                if (placed[e.getEndpoint(u)]) {
                    rel_w1 += e.getWeight();
                }
            }
            rel_w1 /= f.weightedDegree(u);

            double rel_w2 = 0;
            for (Edge e : f.adj(v)) {
                if (placed[e.getEndpoint(v)]) {
                    rel_w2 += e.getWeight();
                }
            }
            rel_w2 /= f.weightedDegree(v);

            return Double.compare(rel_w2, rel_w1);
        }
    }

    private static final class NodeBundle {

        int pos;
        double xi;

        public NodeBundle(int position, double xi) {
            this.pos = position;
            this.xi = xi;
        }

        @Override
        public String toString() {
            return pos + ":" + xi;
        }

    }

}
