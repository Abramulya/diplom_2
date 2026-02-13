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

public class UserOrdersTest {
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

        Order order = new Order(validIngredients);
        orderClient.createOrder(order, token);
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя - успех")
    public void getUserOrdersWithAuthSuccess() {
        Response response = orderClient.getUserOrders(token);

        assertEquals(200, response.statusCode());

        ApiResponse apiResponse = response.as(ApiResponse.class);
        assertTrue(apiResponse.isSuccess());
    }

    @Test
    @DisplayName("Получение заказов без авторизации - ошибка 401")
    public void getUserOrdersWithoutAuthError() {
        Response response = orderClient.getUserOrdersWithoutAuth();

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