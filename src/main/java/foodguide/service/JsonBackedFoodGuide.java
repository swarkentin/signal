package foodguide.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import foodguide.controller.Identification;
import foodguide.dto.SingleDailyMenu;
import foodguide.model.DirectionalStatement;
import foodguide.model.DirectionalStatementLookup;
import foodguide.model.Food;
import foodguide.model.FoodGroup;
import foodguide.model.FoodGroupLookup;
import foodguide.model.FoodLookup;
import foodguide.model.Gender;
import foodguide.model.Serving;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A {@link FoodGuide} that is backed by JSON documents.
 * <p>
 * Food guide dataset was downloaded from
 * <p>
 * https://open.canada.ca/data/en/dataset/e5f4a98e-0ccf-4e5e-9912-d308b46c5a7f
 */
@Singleton
public class JsonBackedFoodGuide implements FoodGuide<JsonBackedFoodGuide> {
    private final ObjectMapper mapper = new ObjectMapper()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
    private final Locale[] locales = new Locale[]{Locale.ENGLISH, Locale.FRENCH};
    private final Map<Locale, DirectionalStatementLookup> directionalStatementLookup = new HashMap<>();
    private final Map<Locale, FoodGroupLookup> foodGroupLookup = new HashMap<>();
    private final Map<Locale, FoodLookup> foodLookup = new HashMap<>();
    private final Map<Locale, List<Serving>> servingLookup = new HashMap<>();

    @PostConstruct
    public JsonBackedFoodGuide read() throws IOException {
        //
        // Read Food Group files for each locale
        //
        for (final Locale locale : locales) {
            final String lang = locale.getLanguage();

            // Read directional_statements
            try (final InputStream in = readFile(String.format("/foodguide/%1$s/fg_directional_satements-%1$s.json", lang))) {
                directionalStatementLookup.put(locale,
                        new DirectionalStatementLookup(getListOfElements(in, new TypeReference<List<DirectionalStatement>>() {
                        })));
            }

            // Read foodgroups
            try (final InputStream in = readFile(String.format("/foodguide/%1$s/foodgroups-%1$s.json", lang))) {
                foodGroupLookup.put(locale,
                        new FoodGroupLookup(getListOfElements(in, new TypeReference<List<FoodGroup>>() {
                        })));
            }

            // Read food list
            try (InputStream in = readFile(String.format("/foodguide/%1$s/foods-%1$s.json", lang))) {
                foodLookup.put(locale,
                        new FoodLookup(getListOfElements(in, new TypeReference<List<Food>>() {
                        })));
            }

            // Read serving sizes
            try (InputStream in = readFile(String.format("/foodguide/%1$s/servings_per_day-%1$s.json", lang))) {
                servingLookup.put(locale,
                        getListOfElements(in, new TypeReference<List<Serving>>() {
                        }));
            }
        }

        return this;
    }

    @Override
    public SingleDailyMenu createDailyMenu(Identification identification) {
        return new SingleDailyMenu(identification.name);
    }

    private <T> List<T> getListOfElements(InputStream in, TypeReference<List<T>> type) throws IOException {
        return mapper.readerFor(type).readValue(mapper.readTree(in).elements().next());
    }

    private InputStream readFile(final String fileName) {
        return JsonBackedFoodGuide.class.getResourceAsStream(fileName);
    }
}
