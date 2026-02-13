package user;

import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import client.UserClientTest;
import model.User;
import model.UserCredentials;
import model.ApiResponse;


import static io.restassured.RestAssured.given;
import static org.junit.Assert.*;
import static config.UserGenerator.*;

public class UserLoginTest {
    private UserClientTest userClient;
    private User user;
    private String token;

    @Before
    public void setUp() {
        userClient = new UserClientTest();
        user = getRandomUser();

        // Логируем данные пользователя
        System.out.println("Create user: " + user.getEmail() + " / " + user.getPassword());

        //Response createResponse = userClient.createUser(user);

        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
        ApiResponse apiResponse = given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .post("/api/auth/register")
                .as(ApiResponse.class);



        //ApiResponse apiResponse = createResponse.as(ApiResponse.class);
        token = apiResponse.getAccessToken();
        System.out.println(token);
        //token = createResponse.as(ApiResponse.class).getAccessToken();
        assertNotNull("Token is null", token);
    }

    @Test
    @DisplayName("Логин под существующим пользователем - успех")
    public void loginExistingUserSuccess() {
        System.out.println("loginExistingUserSuccess");
        UserCredentials credentials = new UserCredentials(user.getEmail(), user.getPassword());

        Response response = userClient.loginUser(credentials);

        // Логируем ответ для отладки
        System.out.println("Login response: " + response.statusCode());
        System.out.println("Login body: " + response.asString());

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
        System.out.println("loginWithWrongPasswordError");
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
