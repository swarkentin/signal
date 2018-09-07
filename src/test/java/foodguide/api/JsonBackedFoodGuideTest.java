package foodguide.api;

import org.junit.Test;
import foodguide.service.FoodGuide;
import foodguide.service.JsonBackedFoodGuide;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test fixture for {@link JsonBackedFoodGuide}
 */
public class JsonBackedFoodGuideTest {

    @Test
    public void deserializeTest() throws IOException {
        final FoodGuide foodGuide = new JsonBackedFoodGuide();
        foodGuide.read();

        assertThat(foodGuide).isNotNull();
    }

}