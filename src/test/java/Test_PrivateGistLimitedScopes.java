import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static io.restassured.RestAssured.given;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test_PrivateGistLimitedScopes {

    private static String _gistId = null;
    Properties props = new Properties();

    @Before
    public void setup() throws IOException {
        InputStream input = new FileInputStream("src/test/resources/config.properties");
        props.load(input);

        File file = new File("src/test/resources/privateGist.json");

        _gistId = given().
                    auth().
                    oauth2(props.getProperty("gist_repo_user_gist_scopes")).
                  when().
                    body(file).
                    post(props.getProperty("endpoint")).
                  then().
                    assertThat().
                    statusCode(HttpStatus.SC_CREATED).
                  and().
                    extract().
                    path("id");
    }

    /**
     * Scenario: user should not be able to create a gist without 'gist' scope assigned to the user token
     */
    @Test
    public void CreateGistForUserWithNoGistScope_ShouldBeNoSuccess() {

        File file = new File("src/test/resources/privateGist.json");
        given().
            auth().
            oauth2(props.getProperty("scope_repo_user_with_no-gist")).
        when().
            body(file).
            post(props.getProperty("endpoint")).
        then().
            assertThat().
            statusCode(HttpStatus.SC_NOT_FOUND);
    }

    /**
     * Scenario: user should not be able to delete a gist without 'gist' scope assigned to the user token
     */
    @Test
    public void DeletePrivateGistForUsersWithNoGistScope_ShouldBeNoSuccess() {
        given().
            auth().
            oauth2(props.getProperty("scope_repo_user_with_no-gist")).
        when()
            .delete(props.getProperty("endpoint") + File.separator +  _gistId).
        then().
            assertThat().
            statusCode(HttpStatus.SC_NOT_FOUND);

    }

}
