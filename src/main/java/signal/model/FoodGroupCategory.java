package signal.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A deserialization of a food group category
 */
public class FoodGroupCategory {
    final int id;

    final String description;

    public FoodGroupCategory(final @JsonProperty("fgcat_id") int id,
                             final @JsonProperty("fgcat") String description) {
        this.id = id;
        this.description = description;
    }
}
