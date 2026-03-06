package ingredient;

import io.restassured.response.Response;
import client.OrderClientTest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IngredientHelperTest {

    public static List<String> getValidIngredientIds() {
        OrderClientTest orderClient = new OrderClientTest();
        Response response = orderClient.getIngredients();

        List<Map<String, Object>> ingredients = response.jsonPath().getList("data");
        List<String> ids = new ArrayList<>();

        if (ingredients != null && ingredients.size() >= 2) {
            ids.add(ingredients.get(0).get("_id").toString());
            ids.add(ingredients.get(1).get("_id").toString());
        }

        return ids;
    }
}
