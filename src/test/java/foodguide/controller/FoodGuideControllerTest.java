package foodguide.controller;

import foodguide.dto.FamilyDailyMenu;
import foodguide.dto.SingleDailyMenu;
import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.server.EmbeddedServer;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
public class FoodGuideControllerTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static EmbeddedServer server;
    private static RxHttpClient client;

    @BeforeClass
    public static void setUp() {
        server = ApplicationContext.run(EmbeddedServer.class);
        client = server.getApplicationContext().createBean(RxHttpClient.class, server.getURL());
    }

    @AfterClass
    public static void tearDown() {
        server.stop();
    }


    //
    // /foodguide/my-daily-menu
    //

    @Test
    public void myDailyMenuLocalizedToFrench() {
        final SingleDailyMenu menu = client.toBlocking()
                .retrieve(HttpRequest.GET("/foodguide/my-daily-menu/Steve:M:20")
                                .header("Accept-Language", "fr,en;q=0.5")
                        , SingleDailyMenu.class);

        assertThat(menu.tipsByFoodGroup.get("Produits céréaliers"))
                .isIn("Choisissez des produits céréaliers plus faibles en lipides, sucre ou sel.",
                        "Consommez au moins la moitié de vos portions de produits céréaliers sous forme de grains entiers.");
    }

    @Test
    public void myDailyMenuLocalizedToEnglish() {
        final SingleDailyMenu menu = client.toBlocking()
                .retrieve(HttpRequest.GET("/foodguide/my-daily-menu/Steve:M:20")
                                .header("Accept-Language", "en-US,en;q=0.5")
                        , SingleDailyMenu.class);

        assertThat(menu.tipsByFoodGroup.get("Grains"))
                .isIn("Make at least half of your grain products whole grain each day.",
                        "Choose grain products that are lower in fat, sugar or salt.");
    }

    @Test
    public void myDailyMenuLocalizedToUnsupportedLanguage() {
        thrown.expect(HttpClientResponseException.class);
        thrown.expect(responseException(HttpStatus.NOT_ACCEPTABLE, "Unsupported language. Supported languages include 'en,fr'."));

        client.toBlocking()
                .retrieve(HttpRequest.GET("/foodguide/my-daily-menu/Steve:M:20")
                                .header("Accept-Language", "de")
                        , SingleDailyMenu.class);
    }

    @Test
    public void myDailyMenuNoAcceptLanguageHeader() {
        final SingleDailyMenu menu = client.toBlocking()
                .retrieve(HttpRequest.GET("/foodguide/my-daily-menu/Steve:M:20"), SingleDailyMenu.class);

        assertThat(menu.tipsByFoodGroup.get("Grains"))
                .isIn("Make at least half of your grain products whole grain each day.",
                        "Choose grain products that are lower in fat, sugar or salt.");
    }

    @Test
    @Parameters()
    public void myDailyMenuHasAcceptedServingSizes(final String path, final List<ServingCount> expectedServings) {
        final SingleDailyMenu menu = client.toBlocking()
                .retrieve(HttpRequest.GET("/foodguide/my-daily-menu/" + path), SingleDailyMenu.class);

        for (final ServingCount expectedServing : expectedServings) {
            assertThat(menu.foodsByFoodGroup.get(expectedServing.foodGroup).size())
                    .isBetween(expectedServing.minimumServingCount, expectedServing.maximumServingCount);
        }
    }

    @SuppressWarnings("unused")
    private Object parametersForMyDailyMenuHasAcceptedServingSizes() {
        return new Object[]{
                new Object[]{"Steve:M:2", asList(vf(4), gr(3), mi(2), me(1))},
                new Object[]{"Steve:M:3", asList(vf(4), gr(3), mi(2), me(1))},
                new Object[]{"Jill:F:2", asList(vf(4), gr(3), mi(2), me(1))},
                new Object[]{"Jill:F:3", asList(vf(4), gr(3), mi(2), me(1))},
                new Object[]{"Steve:M:4", asList(vf(5), gr(4), mi(2), me(1))},
                new Object[]{"Steve:M:8", asList(vf(5), gr(4), mi(2), me(1))},
                new Object[]{"Jill:F:4", asList(vf(5), gr(4), mi(2), me(1))},
                new Object[]{"Jill:F:8", asList(vf(5), gr(4), mi(2), me(1))},
                new Object[]{"Steve:M:60", asList(vf(7), gr(7), mi(3), me(3))},
                new Object[]{"Jill:F:60", asList(vf(7), gr(6), mi(3), me(2))},
                new Object[]{"Steve:M:100", asList(vf(7), gr(7), mi(3), me(3))},
                new Object[]{"Jill:F:100", asList(vf(7), gr(6), mi(3), me(2))},
        };
    }

    @Test
    @Parameters()
    public void myDailyMenuHasTips(final String foodGroup, final List<String> tips) {
        final SingleDailyMenu menu = client.toBlocking()
                .retrieve(HttpRequest.GET("/foodguide/my-daily-menu/nobody:M:20"), SingleDailyMenu.class);

        assertThat(menu.tipsByFoodGroup.get(foodGroup)).isIn(tips);
    }

    @SuppressWarnings("unused")
    private Object parametersForMyDailyMenuHasTips() {
        return new Object[]{
                new Object[]{"Vegetables and Fruit", asList(
                        "Eat at least one dark green and one orange vegetable each day.",
                        "Choose vegetables and fruit prepared with little or no added fat, sugar or salt.",
                        "Have vegetables and fruit more often than juice.")
                },
                new Object[]{"Grains", asList(
                        "Make at least half of your grain products whole grain each day.",
                        "Choose grain products that are lower in fat, sugar or salt.")
                },
                new Object[]{"Milk and Alternatives", asList(
                        "Drink skim, 1%, or 2% milk each day.",
                        "Select lower fat milk alternatives.")
                },
                new Object[]{"Meat and Alternatives", asList(
                        "Have meat alternatives such as beans, lentils and tofu often.",
                        "Meat and Alternatives", "Eat at least two Food Guide Servings of fish each week.",
                        "Meat and Alternatives", "Select lean meat and alternatives prepared with little or no added fat or salt.")
                }
        };
    }

    @Test
    public void myDailyMenuTooYoung() {
        thrown.expect(HttpClientResponseException.class);
        thrown.expect(responseException(HttpStatus.BAD_REQUEST, "too young"));

        client.toBlocking().retrieve(HttpRequest.GET("/foodguide/my-daily-menu/baby:M:0"), HttpResponse.class);
    }

    @Test
    @Parameters({
            "asdfasdf,   Expected <name>:<Gender><age> such as \"Steve:M:25\"",
            "Steve:,     Expected <name>:<Gender>:<age> such as \"Steve:M:25\"",
            "Steve:R:20, No gender matches R. Expected one of 'M' or 'F'.",
            "Steve:F:A,  Cannot read age - For input string: \"A\"",
            "Steve:F:-2, Age must be greater than or equal to 0\\, but was '-1'.",
            "Steve:F:0,  Age must be greater than or equal to 0\\, but was '0'.",
            "Steve:F:1,  Age must be greater than or equal to 0\\, but was '1'.",
            "Steve:20,   Expected <name>:<Gender><age> such as \"Steve:M:25\""})
    public void myDailyMenuBadRequest(final String path, final String expectedBadRequestMessage) {
        thrown.expect(HttpClientResponseException.class);
        thrown.expect(responseException(HttpStatus.BAD_REQUEST, expectedBadRequestMessage));

        client.toBlocking().retrieve(HttpRequest.GET("/foodguide/my-daily-menu/" + path), HttpResponse.class);
    }

    //
    // /foodguide/family-daily-menu
    //

    @Test
    public void familyDailyMenuLocalizedToFrench() {
        final FamilyDailyMenu menu = client.toBlocking()
                .retrieve(HttpRequest.GET("/foodguide/family-daily-menu/Steve:M:20,Jill:F:22")
                                .header("Accept-Language", "fr,en;q=0.5")
                        , FamilyDailyMenu.class);

        assertThat(menu.tipsByFoodGroup.get("Produits céréaliers"))
                .isIn("Choisissez des produits céréaliers plus faibles en lipides, sucre ou sel.",
                        "Consommez au moins la moitié de vos portions de produits céréaliers sous forme de grains entiers.");
    }


    @Test
    public void familyDailyMenuLocalizedToEnglish() {
        final FamilyDailyMenu menu = client.toBlocking()
                .retrieve(HttpRequest.GET("/foodguide/family-daily-menu/Steve:M:20,Jill:F:22")
                                .header("Accept-Language", "en-US,en;q=0.5")
                        , FamilyDailyMenu.class);

        assertThat(menu.tipsByFoodGroup.get("Grains"))
                .isIn("Make at least half of your grain products whole grain each day.",
                        "Choose grain products that are lower in fat, sugar or salt.");
    }

    @Test
    public void familyDailyMenuLocalizedToUnsupportedLanguage() {
        thrown.expect(HttpClientResponseException.class);
        thrown.expect(responseException(HttpStatus.NOT_ACCEPTABLE, "Unsupported language. Supported languages include 'en,fr'."));

        client.toBlocking()
                .retrieve(HttpRequest.GET("/foodguide/family-daily-menu/Steve:M:20,Jill:F:22")
                                .header("Accept-Language", "de")
                        , FamilyDailyMenu.class);

    }

    @Test
    public void familyDailyMenuNoAcceptLanguageHeader() {
        final FamilyDailyMenu menu = client.toBlocking()
                .retrieve(HttpRequest.GET("/foodguide/family-daily-menu/Steve:M:20,Jill:F:22")
                        , FamilyDailyMenu.class);

        assertThat(menu.tipsByFoodGroup.get("Grains"))
                .isIn("Make at least half of your grain products whole grain each day.",
                        "Choose grain products that are lower in fat, sugar or salt.");
    }

    @Test
    @Parameters({
            "asdfasdf,               Expected <name>:<Gender><age> such as \"Steve:M:25\"",
            "Steve:,                 Expected <name>:<Gender>:<age> such as \"Steve:M:25\"",
            "Steve:M:20\\,Francene:, Expected <name>:<Gender>:<age> such as \"Steve:M:25\"",
            "Steve:R:20,             No gender matches R. Expected one of 'M' or 'F'.",
            "Steve:20,               Expected <name>:<Gender><age> such as \"Steve:M:25\""})
    public void familyDailyMenuBadRequest(final String path, final String expectedBadRequestMessage) {
        thrown.expect(HttpClientResponseException.class);
        thrown.expect(responseException(HttpStatus.BAD_REQUEST, expectedBadRequestMessage));

        client.toBlocking().retrieve(HttpRequest.GET("/foodguide/family-daily-menu/" + path), HttpResponse.class);
    }

    @Test
    @Parameters()
    public void familyDailyMenuHasAcceptedServingSizes(final int numberOfIndividualMenus,
                                                       final String path,
                                                       final List<ServingCount> expectedServings) {
        final FamilyDailyMenu menu = client.toBlocking()
                .retrieve(HttpRequest.GET("/foodguide/family-daily-menu/" + path), FamilyDailyMenu.class);

        assertThat(menu.menuPerPerson).hasSize(numberOfIndividualMenus);
        for (final ServingCount expectedServing : expectedServings) {
            assertThat(menu.foodsByFoodGroup.get(expectedServing.foodGroup).size())
                    .isBetween(expectedServing.minimumServingCount, expectedServing.maximumServingCount);
        }
    }

    @SuppressWarnings("unused")
    private Object parametersForFamilyDailyMenuHasAcceptedServingSizes() {
        return new Object[]{
                new Object[]{1, "Steve:M:4", asList(vf(5), gr(4), mi(2), me(1))},
                new Object[]{2, "Steve:M:2,Jill:f:60", asList(vf(11), gr(9), mi(5), me(3))},
                new Object[]{3, "Steve:M:2,Jill:f:60,Bob:M:12", asList(vf(17), gr(15), mi(8, 9), me(4, 5))},
        };
    }

    //
    // test utilities
    //

    private BaseMatcher<HttpClientResponseException> responseException(final HttpStatus expectedStatus, final String expectedMessage) {
        return new BaseMatcher<HttpClientResponseException>() {
            private HttpStatus actualStatus = null;
            private String actualMessage = null;

            @Override
            public void describeTo(Description description) {
                description.appendText(String.format("Expected response status of %d with message containing \"%s\"' but was %d with message \"%s\"",
                        expectedStatus.getCode(), expectedMessage,
                        actualStatus.getCode(), actualMessage));
            }

            @Override
            public boolean matches(Object item) {
                final HttpClientResponseException ex = (HttpClientResponseException) item;
                actualStatus = ex.getStatus();
                actualMessage = ex.getMessage();
                return expectedStatus.equals(ex.getStatus()) && expectedMessage.contains(expectedMessage);
            }
        };
    }

    private static ServingCount vf(final int servingCount) {
        return vf(servingCount, servingCount);
    }

    private static ServingCount vf(final int minServingCount, final int maxServingCount) {
        return servingCount("Vegetables and Fruit", minServingCount, maxServingCount);
    }

    private static ServingCount gr(final int servingCount) {
        return gr(servingCount, servingCount);
    }

    private static ServingCount gr(final int minServingCount, final int maxServingCount) {
        return servingCount("Grains", minServingCount, maxServingCount);
    }

    private static ServingCount mi(final int servingCount) {
        return mi(servingCount, servingCount);
    }

    private static ServingCount mi(final int minServingCount, final int maxServingCount) {
        return servingCount("Milk and Alternatives", minServingCount, maxServingCount);
    }

    private static ServingCount me(final int servingCount) {
        return me(servingCount, servingCount);
    }

    private static ServingCount me(final int minServingCount, final int maxServingCount) {
        return servingCount("Meat and Alternatives", minServingCount, maxServingCount);
    }

    private static ServingCount servingCount(final String foodGroup, final int minServingCount, final int maxServingCount) {
        return new ServingCount(foodGroup, minServingCount, maxServingCount);
    }

    private static class ServingCount {
        final String foodGroup;
        final int minimumServingCount;
        final int maximumServingCount;

        public ServingCount(final String foodGroup,
                            final int minimumServingCount,
                            final int maximumServingCount) {
            this.foodGroup = foodGroup;
            this.minimumServingCount = minimumServingCount;
            this.maximumServingCount = maximumServingCount;
        }
    }
}
