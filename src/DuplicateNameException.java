/**
 * This exception is for handling duplicate student names in the application.
 * The exception is thrown when attempting to register a student with a name that already exists.
 */
public class DuplicateNameException extends Exception {
    /**
     * Constructs a new DuplicateNameException with the specified detail message.
     * @param message the detail message
     */
    public DuplicateNameException(String message) {
        super(message);
    }
}