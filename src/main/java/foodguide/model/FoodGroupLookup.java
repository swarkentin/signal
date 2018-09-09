package foodguide.model;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static foodguide.service.JsonUtilities.getListOfElements;

/**
 * A description Lookup table for various food group statements for a food group.
 */
public class FoodGroupLookup {
    private static final TypeReference<List<FoodGroup>> TYPE = new TypeReference<List<FoodGroup>>() {
    };
    private final Map<FoodGroupID, FoodGroup> foodGroupLookup = new EnumMap<>(FoodGroupID.class);
    private final Map<Integer, String> categoryLookup = new HashMap<>();

    private FoodGroupLookup(final List<FoodGroup> foodGroups) {
        Objects.requireNonNull(foodGroups);

        // Convert a flat list of entries into a multi map
        for (final FoodGroup fg : foodGroups) {
            foodGroupLookup.put(fg.foodGroupID, fg);

            // Categories are unique, so no need to branch on existing key
            for (final FoodGroupCategory category : fg.categories) {
                categoryLookup.putIfAbsent(category.id, category.description);
            }
        }
    }

    /**
     * Get the {@link FoodGroup} matching the id.
     */
    public FoodGroup get(final FoodGroupID foodGroupID){
        return foodGroupLookup.get(foodGroupID);
    }

    /**
     * Get the category description for a food group category id.
     */
    public String getCategoryDescription(final int categoryId){
        return categoryLookup.getOrDefault(categoryId, "");
    }

    /**
     * Create a new lookup instance by reading from a JSON {@link InputStream}.
     */
    public static FoodGroupLookup read(InputStream in) throws IOException {
        return new FoodGroupLookup(getListOfElements(in, TYPE));
    }
}
