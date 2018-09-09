package foodguide.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import foodguide.model.Gender;

import java.util.List;
import java.util.Map;

/**
 * The response DTO for the daily menu for one person.
 */
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
