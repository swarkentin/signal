package signal.api;

import java.io.IOException;

/**
 * The API for accessing the Canada Food Guide
 */
public interface FoodGuide<TSubType> {

    /**
     * Reads from the source data.
     * @return self
     */
    TSubType read() throws IOException;
}
