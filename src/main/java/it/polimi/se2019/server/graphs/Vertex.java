package it.polimi.se2019.server.graphs;

import java.util.Objects;

/**
 * Container class. Is used by the Graph class to build a three data structure.
 *
 * @param <T> type of the object contained in the vertex
 * @author Rodolfo Mariotti
 */
public class Vertex<T> {
    private T content;

    /**
     * Build a new Vertex object.
     *
     * @param content object wrapped by the Vertex
     */
    public Vertex(T content) {
        this.content = content;
    }

    /**
     * Getter method for the content attribute.
     *
     * @return reference to the content attribute
     */
    public T getContent() {
        return content;
    }

    /**
     * Method used to compare two object by value.
     *
     * @param o object that will be compared with this Vertex
     * @return true if the object compared are equals, false otherwise
     */
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

    /**
     * This method returns the hash of the object.
     *
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(content);
    }
}