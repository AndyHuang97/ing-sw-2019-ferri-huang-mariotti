package it.polimi.se2019.server.graphs;

public class AppTest {
    public static void main() {
        Graph<String> graph = new Graph<String>();
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("A", "C");

        boolean a = graph.isReachable("A", "B");
        boolean b = graph.isReachable("B", "C");
        boolean c = graph.isReachable("A", "C");

        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
    }
}
