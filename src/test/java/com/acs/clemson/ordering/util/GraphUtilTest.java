/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acs.clemson.ordering.util;

import com.acs.clemson.ordering.graph.Graph;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author emmanuj
 */
public class GraphUtilTest {
    
    public GraphUtilTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of computeAlgebraicDist method, of class GraphUtil.
     */
    @Test
    public void testComputeAlgebraicDist() {
        System.out.println("computeAlgebraicDist");
        Graph g = null;
        GraphUtil.computeAlgebraicDist(g);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of computeAlgebraicDistPar method, of class GraphUtil.
     */
    @Test
    public void testComputeAlgebraicDistPar() {
        System.out.println("computeAlgebraicDistPar");
        Graph g = null;
        GraphUtil.computeAlgebraicDistPar(g);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of doAlgDist method, of class GraphUtil.
     */
    @Test
    public void testDoAlgDist() {
        System.out.println("doAlgDist");
        Graph g = null;
        GraphUtil.doAlgDist(g);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of doStableMatching method, of class GraphUtil.
     */
    @Test
    public void testDoStableMatching() {
        System.out.println("doStableMatching");
        Graph g = null;
        ArrayList<Integer> seeds = null;
        int cap = 0;
        GraphUtil.doStableMatching(g, seeds, cap, false);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of minElement method, of class GraphUtil.
     */
    @Test
    public void testMinElement() {
        System.out.println("minElement");
        double[] a = null;
        double expResult = 0.0;
        double result = GraphUtil.minElement(a);
        //assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of maxElement method, of class GraphUtil.
     */
    @Test
    public void testMaxElement() {
        System.out.println("maxElement");
        double[] a = null;
        double expResult = 0.0;
        double result = GraphUtil.maxElement(a);
        //assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of randomInRange method, of class GraphUtil.
     */
    @Test
    public void testRandomInRange() {
        System.out.println("randomInRange");
        double min = 0.0;
        double max = 0.0;
        double expResult = 0.0;
        double result = GraphUtil.randomInRange(min, max);
        //assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }
    
}
