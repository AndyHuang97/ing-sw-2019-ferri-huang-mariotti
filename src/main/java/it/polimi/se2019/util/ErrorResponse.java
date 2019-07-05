package it.polimi.se2019.util;

/**
 * Error response class
 *
 * @author AH
 *
 */
public class ErrorResponse {
    private String errorMessage;

    /**
     * The constructor for the class.
     *
     * @param errorMessage the string message
     *
     */
    public ErrorResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * To string method
     *
     * @return the error message
     *
     */
    public String toString() {
        return errorMessage;
    }
}
