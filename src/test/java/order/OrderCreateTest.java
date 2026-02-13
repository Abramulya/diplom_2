package order;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import client.OrderClientTest;
import client.UserClientTest;
import model.*;
import ingredient.IngredientHelperTest;

import java.util.List;

import static org.junit.Assert.*;
import static config.UserGenerator.*;

public class OrderCreateTest {
    private OrderClientTest orderClient;
    private UserClientTest userClient;
    private User user;
    private String token;
    private List<String> validIngredients;

    @Before
    public void setUp() {
        orderClient = new OrderClientTest();
        userClient = new UserClientTest();

        validIngredients = IngredientHelperTest.getValidIngredientIds();

        user = getRandomUser();
        Response createResponse = userClient.createUser(user);

        // Проверяем, что пользователь создан
        assertEquals(200, createResponse.statusCode());

        token = createResponse.as(ApiResponse.class).getAccessToken();

        // ВАЖНО: проверяем, что токен не null
        assertNotNull("Токен не должен быть null", token);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и ингредиентами - успех")
    public void createOrderWithAuthAndIngredientsSuccess() {
        Order order = new Order(validIngredients);

        Response response = orderClient.createOrder(order, token);

        assertEquals(200, response.statusCode());

        // Просто проверяем success, не пытаясь распарсить всю структуру
        assertTrue(response.jsonPath().getBoolean("success"));
    }

    @Test
    @DisplayName("Создание заказа без авторизации, но с ингредиентами - успех")
    public void createOrderWithoutAuthWithIngredientsSuccess() {
        Order order = new Order(validIngredients);

        Response response = orderClient.createOrderWithoutAuth(order);

        assertEquals(200, response.statusCode());

        ApiResponse apiResponse = response.as(ApiResponse.class);
        assertTrue(apiResponse.isSuccess());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов - ошибка 400")
    public void createOrderWithoutIngredientsError() {
        Order order = new Order(List.of());

        Response response = orderClient.createOrderWithoutAuth(order);

        assertEquals(400, response.statusCode());

        ApiResponse apiResponse = response.as(ApiResponse.class);
        assertFalse(apiResponse.isSuccess());
        assertEquals("Ingredient ids must be provided", apiResponse.getMessage());
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиента - ошибка 500")
    public void createOrderWithInvalidIngredientHashError() {
        Order order = new Order(List.of("invalid_hash_123"));

        Response response = orderClient.createOrderWithoutAuth(order);

        assertEquals(500, response.statusCode());
    }

    @After
    public void tearDown() {
        if (token != null && !token.isEmpty()) {
            userClient.deleteUser(token);
        }
    }
}
