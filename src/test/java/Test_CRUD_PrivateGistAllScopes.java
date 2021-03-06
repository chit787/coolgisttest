import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import static io.restassured.RestAssured.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Test_CRUD_PrivateGistAllScopes {

    private static String _gistId = null;
    Properties props = new Properties();

    @Before
    public void setup() throws IOException {
        InputStream input = new FileInputStream("src/test/resources/config.properties");
        props.load(input);
    }

    /**
     * Scenario: User should be able to create a gist with valid token
     */
    @Test
    public void Test_1_CreateGistForUserWithAllScopes_ShouldBeSuccess() {

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
                extract().path("id");
    }

    /**
     * Scenario: User should be able to edit a gist with valid token
     */
    @Test
    public void Test_2_EditPrivateGistForUsersWithAllScopes_ShouldBeSuccess() {

        File file = new File("src/test/resources/editGist.json");

        given().
            auth().
            oauth2(props.getProperty("gist_repo_user_gist_scopes")).
        when().
            body(file).patch(props.getProperty("endpoint") + File.separator + _gistId).
        then().
            assertThat().
            statusCode(HttpStatus.SC_OK);
    }

    /**
     * Scenario: User should be able to star a gist with valid token
     */
    @Test
    public void Test_3_StarPrivateGistForUsersWithAllScopes_ShouldBeSuccess() {

        given().
            auth().
            oauth2(props.getProperty("gist_repo_user_gist_scopes")).
            header("Content-Lenngth", 0).
        when().
            put(props.getProperty("endpoint") +
                    File.separator +
                    _gistId +
                    File.separator +
                    "star").
        then().
            assertThat().
            statusCode(HttpStatus.SC_NO_CONTENT);

    }

    /**
     * Scenario: User should be able to delete a star from a gist with valid token
     */
    @Test
    public void Test_4_UnStarPrivateGistForUsersWithAllScopes_ShouldBeSuccess() {
        given().
                auth().
                oauth2(props.getProperty("gist_repo_user_gist_scopes")).
        when().
            get(props.getProperty("endpoint") +
                File.separator +
                _gistId +
                File.separator +
                "star").
        then().
            assertThat().
            statusCode(HttpStatus.SC_NO_CONTENT);

        given().
                auth().
                oauth2(props.getProperty("gist_repo_user_gist_scopes")).
                when().
                delete(props.getProperty("endpoint") +
                        File.separator +
                        _gistId +
                        File.separator +
                        "star").
                then().
                assertThat().
                statusCode(HttpStatus.SC_NO_CONTENT);

    }

    /**
     * Scenario: User should be able to delete a gist with valid token
     */
    @Test
    public void Test_5_DeletePrivateGistForUsersWithAllScopes_ShouldBeSuccess() {
        given().
                auth().
                oauth2(props.getProperty("gist_repo_user_gist_scopes")).
                when()
                .delete(props.getProperty("endpoint") + File.separator +  _gistId).
                then().
                assertThat().
                statusCode(HttpStatus.SC_NO_CONTENT);

    }

}
