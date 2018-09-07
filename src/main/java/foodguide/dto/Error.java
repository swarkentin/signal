package foodguide.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A serializable document for returning a server error
 */
public class Error {
    @JsonProperty
    private final String message;

    public Error(String message) {
        this.message = message;
    }

}
