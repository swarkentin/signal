package signal.model;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A foodGroup Lookup table for various food group statements for a food group.
 */
public class FoodGroupLookup {
    private final Map<FoodGroupID, Set<FoodGroup>> foodGroupLookup = new EnumMap<>(FoodGroupID.class);
    private final Map<Integer, String> categoryLookup = new HashMap<>();

    public FoodGroupLookup(final List<FoodGroup> foodGroups) {
        Objects.requireNonNull(foodGroups);

        // Convert a flat list of entries into a multi map
        for (final FoodGroup fg : foodGroups) {
            final Set<FoodGroup> entries = foodGroupLookup.getOrDefault(fg.foodGroupID, new HashSet<>());
            entries.add(fg);

            foodGroupLookup.put(fg.foodGroupID, entries);

            // Categories are unique, so no need to branch on existing key
            for (final FoodGroupCategory category : fg.categories) {
                categoryLookup.put(category.id, category.description);
            }
        }
    }
}
