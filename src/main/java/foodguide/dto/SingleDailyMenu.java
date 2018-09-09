package foodguide.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * The response DTO for the daily menu for one person.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SingleDailyMenu {

    public final String name;
    public final int age;
    public final Map<String, String> tipsByFoodGroup;
    public final Map<String, List<FoodItem>> foodsByFoodGroup;

    public SingleDailyMenu(final @JsonProperty("name") String name,
                           final @JsonProperty("age") int age,
                           final @JsonProperty("tipsByFoodGroup") Map<String, String> tipsByFoodGroup,
                           final @JsonProperty("foodsByFoodGroup") Map<String, List<FoodItem>> foodsByFoodGroup) {
        this.name = name;
        this.age = age;
        this.tipsByFoodGroup = tipsByFoodGroup;
        this.foodsByFoodGroup = foodsByFoodGroup;
    }
}
