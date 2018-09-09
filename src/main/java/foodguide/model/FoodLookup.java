package foodguide.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static foodguide.service.JsonUtilities.getListOfElements;

/**
 * A food lookup table for various food group statements for a food.
 */
public class FoodLookup {
    private static final TypeReference<List<Food>> TYPE = new TypeReference<List<Food>>() {
    };
    private final Map<FoodGroupID, List<Food>> foodLookup = new EnumMap<>(FoodGroupID.class);

    private FoodLookup(final List<Food> foodList) {
        Objects.requireNonNull(foodList);

        // Convert a flat list of entries into a multi map
        for (final Food f : foodList) {
            final List<Food> foodByGroup = foodLookup.getOrDefault(f.foodGroupID, new ArrayList<>());
            foodByGroup.add(f);

            foodLookup.putIfAbsent(f.foodGroupID, foodByGroup);
        }
    }

    /**
     * Get a list of {@link Food} for the given food group
     */
    public final List<Food> getByFood(final FoodGroupID foodGroupID){
        return foodLookup.getOrDefault(foodGroupID, ImmutableList.of());
    }

    /**
     * Create a new lookup instance by reading from a JSON {@link InputStream}.
     */
    public static FoodLookup read(InputStream in) throws IOException {
        return new FoodLookup(getListOfElements(in, TYPE));
    }
}