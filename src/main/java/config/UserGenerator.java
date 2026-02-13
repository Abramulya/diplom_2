package config;

import model.User;

public class UserGenerator {

    public static User getRandomUser() {
        return new User(
                getRandomEmail(),
                getRandomPassword(),
                getRandomName()
        );
    }

    public static String getRandomEmail() {
        return "testuser" + System.currentTimeMillis() + "@yandex.ru";
    }

    public static String getRandomPassword() {
        return "pass" + (int)(Math.random() * 10000);
    }

    public static String getRandomName() {
        return "User" + System.currentTimeMillis();
    }
}
