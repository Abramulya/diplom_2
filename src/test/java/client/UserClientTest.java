package client;

import io.restassured.response.Response;
import model.User;
import model.UserCredentials;

import static io.restassured.RestAssured.given;

public class UserClientTest extends RestClientTest {
    private static final String USER_REGISTER = "/api/auth/register";
    private static final String USER_LOGIN = "/api/auth/login";
    private static final String USER_UPDATE = "/api/auth/user";

    public Response createUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post(USER_REGISTER);
    }

    public Response loginUser(UserCredentials credentials) {
        return given()
                .header("Content-type", "application/json")
                .body(credentials)
                .when()
                .post(USER_LOGIN);
    }

    public Response updateUser(User user, String token) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .patch(USER_UPDATE);
    }

    public Response updateUserWithoutAuth(User user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .patch(USER_UPDATE);
    }

    public Response deleteUser(String token) {
        return given()
                .header("Content-type", "application/json")
                .when()
                .delete(USER_UPDATE);
    }
}