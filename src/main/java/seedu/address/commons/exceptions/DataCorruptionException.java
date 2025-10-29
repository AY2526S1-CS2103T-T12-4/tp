package seedu.address.commons.exceptions;

/**
 * Represents an error when data integrity is compromised (e.g., manual modification of data files).
 */
public class DataCorruptionException extends Exception {

    public DataCorruptionException(String message) {
        super(message);
    }

    public DataCorruptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
