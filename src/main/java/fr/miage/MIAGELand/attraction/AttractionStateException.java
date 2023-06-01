package fr.miage.MIAGELand.attraction;

/**
 * Exception thrown when an attraction is not in a valid state.
 */
public class AttractionStateException extends Exception {
    public AttractionStateException(String message) {
        super(message);
    }
}
