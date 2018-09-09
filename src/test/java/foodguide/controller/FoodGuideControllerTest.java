package foodguide.controller;

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

    // TODO add localization tests
    @Test
    public void getMyDailyMenu() {
        final SingleDailyMenu menu = client.toBlocking()
                .retrieve(HttpRequest.GET("/foodguide/my-daily-menu/Bob-M-23"), SingleDailyMenu.class);

        assertThat(menu)
                .extracting(SingleDailyMenu::getName)
                .isEqualTo("Bob");
    }

    @Test
    @Parameters({
            "asdfasdf,   Expected <name>-<Gender>-<age> such as \"Steve-M-25\"",
            "Steve-,     Expected <name>-<Gender>-<age> such as \"Steve-M-25\"",
            "Steve-R-20, No gender matches R. Expected one of 'M' or 'F'.",
            "Steve-F-A,  Cannot read age - For input string: \"A\"",
            "Steve-20,   Expected <name>-<Gender>-<age> such as \"Steve-M-25\""})
    public void getMyDailyMenuBadRequest(final String path, final String expectedBadRequestMessage) {
        thrown.expect(HttpClientResponseException.class);
        thrown.expect(new BaseMatcher<HttpClientResponseException>() {
            private HttpStatus actualStatus = null;

            @Override
            public void describeTo(Description description) {
                description.appendText("Expected response status of 400 but was " + actualStatus.getCode());
            }

            @Override
            public boolean matches(Object item) {
                final HttpClientResponseException ex = (HttpClientResponseException) item;
                actualStatus = ex.getStatus();
                return HttpStatus.BAD_REQUEST.equals(ex.getStatus());
            }
        });
        thrown.expectMessage(expectedBadRequestMessage);

        client.toBlocking().retrieve(HttpRequest.GET("/foodguide/my-daily-menu/" + path), HttpResponse.class);
    }
}
