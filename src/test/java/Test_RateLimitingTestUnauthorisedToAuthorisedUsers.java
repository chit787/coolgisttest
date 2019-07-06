import java.io.File;
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
import static io.restassured.RestAssured.when;

public class Test_RateLimitingTestUnauthorisedToAuthorisedUsers {

    private static Integer MAX_UNAUTH_RATE_LIMIT = 60;
    private static Integer MAX_AUTH_RATE_LIMIT = 5000;
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
     * Scenario : To test api rate limit when a user without sessions consumes the limit and still able
     * to request same resource when requested that resource with oAuth token
     */
    @Test
    public void RateLimitForUserWithNoGistScopeAndGistScope() {

        for (int i = 1; i <= MAX_UNAUTH_RATE_LIMIT + 1; i++) {
            Response response = when().
                    get(props.getProperty("endpoint"));

            curr_rate_limiting = response.getHeader("X-RateLimit-Remaining");

            if(Integer.parseInt(curr_rate_limiting) > 0) {
                Assert.assertTrue("Current rate limit in range", Integer.parseInt(curr_rate_limiting) > 0);
                Assert.assertTrue(response.getStatusCode() == HttpStatus.SC_OK);
            } else if( i == MAX_UNAUTH_RATE_LIMIT) {
                Assert.assertTrue("Current rate limit in range", Integer.parseInt(curr_rate_limiting) == 0);
                Assert.assertTrue(response.getStatusCode() == HttpStatus.SC_OK);
            } else {
                Assert.assertTrue("Current rate applied", Integer.parseInt(curr_rate_limiting) == 0);
                Assert.assertTrue(response.getStatusCode() == HttpStatus.SC_FORBIDDEN);

                given().
                        auth().
                        oauth2(props.getProperty("gist_rate_limiting_scopeGist")).
                        when().
                        get(props.getProperty("endpoint") + File.separator + "gists").
                        then().
                        extract().
                        header("X-RateLimit-Limit")
                        .equals(MAX_AUTH_RATE_LIMIT);

            }
        }

    }

}
