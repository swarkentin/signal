package foodguide.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A Deserialization of an entry from the <em>foods-[locale].json</em> file.
 */
public class Food {
    final FoodGroupID foodGroupID;

    final int foodGroupCategoryID;

    final String servingSize;

    final String name;

    public Food(final @JsonProperty("fgid") FoodGroupID foodGroupID,
                final @JsonProperty("fgcat_id") int foodGroupCategoryID,
                final @JsonProperty("srvg_sz") String servingSize,
                final @JsonProperty("food") String name) {
        this.foodGroupID = foodGroupID;
        this.foodGroupCategoryID = foodGroupCategoryID;
        this.servingSize = servingSize;
        this.name = name;
    }
}
