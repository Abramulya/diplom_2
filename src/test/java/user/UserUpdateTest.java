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

public class UserUpdateTest {
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
    @DisplayName("Изменение email с авторизацией - успех")
    public void updateEmailWithAuthSuccess() {
        String newEmail = getRandomEmail();
        User updatedUser = new User(newEmail, null, null);

        Response response = userClient.updateUser(updatedUser, token);

        assertEquals(200, response.statusCode());

        ApiResponse apiResponse = response.as(ApiResponse.class);
        assertTrue(apiResponse.isSuccess());
        assertEquals(newEmail.toLowerCase(), apiResponse.getUser().getEmail());
    }

    @Test
    @DisplayName("Изменение имени с авторизацией - успех")
    public void updateNameWithAuthSuccess() {
        String newName = "NewName" + System.currentTimeMillis();
        User updatedUser = new User(null, null, newName);

        Response response = userClient.updateUser(updatedUser, token);

        assertEquals(200, response.statusCode());

        ApiResponse apiResponse = response.as(ApiResponse.class);
        assertTrue(apiResponse.isSuccess());
        assertEquals(newName, apiResponse.getUser().getName());
    }

    @Test
    @DisplayName("Изменение данных без авторизации - ошибка 401")
    public void updateUserWithoutAuthError() {
        User updatedUser = new User(null, null, "NewName");

        Response response = userClient.updateUserWithoutAuth(updatedUser);

        assertEquals(401, response.statusCode());

        ApiResponse apiResponse = response.as(ApiResponse.class);
        assertFalse(apiResponse.isSuccess());
        assertEquals("You should be authorised", apiResponse.getMessage());
    }

    @After
    public void tearDown() {
        if (token != null && !token.isEmpty()) {
            userClient.deleteUser(token);
        }
    }
}