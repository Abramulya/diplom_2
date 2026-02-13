package user;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import client.UserClient;
import model.User;
import model.UserCredentials;
import model.ApiResponse;

import static org.junit.Assert.*;
import static config.UserGenerator.*;

public class UserLoginTest {
    private UserClient userClient;
    private User user;
    private String token;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = getRandomUser();

        Response createResponse = userClient.createUser(user);
        token = createResponse.as(ApiResponse.class).getAccessToken();
    }

    @Test
    @DisplayName("Логин под существующим пользователем - успех")
    public void loginExistingUserSuccess() {
        UserCredentials credentials = new UserCredentials(user.getEmail(), user.getPassword());

        Response response = userClient.loginUser(credentials);

        assertEquals(200, response.statusCode());

        ApiResponse apiResponse = response.as(ApiResponse.class);
        assertTrue(apiResponse.isSuccess());
        assertNotNull(apiResponse.getAccessToken());
        assertNotNull(apiResponse.getRefreshToken());
        assertEquals(user.getEmail().toLowerCase(), apiResponse.getUser().getEmail());
        assertEquals(user.getName(), apiResponse.getUser().getName());
    }

    @Test
    @DisplayName("Логин с неверным паролем - ошибка 401")
    public void loginWithWrongPasswordError() {
        UserCredentials credentials = new UserCredentials(user.getEmail(), "wrongPassword123");

        Response response = userClient.loginUser(credentials);

        assertEquals(401, response.statusCode());

        ApiResponse apiResponse = response.as(ApiResponse.class);
        assertFalse(apiResponse.isSuccess());
        assertEquals("email or password are incorrect", apiResponse.getMessage());
    }

    @After
    public void tearDown() {
        if (token != null && !token.isEmpty()) {
            userClient.deleteUser(token);
        }
    }
}
