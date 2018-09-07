package foodguide.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The response DTO for the daily menu for one person.
 */
public class SingleDailyMenu {

    private final String name;

    public SingleDailyMenu(@JsonProperty("name") String name) {
        this.name = name;
    }

    @JsonProperty
    public String getName() {
        return name;
    }
}
