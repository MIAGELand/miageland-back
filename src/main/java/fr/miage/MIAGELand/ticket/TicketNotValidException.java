package fr.miage.MIAGELand.ticket;

/**
 * Exception thrown when a ticket is not valid.
 */
public class TicketNotValidException extends Exception {
    public TicketNotValidException(String message) {
        super(message);
    }
}
