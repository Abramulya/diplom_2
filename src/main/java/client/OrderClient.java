package client;

import io.restassured.response.Response;
import model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestClient {
    private static final String ORDERS = "/api/orders";
    private static final String INGREDIENTS = "/api/ingredients";

    public Response getIngredients() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(INGREDIENTS);
    }

    public Response createOrder(Order order, String token) {
        return given()
                .spec(getAuthSpec(token))
                .body(order)
                .when()
                .post(ORDERS);
    }

    public Response createOrderWithoutAuth(Order order) {
        return given()
                .spec(getBaseSpec())
                .body(order)
                .when()
                .post(ORDERS);
    }

    public Response getUserOrders(String token) {
        return given()
                .spec(getAuthSpec(token))
                .when()
                .get(ORDERS);
    }

    public Response getUserOrdersWithoutAuth() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(ORDERS);
    }
}
