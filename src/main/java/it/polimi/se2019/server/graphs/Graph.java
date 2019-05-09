package it.polimi.se2019.server.graphs;

import java.util.*;

public class Graph<T> {
    private Map<Vertex<T>, List<Vertex<T>>> adjacentVertices;

    public Graph() {
        this.adjacentVertices = new HashMap<>();
    }

    public void addVertex(T content) {
        adjacentVertices.putIfAbsent(new Vertex(content), new ArrayList<>());
    }

    public void addEdge(T content1, T content2) {
        Vertex firstVertex = new Vertex(content1);
        Vertex secondVertex = new Vertex(content2);

        adjacentVertices.get(firstVertex).add(secondVertex);
        adjacentVertices.get(secondVertex).add(firstVertex);
    }

    public List<Vertex<T>> getAdjacentVertices(T content) {
        return adjacentVertices.get(new Vertex(content));
    }

    /**
     * This method determines if two nodes are reachable in a given amount of steps (maxDistance).
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
     * @return the distance between the nodes, if they nodes are disconnected it returns -1.
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
