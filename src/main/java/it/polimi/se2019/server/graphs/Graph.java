package it.polimi.se2019.server.graphs;

import java.util.*;

public class Graph<T> {
    private Map<Vertex<T>, List<Vertex<T>>> adjacentVertices;

    void addVertex(T content) {
        adjacentVertices.putIfAbsent(new Vertex(content), new ArrayList<>());
    }

    void addEdge(T content1, T content2) {
        Vertex firstVertex = new Vertex(content1);
        Vertex secondVertex = new Vertex(content2);

        adjacentVertices.get(firstVertex).add(secondVertex);
        adjacentVertices.get(secondVertex).add(firstVertex);
    }

    List<Vertex<T>> getAdjacentVertices(T content) {
        return adjacentVertices.get(new Vertex(content));
    }

    Boolean isReachable(T start, T end) {
        Set<T> visited = new LinkedHashSet<T>();
        Stack<T> stack = new Stack<T>();
        stack.push(start);
        while (!stack.isEmpty()) {
            T vertexContent = stack.pop();
            if (vertexContent == end) {
                return true;
            }
            else if (!visited.contains(vertexContent)) {
                visited.add(vertexContent);

                for (Vertex<T> vertex : this.getAdjacentVertices(vertexContent)) {
                    stack.push(vertex.getContent());
                }
            }
        }

        return false;
    }
}
