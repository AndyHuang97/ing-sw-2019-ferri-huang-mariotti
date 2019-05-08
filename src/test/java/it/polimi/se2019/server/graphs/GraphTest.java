package it.polimi.se2019.server.graphs;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GraphTest {
    Graph<String> graph;

    @Before
    public void setUp() {
        graph = new Graph<>();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testIsReachable() {
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("A", "C");
        boolean a = graph.isReachable("A", "B",10);
        boolean b = graph.isReachable("B", "C",10);
        boolean c = graph.isReachable("A", "C",10);

        Assert.assertTrue(a);
        Assert.assertTrue(b);
        Assert.assertTrue(c);
    }

    @Test
    public void testIsReachableParam() {
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");
        graph.addVertex("E");
        graph.addVertex("F");
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("C", "D");
        graph.addEdge("C", "E");
        graph.addEdge("D", "F");

        boolean a = graph.isReachable("A", "E",10);
        Assert.assertTrue(a);
        boolean b = graph.isReachable("A", "E",2);
        Assert.assertFalse(b);
        boolean c = graph.isReachable("A", "B",1);
        Assert.assertTrue(c);
        boolean d = graph.isReachable("A", "F",4);
        Assert.assertTrue(d);
    }
}
