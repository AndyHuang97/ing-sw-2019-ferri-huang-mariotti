package it.polimi.se2019.server.graphs;

import java.util.*;

/**
 * Basic implementation of a Graph, it's used for distance calculation in the Board.
 *
 * @param <T> type of vertex of the graph
 * @author Andrea Huang
 */
public class Graph<T> {
    private Map<Vertex<T>, List<Vertex<T>>> adjacentVertices;

    /**
     * Constructor used to initialize a blank graph.
     */
    public Graph() {
        this.adjacentVertices = new HashMap<>();
    }

    /**
     * Method to add a vertex in the graph.
     *
     * @param content content of the vertex
     */
    public void addVertex(T content) {
        adjacentVertices.putIfAbsent(new Vertex(content), new ArrayList<>());
    }

    /**
     * Ad an edge between two vertex.
     *
     * @param content1 starting vertex
     * @param content2 final vertex
     */
    public void addEdge(T content1, T content2) {
        Vertex firstVertex = new Vertex(content1);
        Vertex secondVertex = new Vertex(content2);

        adjacentVertices.get(firstVertex).add(secondVertex);
        adjacentVertices.get(secondVertex).add(firstVertex);
    }

    /**
     * Get all adjacent vertex of a vertex.
     *
     * @param content target vertex
     * @return vertexes adjacent to the target vertex
     */
    public List<Vertex<T>> getAdjacentVertices(T content) {
        return adjacentVertices.get(new Vertex(content));
    }

    /**
     * This method determines if two nodes are reachable in a given amount of steps (maxDistance).
     *
     * @param start start is the root node.
     * @param end end is the final node we are looking for.
     * @param maxDistance maxDistance is the maximum amount of steps the method will look for the end node.
     * @return false if the end node is not reachable, true otherwise.
     */
    public Boolean isReachable(T start, T end, int maxDistance) {

        Integer distance = distance(start, end);
        boolean reachable = false;

        if (maxDistance >= distance && distance != -1){
                reachable = true;
        }

        return reachable;
    }

    /**
     * This methods computes the distance between two nodes using a breadth-first search.
     * It returns a positive value if the nodes are connected, a negative value (-1) otherwise.
     * @param start is the starting node.
     * @param end is the node we are calculating the distance from start node.
     * @return the distance between the nodes, if the nodes are disconnected it returns -1.
     */
    public Integer distance(T start, T end) {
        Integer distance = -1;

        int i = 0;
        T vertexContent = null;
        List<T> visited = new ArrayList<>();
        Queue<T> queue = new LinkedList<>();
        T lastInLevel = null;

        queue.add(start);
        visited.add(start);
        while (!queue.isEmpty() && vertexContent != end) {
            vertexContent = queue.poll();
            if (vertexContent == end) {
                distance = i;
            }
            else {
                for (Vertex<T> vertex : this.getAdjacentVertices(vertexContent)) {
                    if (!visited.contains(vertex.getContent())) {
                        visited.add(vertex.getContent());
                        queue.add(vertex.getContent());
                    }
                }
            }
            if(lastInLevel == vertexContent || lastInLevel == null) {
                lastInLevel = visited.get(visited.size()-1);
                i++;
            }
        }

        return distance;
    }
}
