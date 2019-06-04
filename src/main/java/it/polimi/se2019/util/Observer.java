package it.polimi.se2019.util;

public interface Observer<T> {

    public class CommunicationError extends Exception {
        public CommunicationError(String errorMessage) {
            super(errorMessage);
        }
    }

    void update(T message) throws CommunicationError;
}