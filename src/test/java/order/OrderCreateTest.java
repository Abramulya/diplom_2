package order;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import client.OrderClient;
import client.UserClient;
import model.*;
import utils.IngredientHelper;

import java.util.List;

import static org.junit.Assert.*;
import static config.UserGenerator.*;

public class OrderCreateTest {
    private OrderClient orderClient;
    private UserClient userClient;
    private User user;
    private String token;
    private List<String> validIngredients;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        userClient = new UserClient();

        validIngredients = IngredientHelper.getValidIngredientIds();

        user = getRandomUser();
        Response createResponse = userClient.createUser(user);
        token = createResponse.as(ApiResponse.class).getAccessToken();
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и ингредиентами - успех")
    public void createOrderWithAuthAndIngredientsSuccess() {
        Order order = new Order(validIngredients);

        Response response = orderClient.createOrder(order, token);

        assertEquals(200, response.statusCode());

        ApiResponse apiResponse = response.as(ApiResponse.class);
        assertTrue(apiResponse.isSuccess());
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
