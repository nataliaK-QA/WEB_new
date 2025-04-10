package ru.netology;

import org.apache.commons.lang3.RandomStringUtils;
import java.util.Random;

public class TestDataGenerator {
    public static String generateValidName() {
        String[] surnames = {"Иванов", "Петров", "Сидоров", "Смирнов", "Кузнецов"};
        String[] names = {"Иван", "Петр", "Алексей", "Дмитрий", "Сергей"};
        Random random = new Random();
        return surnames[random.nextInt(surnames.length)] + " " + names[random.nextInt(names.length)];
    }

    public static String generateInvalidName() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    public static String generateValidPhone() {
        return "+7" + RandomStringUtils.randomNumeric(10);
    }

    public static String generateInvalidPhone() {
        return "+7" + RandomStringUtils.randomNumeric(9);
    }
}