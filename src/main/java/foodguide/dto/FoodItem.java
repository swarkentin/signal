package foodguide.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A serialized object which represents a food and its serving sizes
 */
public class FoodItem {

    public final String food;
    public final String categoryDescription;
    public final String servingSize;

    public FoodItem(final @JsonProperty("food)") String food,
                    final @JsonProperty("categoryDescription") String categoryDescription,
                    final @JsonProperty("servingSize") String servingSize) {
        this.food = food;
        this.categoryDescription = categoryDescription;
        this.servingSize = servingSize;
    }
}
