package foodguide.management;

import io.micronaut.context.ApplicationContext;
import io.micronaut.health.HealthStatus;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.management.health.indicator.HealthResult;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test the <code>/health</code> endpoint.
 */
public class HealthCheckTest {
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

    @Test
    public void getHealth() {
        final HealthResult check = client.toBlocking()
                .retrieve(HttpRequest.GET("/health"), HealthResult.class);

        assertThat(check)
                .extracting(HealthResult::getName, HealthResult::getStatus)
                .containsExactly("foodguide", HealthStatus.UP);
    }
}
