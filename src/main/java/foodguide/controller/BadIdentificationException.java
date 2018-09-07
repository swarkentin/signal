package foodguide.controller;

/**
 * Thrown when an identification string cannot be parsed
 */
public class BadIdentificationException extends Exception {

    public BadIdentificationException(String message) {
        super(message);
    }
}
