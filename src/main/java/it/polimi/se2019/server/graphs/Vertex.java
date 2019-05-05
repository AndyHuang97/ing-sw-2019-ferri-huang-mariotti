package it.polimi.se2019.server.graphs;

public class Vertex<T> {
    private T content;

    public Vertex(T content) {
        this.content = content;
    }

    public T getContent() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Vertex)) {
            return false;
        }

        Vertex<T> objectToCompare = (Vertex<T>) o;

        return objectToCompare.getContent() == this.getContent();
    }

    // TODO: implement hashCode :(
    @Override
    public int hashCode() {
        return 0;
    }
}