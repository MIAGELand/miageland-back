package fr.miage.MIAGELand.attraction;

public class AttractionNotFoundException extends Exception {
    public AttractionNotFoundException(Long id) {
        super("Could not find attraction " + id);
    }
}
