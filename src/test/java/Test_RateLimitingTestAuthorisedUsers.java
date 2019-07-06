import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class Test_RateLimitingTestAuthorisedUsers {

    private static Integer MAX_AUTH_RATE_LIMIT = 0;
    private String curr_rate_limiting = null;
    private static Properties props = new Properties();

    @Before
    public void setup() throws IOException {
        InputStream input = new FileInputStream("src/test/resources/config.properties");
        props.load(input);

        MAX_AUTH_RATE_LIMIT = Integer.parseInt(
                given().auth().oauth2(props.getProperty("gist_rate_limiting_scopeGist")).
                when().get(props.getProperty("rateLimitUrl")).then().extract().path("resources.core.remaining").toString());

    }

    /**
     * Scenario: To Test api rate limit gives user valid status codes when limit is reached and/or crossed
     */
    @Test
    public void Test_GistApiRateLimitForUserWithGistScope() {

        for(int i = 1; i <= MAX_AUTH_RATE_LIMIT + 1; i++) {
            Response response = given().
                    auth().
                    oauth2(props.getProperty("gist_rate_limiting_scopeGist")).
                    when().
                    get(props.getProperty("endpoint")).then().
                    extract().response();

            curr_rate_limiting = response.
                    getHeader("X-RateLimit-Remaining");

            if(Integer.parseInt(curr_rate_limiting) > 0) {
                Assert.assertTrue("Current rate limit in range",
                        Integer.parseInt(curr_rate_limiting) > 0);
                Assert.assertTrue(response.getStatusCode() == HttpStatus.SC_OK);
            } else if(i == MAX_AUTH_RATE_LIMIT) {
                Assert.assertTrue("Current rate limit in range",
                        Integer.parseInt(curr_rate_limiting) == 0);
                Assert.assertTrue(response.getStatusCode() == HttpStatus.SC_OK);
            } else {
                Assert.assertTrue("Current rate applied",
                        Integer.parseInt(curr_rate_limiting) == 0);
                Assert.assertTrue(response.getStatusCode() == HttpStatus.SC_FORBIDDEN);
            }
        }

    }

}
