import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.equalTo;

import static io.restassured.RestAssured.given;

public class ApiRestAssuredTests {

    public static final String BASE_URL = "https://petstore.swagger.io/v2/user/";
    public static final String TEST_USER_NAME = "MIPT_User";
    public static final String TEST_USER_NAME_2 = "MIPT_User_2";
    public static final String JSON_BODY =
            "{" +
                    "\"id\": 0," +
                    "\"username\": \"" + TEST_USER_NAME + "\"," +
                    "\"firstName\": \"Test User\"," +
                    "\"lastName\": \"MIPT\"," +
                    "\"email\": \"testuser@mipt.ru\"," +
                    "\"password\": \"***\"," +
                    "\"phone\": \"+79012224444\"," +
                    "\"userStatus\": 1" +
                    "}";

    @Test
    void getUser200Test() {
        postUser("", JSON_BODY);
        Assertions.assertEquals(200, getUser(TEST_USER_NAME).extract().statusCode());
    }

    @Test
    void getUser404Test() {
        Assertions.assertEquals(404, getUser("notExistedUser666").extract().statusCode());
    }

    @Test
    void postUserTest() {
        ValidatableResponse postResponse = postUser("", JSON_BODY);
        postResponse.assertThat().statusCode(200);
        String userId = postResponse.extract().response().path("message");
        ValidatableResponse getResponse =
                getUser(TEST_USER_NAME).
                        assertThat().
                        statusCode(200).
                        body("id", equalTo(Long.parseLong(userId))).
                        body("username", equalTo(TEST_USER_NAME)).
                        body("firstName", equalTo("Test User")).
                        body("lastName", equalTo("MIPT")).
                        body("email", equalTo("testuser@mipt.ru")).
                        body("password", equalTo("***")).
                        body("phone", equalTo("+79012224444")).
                        body("userStatus", equalTo(1));
    }

    @Test
    void postUserWithListTest() {
        String jsonBodyAsList =
                        "[" +
                        JSON_BODY + "," +
                        JSON_BODY.
                                replace(TEST_USER_NAME, TEST_USER_NAME_2).
                                replace("***", "!!!") +
                        "]";
        postUser("createWithList", jsonBodyAsList).log().all();
        ValidatableResponse user1 =
                getUser(TEST_USER_NAME).
                        assertThat().
                        statusCode(200).
                        body("username", equalTo(TEST_USER_NAME)).
                        body("firstName", equalTo("Test User")).
                        body("lastName", equalTo("MIPT")).
                        body("email", equalTo("testuser@mipt.ru")).
                        body("password", equalTo("***")).
                        body("phone", equalTo("+79012224444")).
                        body("userStatus", equalTo(1));
        ValidatableResponse user2 =
                getUser(TEST_USER_NAME_2).
                        assertThat().
                        statusCode(200).
                        body("username", equalTo(TEST_USER_NAME_2)).
                        body("firstName", equalTo("Test User")).
                        body("lastName", equalTo("MIPT")).
                        body("email", equalTo("testuser@mipt.ru")).
                        body("password", equalTo("!!!")).
                        body("phone", equalTo("+79012224444")).
                        body("userStatus", equalTo(1));
    }

    @Test
    void putUserTest() {
        String userId = postUser("", JSON_BODY).extract().body().path("message");

        String endpoint = BASE_URL + TEST_USER_NAME;
        String putJsonBody =
                        "{" +
                        "\"id\": " + Long.parseLong(userId) + "," +
                        "\"email\": \"test@mipt.ru\"," +
                        "\"phone\": \"79012225555\"" +
                        "}";
        ValidatableResponse putResponse = given().
                header("accept", "application/json").
                header("Content-Type", "application/json").
                body(putJsonBody).
                when().
                put(endpoint).
                then().
                log().
                all().
                assertThat().
                statusCode(200);

        getUser(TEST_USER_NAME).
                assertThat().
                statusCode(200).
                body("username", equalTo(TEST_USER_NAME)).
                body("email", equalTo("test@mipt.ru")).
                body("phone", equalTo("+79012225555"));
    }

    @Test
    void deleteUserTest() {
        postUser("", JSON_BODY);

        String endpoint = BASE_URL + TEST_USER_NAME;
        given().
                header("accept", "application/json").
                header("Content-Type", "application/json").
                when().
                delete(endpoint).
                then().
                log().
                all().
                assertThat().
                statusCode(200);

        Assertions.assertEquals(404, getUser(TEST_USER_NAME).extract().statusCode());
    }

    @AfterEach
    void clearTestData() {
        System.out.println("Clear test data...");
        deleteUserIfExists(TEST_USER_NAME);
        deleteUserIfExists(TEST_USER_NAME_2);
        System.out.println("Test data cleared successfully");
    }

    ValidatableResponse getUser(String userName) {
        String endpoint = BASE_URL + userName;
        return given().
                header("accept", "application/json").
                header("Content-Type", "application/json").
                when().
                get(endpoint).
                then();
    }

    ValidatableResponse postUser(String postType, String jsonBody) {
        String endpoint = BASE_URL + postType;
        ValidatableResponse response =
                given().
                        header("accept", "application/json").
                        header("Content-Type", "application/json").
                        body(jsonBody).
                        when().
                        post(endpoint).
                        then();
        if (response.
                extract().
                statusCode() == 200) {
            System.out.println("User " + TEST_USER_NAME + " created successfully");
        }
        return response;
    }

    void deleteUserIfExists(String userName) {
        if (getUser(userName).extract().statusCode() == 200) {
            String endpoint = BASE_URL + userName;
            while (given().
                    when().
                    delete(endpoint).
                    then().
                    extract().
                    statusCode() == 200);
            System.out.println("User " + userName + " deleted successfully");
        }
    }
}
