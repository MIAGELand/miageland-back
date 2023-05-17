package fr.miage.MIAGELand.park;

public class IllegalGaugeException extends Exception {

        public IllegalGaugeException() {
            super("The gauge is not valid.");
        }

        public IllegalGaugeException(String message) {
            super(message);
        }
}
