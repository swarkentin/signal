package foodguide.service;

import foodguide.controller.BadIdentificationException;
import foodguide.controller.Identification;
import foodguide.dto.SingleDailyMenu;
import foodguide.model.Gender;

import java.io.IOException;

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
     * @return a new menu
     */
    SingleDailyMenu createDailyMenu(final Identification identification);
}
