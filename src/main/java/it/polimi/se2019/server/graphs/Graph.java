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
     * This method employs breadth-first search limited to a certain depth(distance).
     * @param start start is the root node.
     * @param end end is the final node we are looking for.
     * @param distance distance is the maximum depth the method will loof for the end node.
     * @return
     */
    public Boolean isReachable(T start, T end, int distance) {

        int i = 0;
        List<T> visited = new ArrayList<>();
        Queue<T> queue = new LinkedList<>();
        T lastInLevel = null;

        queue.add(start);
        visited.add(start);
        while (!queue.isEmpty() && i <= distance) {
            T vertexContent = queue.poll();
            if (vertexContent == end) {
                return true;
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

        return false;
    }
}
