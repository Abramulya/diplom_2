package user;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import client.UserClientTest;
import model.User;
import model.ApiResponse;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.*;
import static config.UserGenerator.*;

public class UserUpdateTest {
    private UserClientTest userClient;
    private User user;
    private String token;


    @Before
    public void setUp() {
        while (token == null) {
            userClient = new UserClientTest();
            user = getRandomUser();
            Response createResponse = userClient.createUser(user);
            token = createResponse.as(ApiResponse.class).getAccessToken();
        }
    }

    @Test
    @DisplayName("Изменение email с авторизацией - успех")
    public void updateEmailWithAuthSuccess() {
        String newEmail = getRandomEmail();
        Map<String, String> updatedFields = new HashMap<>();
        updatedFields.put("email", newEmail);
        Response response = userClient.updateUser(updatedFields, token);

        assertEquals(200, response.statusCode());

        ApiResponse apiResponse = response.as(ApiResponse.class);
        assertTrue(apiResponse.isSuccess());
        assertEquals(newEmail.toLowerCase(), apiResponse.getUser().getEmail());
    }

    @Test
    @DisplayName("Изменение имени с авторизацией - успех")
    public void updateNameWithAuthSuccess() {
        String newName = getRandomName();
        Map<String, String> updatedFields = new HashMap<>();
        updatedFields.put("name", newName);

        Response response = userClient.updateUser(updatedFields, token);

        assertEquals(200, response.statusCode());

        ApiResponse apiResponse = response.as(ApiResponse.class);
        assertTrue(apiResponse.isSuccess());
        assertEquals(newName, apiResponse.getUser().getName());
    }

    @Test
    @DisplayName("Изменение данных без авторизации - ошибка 401")
    public void updateUserWithoutAuthError() {
        String newName = getRandomName();
        Map<String, String> updatedFields = new HashMap<>();
        updatedFields.put("name", newName);

        Response response = userClient.updateUser(updatedFields, "12345");

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