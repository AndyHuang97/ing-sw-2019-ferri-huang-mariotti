package it.polimi.se2019.util;

/**
 * This is a key part, the observer waits for an update by the observable
 *
 * @author AH
 *
 */
public interface Observer<T> {

    /**
     * The exception that notify if something is wrong
     *
     */
    public class CommunicationError extends Exception {
        public CommunicationError(String errorMessage) {
            super(errorMessage);
        }
    }

    /**
     * This method is used by the observer to update the observers with the message
     *
     * @param message the content
     * @throws CommunicationError if something goes wrong during communication
     *
     */
    void update(T message) throws CommunicationError;
}