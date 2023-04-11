package fr.miage.MIAGELand.ticket;

public class TicketNotValidException extends Exception {
    public TicketNotValidException(String message) {
        super(message);
    }
}
