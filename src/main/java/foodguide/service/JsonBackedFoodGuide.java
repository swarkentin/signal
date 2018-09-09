package foodguide.service;

import foodguide.controller.Identification;
import foodguide.controller.UnsupportedLocaleException;
import foodguide.dto.FoodItem;
import foodguide.dto.SingleDailyMenu;
import foodguide.model.*;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.server.exceptions.HttpServerException;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static foodguide.service.JsonUtilities.readResource;
import static org.slieb.throwables.FunctionWithThrowable.castFunctionWithThrowable;

/**
 * A {@link FoodGuide} that is backed by JSON documents.
 * <p>
 * Food guide dataset was downloaded from
 * <p>
 * https://open.canada.ca/data/en/dataset/e5f4a98e-0ccf-4e5e-9912-d308b46c5a7f
 */
@Singleton
public class JsonBackedFoodGuide implements FoodGuide<JsonBackedFoodGuide> {
    private final Random rand = new Random();

    private final Locale[] locales = new Locale[]{Locale.ENGLISH, Locale.FRENCH};
    private final Map<Locale, DirectionalStatementLookup> directionalStatementLookup = new HashMap<>();
    private final Map<Locale, FoodGroupLookup> foodGroupLookup = new HashMap<>();
    private final Map<Locale, FoodLookup> foodLookup = new HashMap<>();
    private final Map<Locale, ServingLookup> servingLookup = new HashMap<>();

    @PostConstruct
    public JsonBackedFoodGuide read() throws IOException {
        //
        // Read Food Group files for each locale
        //
        for (final Locale locale : locales) {
            final String lang = locale.getLanguage();

            final String directionalStatementResource = String.format("/foodguide/%1$s/fg_directional_satements-%1$s.json", lang);
            final String foodGroupsResource = String.format("/foodguide/%1$s/foodgroups-%1$s.json", lang);
            final String foodResource = String.format("/foodguide/%1$s/foods-%1$s.json", lang);
            final String servingResource = String.format("/foodguide/%1$s/servings_per_day-%1$s.json", lang);

            directionalStatementLookup.put(locale, read(directionalStatementResource, castFunctionWithThrowable(DirectionalStatementLookup::read)));
            foodGroupLookup.put(locale, read(foodGroupsResource, castFunctionWithThrowable(FoodGroupLookup::read)));
            foodLookup.put(locale, read(foodResource, castFunctionWithThrowable(FoodLookup::read)));
            servingLookup.put(locale, read(servingResource, castFunctionWithThrowable(ServingLookup::read)));

        }

        return this;
    }

    private static <T> T read(final String resource, final Function<InputStream, T> readLookup) throws IOException {
        try (final InputStream in = readResource(resource)) {
            return readLookup.apply(in);
        }
    }

    @Override
    public SingleDailyMenu createDailyMenu(final List<Locale.LanguageRange> languageRanges, final Identification identification)
            throws UnsupportedLocaleException {
        final Locale locale = getLocaleForLanguageRanges(languageRanges);
        final FoodGroupLookup foodGroupLookForLocale = lookupForLocale(locale, foodGroupLookup);
        final FoodLookup foodLookupForLocale = lookupForLocale(locale, foodLookup);
        final DirectionalStatementLookup statementLookup = lookupForLocale(locale, directionalStatementLookup);
        final EnumMap<FoodGroupID, String> servingByFoodGroup = lookupForLocale(locale, servingLookup)
                .findServings(identification.gender, identification.age);

        final Map<String, String> tipsByFoodGroup = new HashMap<>();
        final Map<String, List<FoodItem>> foodsByFoodGroup = new HashMap<>();
        final Set<FoodGroupID> foodGroups = servingByFoodGroup.keySet();
        for (final FoodGroupID fgId : foodGroups) {
            final FoodGroup foodGroup = foodGroupLookForLocale.get(fgId);

            // First determine the number of servings for a given food group
            final int servings = parseServings(servingByFoodGroup.get(fgId));

            // Get some random tips for the food group
            tipsByFoodGroup.put(foodGroup.description, statementLookup.randomStatement(fgId));

            // Build a random list of food items for the food group
            final List<Food> foodForGroup = foodLookupForLocale.getByFood(fgId);
            final List<FoodItem> foodItems = IntStream.range(0, servings)
                    .mapToObj(i -> foodForGroup.get(rand.nextInt(foodForGroup.size())))
                    .map(f -> new FoodItem(f.name, foodGroupLookForLocale.getCategoryDescription(f.foodGroupCategoryID), f.servingSize))
                    .collect(Collectors.toList());
            foodsByFoodGroup.put(foodGroup.description, foodItems);
        }

        return new SingleDailyMenu(identification.name,
                identification.age,
                tipsByFoodGroup,
                foodsByFoodGroup);
    }

    private Locale getLocaleForLanguageRanges(final List<Locale.LanguageRange> languageRanges) throws UnsupportedLocaleException {
        if(languageRanges.size() == 0){
            return Locale.ENGLISH;
        }

        final List<Locale> matching = Locale.filter(languageRanges, Arrays.asList(this.locales));
        if(matching.size() > 0){
            return matching.get(0);
        }

        throw new UnsupportedLocaleException(String.format("Unsupported language. Supported languages include '%s'.",
                Arrays.stream(locales).map(Locale::getLanguage).collect(Collectors.joining(","))));
    }

    private int parseServings(final String servingString) {
        // Servings come in either a range or an exact count.
        // In the case of a range, randomly pick some number inside the range.
        final String[] boundRange = servingString.split(" ");
        if (boundRange.length == 3) {
            return rand.ints(Integer.parseInt(boundRange[0]), Integer.parseInt(boundRange[2])).findFirst()
                    .orElseThrow(() -> new HttpServerException(String.format("Failed to parse serving string '%s'.", servingString)));
        }

        // Otherwise return the exact serving size
        return Integer.parseInt(servingString);
    }

    private <T> T lookupForLocale(final Locale locale, Map<Locale, T> byLocaleMap) {
        return Optional.ofNullable(byLocaleMap.get(locale))
                .orElseThrow(() -> {
                    throw new HttpClientResponseException(String.format("Locale not supported '%s'.", locale.getLanguage()),
                            HttpResponse.status(HttpStatus.NOT_ACCEPTABLE));
                });
    }

}
