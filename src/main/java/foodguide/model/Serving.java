package foodguide.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A Deserialization of an entry from the <em>servings_per_day-[locale].json</em> file.
 */
public class Serving {
    final FoodGroupID foodGroupID;

    final Gender gender;

    final String ages;

    final String servings;

    public Serving(final @JsonProperty("fgid") FoodGroupID foodGroupID,
                   final @JsonProperty("gender") Gender gender,
                   final @JsonProperty("ages") String ages,
                   final @JsonProperty("servings") String servings) {
        this.foodGroupID = foodGroupID;
        this.gender = gender;
        this.ages = ages;
        this.servings = servings;
    }
}
