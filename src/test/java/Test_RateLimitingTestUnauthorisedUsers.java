import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.restassured.response.Response;

import static io.restassured.RestAssured.when;

public class Test_RateLimitingTestUnauthorisedUsers {

    private static Integer MAX_UNAUTH_RATE_LIMIT = 60;
    private String curr_rate_limiting = null;
    Properties props = new Properties();

    @Before
    public void setup() throws IOException {
        InputStream input = new FileInputStream("src/test/resources/config.properties");
        props.load(input);

        MAX_UNAUTH_RATE_LIMIT = Integer.parseInt(
                when().get(props.getProperty("rateLimitUrl")).then().extract().path("resources.core.remaining").toString());

    }

    /**
     * Scenario: To Test the unauthenticated users can only request the resources for maximum
     * allowed boundaries
     */
    @Test
    public void RateLimitingForUserWithNoGistScope() {

        for (int i = 1 ; i <= MAX_UNAUTH_RATE_LIMIT + 1; i++) {

            Response response = when().
                    get(props.getProperty("endpoint"));

            curr_rate_limiting = response.getHeader("X-RateLimit-Remaining");

            if(Integer.parseInt(curr_rate_limiting) > 0) {
                Assert.assertTrue("Current rate limit in range", Integer.parseInt(curr_rate_limiting) > 0);
                Assert.assertTrue(response.getStatusCode() == HttpStatus.SC_OK);
            } else if ( i == MAX_UNAUTH_RATE_LIMIT) {
                Assert.assertTrue("Current rate limit in range", Integer.parseInt(curr_rate_limiting) == 0);
                Assert.assertTrue(response.getStatusCode() == HttpStatus.SC_OK);
            } else {
                Assert.assertTrue("Current rate applied", Integer.parseInt(curr_rate_limiting) == 0);
                Assert.assertTrue(response.getStatusCode() == HttpStatus.SC_FORBIDDEN);
            }
        }

    }

}
