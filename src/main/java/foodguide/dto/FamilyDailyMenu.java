package foodguide.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The response DTO for the daily menu for one person.
 */
public class FamilyDailyMenu {

    public final Map<String, String> tipsByFoodGroup;
    public final Map<String, List<FoodItem>> foodsByFoodGroup;
    public final List<SingleDailyMenu> menuPerPerson;

    public FamilyDailyMenu(final @JsonProperty("tipsByFoodGroup") Map<String, String> tipsByFoodGroup,
                           final @JsonProperty("menuPerPerson") List<SingleDailyMenu> menuPerPerson) {
        this.tipsByFoodGroup = tipsByFoodGroup;
        this.menuPerPerson = menuPerPerson;
        this.foodsByFoodGroup = new HashMap<>();

        // Aggregate all the items from each individual menu into the overall food by foodgroup map
        for (final SingleDailyMenu menu : menuPerPerson) {
            for (final Map.Entry<String, List<FoodItem>> entry : menu.foodsByFoodGroup.entrySet()) {
                List<FoodItem> items = foodsByFoodGroup.getOrDefault(entry.getKey(), new ArrayList<>());
                items.addAll(entry.getValue());
                foodsByFoodGroup.putIfAbsent(entry.getKey(), items);
            }
        }
    }
}
