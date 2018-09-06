package signal.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A Deserialization of an entry from the <em>fg_directional_statements-[locale].json</em> file.
 */
public class DirectionalStatement {

    final FoodGroupID foodGroupID;

    final String statement;

    @JsonCreator
    public DirectionalStatement(@JsonProperty("fgid") final FoodGroupID foodGroupID,
                                @JsonProperty("dir_stmt") final String statement) {
        this.foodGroupID = foodGroupID;
        this.statement = statement;
    }
}
