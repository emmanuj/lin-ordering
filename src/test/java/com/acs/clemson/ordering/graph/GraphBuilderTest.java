/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acs.clemson.ordering.graph;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author emmanuj
 */
public class GraphBuilderTest {
    
    public GraphBuilderTest() {
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
     * Test of round method, of class GraphBuilder.
     */
    @org.junit.Test
    public void testRound() {
        System.out.println("round");
        double x = 0.0005;
        int sig = 5;
        double expResult = 0.0005;
        double result = GraphBuilder.round(x, sig);
        assertEquals(expResult, result, 0.0);
    }
    
}
