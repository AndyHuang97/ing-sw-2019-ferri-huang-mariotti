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
        boolean a = graph.isReachable("A", "B");
        boolean b = graph.isReachable("B", "C");
        boolean c = graph.isReachable("A", "C");

        Assert.assertTrue(a);
        Assert.assertTrue(b);
        Assert.assertTrue(c);
    }
}
