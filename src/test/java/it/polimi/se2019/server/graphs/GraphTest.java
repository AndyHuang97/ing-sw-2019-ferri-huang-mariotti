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
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");
        graph.addVertex("E");
        graph.addVertex("F");
        graph.addVertex("Z");
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("C", "D");
        graph.addEdge("C", "E");
        graph.addEdge("D", "F");
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testIsReachableParam() {

        boolean a = graph.isReachable("A", "E",10);
        Assert.assertTrue(a);
        boolean b = graph.isReachable("A", "E",2);
        Assert.assertFalse(b);
        boolean c = graph.isReachable("A", "B",1);
        Assert.assertTrue(c);
        boolean d = graph.isReachable("A", "F",4);
        Assert.assertTrue(d);
        boolean f = graph.isReachable("A", "Z",10);
        Assert.assertFalse(f);
    }

    @Test
    public void testDistance() {
        Assert.assertEquals(-1, graph.distance("A", "Z").intValue());
        Assert.assertEquals(1, graph.distance("A", "B").intValue());
        Assert.assertEquals(2, graph.distance("A", "C").intValue());
        Assert.assertEquals(3, graph.distance("A", "D").intValue());
        Assert.assertEquals(3, graph.distance("A", "E").intValue());
        Assert.assertEquals(4, graph.distance("A", "F").intValue());
        Assert.assertEquals(2, graph.distance("D", "E").intValue());
    }
}
