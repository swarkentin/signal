package foodguide.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * A Deserialization of an entry from the <em>foodgroups-[locale].json</em> file.
 */
public class FoodGroup {
    final FoodGroupID foodGroupID;

    final String goodGroup;

    final List<FoodGroupCategory> categories;

    public FoodGroup(final @JsonProperty("fgid") FoodGroupID foodGroupID,
                     final @JsonProperty("foodgroup")  String foodGroup,
                     final @JsonProperty("categories") List<FoodGroupCategory> categories) {
        this.foodGroupID = foodGroupID;
        this.goodGroup = foodGroup;
        this.categories = categories;
    }
}
