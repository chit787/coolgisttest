import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

public class Test_GetGistScenarios {

    private static ArrayList<String> _publicGists = new ArrayList<String>();
    private static ArrayList<String> _privateGists = new ArrayList<String>();
    Properties props = new Properties();

    public String createGist(String fileName) {

        File file = new File("src/test/resources/" + fileName + ".json");

        return given().
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
                path("id").toString();
    }

    /**
     * Setup method which will first inject some gists which can be asserted later for
     * authenticated and unauthenticated user
     * @throws IOException
     */
    @Before
    public void setup() throws IOException {
        InputStream input = new FileInputStream("src/test/resources/config.properties");
        props.load(input);

        _publicGists.add(createGist("publicGist"));
        _publicGists.add(createGist("publicGist"));
        _privateGists.add(createGist("privateGist"));
        _privateGists.add(createGist("privateGist"));
        _privateGists.add(createGist("privateGist"));

        RestAssured.defaultParser = Parser.JSON;
    }

    /**
     * Scenario: user should be able to list his public gists when no token is provided
     */
    @Test
    public void ListPublicGists_ShouldBeSuccess() {

        Response response = when().
            get(props.getProperty("userEndpoint") + "/chit787/gists");

        List<String> jsonResponse = response.jsonPath().getList("id");

        if (jsonResponse != null) {
            for(Integer i=0; i < _publicGists.size(); i++) {
                Assert.assertTrue("Public gists do not contain" + _publicGists.get(i), jsonResponse.contains(_publicGists.get(i)));
            }

            for(Integer i=0; i < _privateGists.size(); i++) {
                Assert.assertTrue("private gist is visible without authentication which is not expected"
                        + _privateGists.get(i), !jsonResponse.contains(_privateGists.get(i)));
            }
        } else {
            System.out.println("rate limit for unauthorised access is crossed");
        }

    }

    /**
     * Scenario: user should be able to list his public & private gists when valid token is provided
     */
    @Test
    public void ListPrivateGists_ShouldBeSuccess() {

        Response response = given().
            auth().
            oauth2(props.getProperty("gist_repo_user_gist_scopes")).
        when().
            get(props.getProperty("userEndpoint") + "/chit787/gists");

        List<String> jsonResponse = response.jsonPath().getList("id");

        for(Integer i=0; i < _publicGists.size(); i++) {
            Assert.assertTrue("Public gists do not contain" + _publicGists.get(i), jsonResponse.contains(_publicGists.get(i)));
        }

        for(Integer i=0; i < _privateGists.size(); i++) {
            Assert.assertTrue("private gist is not visible"
                    + _privateGists.get(i), jsonResponse.contains(_privateGists.get(i)));
        }

    }

}
