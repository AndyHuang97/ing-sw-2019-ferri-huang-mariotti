package it.polimi.se2019.server.graphs;

import java.util.Objects;

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

        if (o == null) {
            return false;
        }

        if (getClass() != o.getClass()) {
            return false;
        }

        Vertex<T> objectToCompare = (Vertex<T>) o;

        return Objects.equals(objectToCompare.getContent(), this.getContent())
                && Objects.equals(this.getContent(), objectToCompare.getContent());
    }

    // TODO: implement hashCode :(
    @Override
    public int hashCode() {
        return Objects.hash(content);
    }
}