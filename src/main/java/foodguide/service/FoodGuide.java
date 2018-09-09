package foodguide.service;

import foodguide.controller.Identification;
import foodguide.controller.UnsupportedLocaleException;
import foodguide.dto.SingleDailyMenu;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * The API for accessing the Canada Food Guide
 */
public interface FoodGuide<TSubType> {

    /**
     * Reads from the source data.
     *
     * @return self
     */
    TSubType read() throws IOException;

    /**
     * Creates a daily menu for an individual.
     *
     * @param locale         The target locale of the menu returned
     * @param identification The identification of the person for whom to make the menu
     * @return a new menu
     * @throws UnsupportedLocaleException if the {@link java.util.Locale.LanguageRange} provided cannot be matched to a locale
     */
    SingleDailyMenu createDailyMenu(final List<Locale.LanguageRange> locale, final Identification identification)
            throws UnsupportedLocaleException;
}
