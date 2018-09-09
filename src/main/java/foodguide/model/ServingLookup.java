package foodguide.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableSortedMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static foodguide.service.JsonUtilities.getListOfElements;

/**
 * A lookup table for {@link Serving} for a given persona (sex/age)
 */
public class ServingLookup {
    private static final TypeReference<List<Serving>> TYPE = new TypeReference<List<Serving>>() {
    };
    private final EnumMap<Gender, ImmutableSortedMap<Integer, EnumMap<FoodGroupID, String>>> servingLookupByAge;

    private ServingLookup(final List<Serving> servings) {
        Objects.requireNonNull(servings);

        final Map<Integer, EnumMap<FoodGroupID, String>> maleMap = new HashMap<>();
        final Map<Integer, EnumMap<FoodGroupID, String>> femaleMap = new HashMap<>();

        for (final Serving serving : servings) {
            switch (serving.gender) {
                case Male:
                    add(maleMap, serving);
                    break;
                case Female:
                    add(femaleMap, serving);
                    break;
            }
        }

        servingLookupByAge = new EnumMap<>(Gender.class);
        servingLookupByAge.put(Gender.Male, ImmutableSortedMap.<Integer, EnumMap<FoodGroupID, String>>naturalOrder().putAll(maleMap).build());
        servingLookupByAge.put(Gender.Female, ImmutableSortedMap.<Integer, EnumMap<FoodGroupID, String>>naturalOrder().putAll(femaleMap).build());
    }

    /**
     * Create a new lookup instance by reading from a JSON {@link InputStream}.
     */
    public static ServingLookup read(InputStream in) throws IOException {
        return new ServingLookup(getListOfElements(in, TYPE));
    }

    private static void add(final Map<Integer, EnumMap<FoodGroupID, String>> map, final Serving serving) {
        final int age = floor(serving.ages);
        final EnumMap<FoodGroupID, String> servingsByFoodGroup = map.getOrDefault(age, new EnumMap<>(FoodGroupID.class));
        servingsByFoodGroup.put(serving.foodGroupID, serving.servings);
        map.putIfAbsent(age, servingsByFoodGroup);
    }

    /**
     * Raw JSON uses bound and unbound ages ranges (localized) for each {@link Serving}.
     * <p>
     * For example:
     * <pre>
     *     {
     *       "fgid": "mi",
     *       "gender": "Male",
     *       "ages": "51 à 70", <<<< bound range
     *       "servings": "3"
     *     },
     *     {
     *       "fgid": "mi",
     *       "gender": "Female",
     *       "ages": "71+",     <<<< unbound range
     *       "servings": "3"
     *     },
     * </pre>
     *
     * @param ageRange Raw age range string
     * @return The floor of an age range, if one is specified, otherwise the exact age value
     */
    private static int floor(final String ageRange) {
        // A bound range is split into 3 parts [ 1 to 3 ]
        final String[] boundRange = ageRange.split(" ");
        if (boundRange.length == 3) {
            return Integer.parseInt(boundRange[0]);
        }

        // Otherwise the value is an unbound range [ 25+ ]. This just trims off any trailing characters.
        return Integer.parseInt(ageRange.substring(0, ageRange.length() - 1));
    }

    /**
     * Finds the best match {@link Serving} for a given {@link Gender} and age.
     */
    public EnumMap<FoodGroupID, String> findServings(final Gender gender, final int age) {

        // Find the closest match by taking the floor match of an age.
        return servingLookupByAge.get(gender).floorEntry(age).getValue();
    }
}
