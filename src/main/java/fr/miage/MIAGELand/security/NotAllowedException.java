package fr.miage.MIAGELand.security;

/**
 * Exception thrown when a user is not allowed to do something.
 */
public class NotAllowedException extends Exception {

    public NotAllowedException() {
        super("You are not allowed to do this.");
    }

    public NotAllowedException(String message) {
        super(message);
    }
}
