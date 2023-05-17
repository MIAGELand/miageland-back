package fr.miage.MIAGELand.security;

public class NotAllowedException extends Exception {

    public NotAllowedException() {
        super("You are not allowed to do this.");
    }

    public NotAllowedException(String message) {
        super(message);
    }
}
