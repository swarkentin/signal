package foodguide.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static foodguide.service.JsonUtilities.getListOfElements;

/**
 * A lookup table for various food group statements for a food group.
 */
public class DirectionalStatementLookup {
    private static final TypeReference<List<DirectionalStatement>> TYPE = new TypeReference<List<DirectionalStatement>>() {
    };

    private static final Random rand = new Random();
    private final Map<FoodGroupID, Set<String>> lookup = new EnumMap<>(FoodGroupID.class);

    private DirectionalStatementLookup(final List<DirectionalStatement> statements) {
        Objects.requireNonNull(statements);

        // Convert a flat list of entries into a multi map
        for (final DirectionalStatement directionalStatement : statements) {
            final Set<String> entries = lookup.getOrDefault(directionalStatement.foodGroupID, new HashSet<>());
            entries.add(directionalStatement.statement);

            lookup.putIfAbsent(directionalStatement.foodGroupID, entries);
        }
    }

    /**
     * Get a random statement for a given food group;
     */
    public String randomStatement(final FoodGroupID id){
        final Set<String> statements = lookup.getOrDefault(id, ImmutableSet.of());
        return Iterables.get(statements, rand.nextInt(statements.size()));
    }

    /**
     * Create a new lookup instance by reading from a JSON {@link InputStream}.
     */
    public static DirectionalStatementLookup read(InputStream in) throws IOException {
        return new DirectionalStatementLookup(getListOfElements(in, TYPE));
    }
}
