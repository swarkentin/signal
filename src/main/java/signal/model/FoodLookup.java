package signal.model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A food lookup table for various food group statements for a food.
 */
public class FoodLookup {
    private final Map<FoodGroupID, Map<Integer, List<Food>>> foodLookup = new EnumMap<>(FoodGroupID.class);

    public FoodLookup(final List<Food> foodList) {
        Objects.requireNonNull(foodList);

        // Convert a flat list of entries into a multi map
        for (final Food f : foodList) {
            final Map<Integer, List<Food>> foodByGroup = foodLookup.getOrDefault(f.foodGroupID, new HashMap<>());
            final List<Food> foodByCategory = foodByGroup.getOrDefault(f.foodGroupCategoryID, new ArrayList<>());
            foodByCategory.add(f);

            foodByGroup.put(f.foodGroupCategoryID, foodByCategory);
            foodLookup.put(f.foodGroupID, foodByGroup);
        }
    }
}