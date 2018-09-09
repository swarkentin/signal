package foodguide.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * A Deserialization of an entry from the <em>foodgroups-[locale].json</em> file.
 */
public class FoodGroup {
    public final FoodGroupID foodGroupID;

    public final String description;

    public final List<FoodGroupCategory> categories;

    public FoodGroup(final @JsonProperty("fgid") FoodGroupID foodGroupID,
                     final @JsonProperty("foodgroup")  String description,
                     final @JsonProperty("categories") List<FoodGroupCategory> categories) {
        this.foodGroupID = foodGroupID;
        this.description = description;
        this.categories = categories;
    }
}
