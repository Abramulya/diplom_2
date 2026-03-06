package order;

import ingredient.IngredientHelperTest;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import client.OrderClientTest;
import client.UserClientTest;
import model.*;

import java.util.List;

import static org.junit.Assert.*;
import static config.UserGenerator.*;

public class UserOrdersTest {
    private OrderClientTest orderClient;
    private UserClientTest userClient;
    private User user;
    private String token;
    private List<String> validIngredients;

    @Before
    public void setUp() {
        while (token == null) {
            orderClient = new OrderClientTest();
            userClient = new UserClientTest();

            validIngredients = IngredientHelperTest.getValidIngredientIds();

            user = getRandomUser();
            Response createResponse = userClient.createUser(user);
            token = createResponse.as(ApiResponse.class).getAccessToken();


        }
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