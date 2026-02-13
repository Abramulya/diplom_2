package config;

import com.github.javafaker.Faker;
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
        Faker faker = new Faker();
        String lastName = faker.name().lastName();
        return lastName + "@yandex.ru";
    }

    public static String getRandomPassword() {
        Faker faker = new Faker();
        String password = faker.harryPotter().character();
        return password;
    }

    public static String getRandomName() {
        Faker faker = new Faker();
        String firstName = faker.name().firstName();
        return firstName;
    }
}
