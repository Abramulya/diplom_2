package model;

import java.util.List;
import java.util.Map;

public class ApiResponse {
    private boolean success;
    private String message;
    private String accessToken;
    private String refreshToken;
    private User user;
    private List<Map<String, Object>> orders;  // ← ИЗМЕНИЛ ТИП!
    private String name;                        // ← ДОБАВИЛ!
    private Map<String, Object> order;           // ← ДОБАВИЛ!

    public ApiResponse() {}

    // Геттеры и сеттеры для всех полей
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Map<String, Object>> getOrders() {
        return orders;
    }

    public void setOrders(List<Map<String, Object>> orders) {
        this.orders = orders;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getOrder() {
        return order;
    }

    public void setOrder(Map<String, Object> order) {
        this.order = order;
    }
}