package signal.model;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A lookup table for various food group statements for a food group.
 */
public class DirectionalStatementLookup {
    private final Map<FoodGroupID, Set<String>> lookup = new EnumMap<>(FoodGroupID.class);

    public DirectionalStatementLookup(final List<DirectionalStatement> statements) {
        Objects.requireNonNull(statements);

        // Convert a flat list of entries into a multi map
        for (final DirectionalStatement directionalStatement : statements) {
            final Set<String> entries = lookup.getOrDefault(directionalStatement.foodGroupID, new HashSet<>());
            entries.add(directionalStatement.statement);

            lookup.put(directionalStatement.foodGroupID, entries);
        }
    }
}
