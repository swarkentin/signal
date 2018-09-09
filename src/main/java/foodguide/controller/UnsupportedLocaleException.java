package foodguide.controller;

/**
 * Thrown when an a request is made for a locale that is not supported
 */
public class UnsupportedLocaleException extends Exception {

    public UnsupportedLocaleException(String message) {
        super(message);
    }
}
