package user;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import client.UserClient;
import model.User;
import model.ApiResponse;

import static org.junit.Assert.*;
import static config.UserGenerator.*;

public class UserCreateTest {
    private UserClient userClient;
    private User user;
    private String token;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = getRandomUser();
    }

    @Test
    @DisplayName("Создание уникального пользователя - успех")
    public void createUniqueUserSuccess() {
        Response response = userClient.createUser(user);

        assertEquals(200, response.statusCode());

        ApiResponse apiResponse = response.as(ApiResponse.class);
        assertTrue(apiResponse.isSuccess());
        assertNotNull(apiResponse.getAccessToken());
        assertNotNull(apiResponse.getRefreshToken());
        assertEquals(user.getEmail().toLowerCase(), apiResponse.getUser().getEmail());
        assertEquals(user.getName(), apiResponse.getUser().getName());

        token = apiResponse.getAccessToken();
    }

    @Test
    @DisplayName("Создание уже существующего пользователя - ошибка 403")
    public void createExistingUserError() {
        Response firstResponse = userClient.createUser(user);
        token = firstResponse.as(ApiResponse.class).getAccessToken();

        Response secondResponse = userClient.createUser(user);

        assertEquals(403, secondResponse.statusCode());

        ApiResponse apiResponse = secondResponse.as(ApiResponse.class);
        assertFalse(apiResponse.isSuccess());
        assertEquals("User already exists", apiResponse.getMessage());
    }

    @Test
    @DisplayName("Создание пользователя без обязательного поля - ошибка 403")
    public void createUserWithoutRequiredFieldError() {
        User userWithoutEmail = new User(null, user.getPassword(), user.getName());

        Response response = userClient.createUser(userWithoutEmail);

        assertEquals(403, response.statusCode());

        ApiResponse apiResponse = response.as(ApiResponse.class);
        assertFalse(apiResponse.isSuccess());
        assertEquals("Email, password and name are required fields", apiResponse.getMessage());
    }

    @After
    public void tearDown() {
        if (token != null && !token.isEmpty()) {
            userClient.deleteUser(token);
        }
    }
}