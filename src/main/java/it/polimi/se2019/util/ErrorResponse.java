package it.polimi.se2019.util;

/**
 * This class is basically a wrapper of the String class, this class was designed to have more functionality
 * but due to lack of time they were never implemented.
 *
 * @author Rodolfo Mariotti
 */
public class ErrorResponse {
    private String errorMessage;

    /**
     * Create a new ErrorMessage object that contains the description of the error.
     *
     * @param errorMessage description of the error
     */
    public ErrorResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Get the text representation of the object.
     *
     * @return description of the error
     */
    public String toString() {
        return errorMessage;
    }
}
