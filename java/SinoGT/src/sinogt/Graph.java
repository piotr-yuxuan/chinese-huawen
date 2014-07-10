/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sinogt;

import org.jgrapht.*;
import org.jgrapht.generate.*;
import org.jgrapht.graph.*;
import org.jgrapht.traverse.*;

/**
 *
 * @author caocoa
 */
public class Graph {

    public void TestMethod() {
        DirectedPseudograph g = new DirectedPseudograph(DefaultEdge.class);
        Object[] o;
        o = new Object[10];
        for (int i = 0; i < o.length; i++) {
            o[i] = new Object();
        }
        g.addVertex(null);
        System.out.println(g.isAllowingMultipleEdges());
    }
}
