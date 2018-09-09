package foodguide.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Utilities for interacting with JSON
 */
public final class JsonUtilities {
    private static final ObjectMapper mapper = new ObjectMapper().enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

    /**
     * Opens an {@link InputStream} for a resource in this module.
     *
     * @param resourceName The name of the resource
     * @return A new {@link InputStream} for the resource. It is the responsibility of the caller to close the resource after
     * they are finished using it.
     */
    public static InputStream readResource(final String resourceName) {
        return JsonUtilities.class.getResourceAsStream(resourceName);
    }

    /**
     * Return a list of elements from a JSON {@link InputStream}.
     *
     * For example, given some input:
     * <pre>
     *  {
     *   "servings to per to miy": [
     *     {
     *       "fgid": "vf",
     *       "gender": "Female",
     *       "ages": "2 to 3",
     *       "servings": "4"
     *     },
     *     {
     *       "fgid": "vf",
     *       "gender": "Male",
     *       "ages": "2 to 3",
     *       "servings": "4"
     *     }
     *   ]
     * }
     * </pre>
     *  this method will get the list of elements inside the <code>"servings to per to miy"</code> block. This was done to
     *  support JSON documents with localized root property names.
     * @param in The {@link InputStream} containing the JSON
     * @param type The {@link TypeReference} is used to deserialize the JSON elements into the appropriate type without the
     *             use of a wrapper class.
     * @param <T> The type of list elements to deserialize into.
     * @return A list of {@link T} elements deserialized from the input.
     * @throws IOException if there was any problem deserializing the input
     */
    public static <T> List<T> getListOfElements(final InputStream in, final TypeReference<List<T>> type) throws IOException {
        return mapper.readerFor(type).readValue(mapper.readTree(in).elements().next());
    }

}
