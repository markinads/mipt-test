import controller.UserController;
import io.restassured.response.Response;
import model.User;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;
import static testdata.ApiTestData.DEFAULT_USER;

public class ExtendedAPITests {
    UserController userController = new UserController();

    @BeforeEach
    @AfterEach
    void clearTestData() {
        Response getUserResponse = userController.getUserByName(DEFAULT_USER.getUsername());
        while (getUserResponse.jsonPath().getString("id") != null) {
            userController.deleteUserByName(DEFAULT_USER.getUsername());
            getUserResponse = userController.getUserByName(DEFAULT_USER.getUsername());
        }
    }

    @Test
    @Tag("smoke")
    @DisplayName("Check add user is returns 200 status ok")
    long checkAddDefaultUserTest() {
        Response response = userController.addUser(DEFAULT_USER);
        assertThat(response.statusCode()).isEqualTo(200);
        return Long.parseLong(response.jsonPath().getString("message"));
    }

    @Test
    @Tag("smoke")
    @DisplayName("Check json schema")
    void jsonSchemaTest() {
        userController.addUser(DEFAULT_USER);
        userController.getUserByName(DEFAULT_USER.getUsername()).
                then().
                assertThat().
                body(matchesJsonSchemaInClasspath("jsonSchema/userSchema.json"));
    }

    @Test
    @DisplayName("Check user added correctly")
    void checkAddUserExtendedTest() {
        long expectedId = checkAddDefaultUserTest();

        Response getUserResponse = userController.getUserByName(DEFAULT_USER.getUsername());
        User actualUser = getUserResponse.as(User.class);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(getUserResponse.statusCode()).isEqualTo(200);
        softly.assertThat(actualUser.getId()).isEqualTo(expectedId);
        softly.assertThat(actualUser).usingRecursiveComparison().ignoringFields("id").isEqualTo(DEFAULT_USER);
        softly.assertAll();
    }

    @Test
    @DisplayName("Check user updated correctly")
    void checkUpdateUserTest() {
        long expectedId = checkAddDefaultUserTest();

        User userToUpdate = new User(
                expectedId,
                DEFAULT_USER.getUsername(),
                DEFAULT_USER.getFirstName(),
                DEFAULT_USER.getLastName(),
                "new@gmail.com",
                "newpass",
                DEFAULT_USER.getPhone(),
                DEFAULT_USER.getUserStatus());
        Response updateUserResponse = userController.updateUserByName(userToUpdate);
        assertThat(updateUserResponse.statusCode()).isEqualTo(200);

        Response getUserResponse = userController.getUserByName(DEFAULT_USER.getUsername());
        User actualUser = getUserResponse.as(User.class);

        assertThat(getUserResponse.statusCode()).isEqualTo(200);
        assertThat(actualUser).usingRecursiveComparison().isEqualTo(userToUpdate);
    }

    @Test
    @DisplayName("Check user removed correctly")
    void checkDeleteUserTest() {
        checkAddDefaultUserTest();

        Response deleteUserResponse = userController.deleteUserByName(DEFAULT_USER.getUsername());
        assertThat(deleteUserResponse.statusCode()).isEqualTo(200);

        Response getUserResponse = userController.getUserByName(DEFAULT_USER.getUsername());
        assertThat(getUserResponse.statusCode()).isEqualTo(404);
    }
}
