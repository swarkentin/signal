package signal.api;

import org.junit.Test;

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
    
    @Test
    public void test(){
        assertThat(false).isTrue();
    }

}
