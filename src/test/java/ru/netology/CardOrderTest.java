package ru.netology;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.jupiter.api.Assertions.*;

public class CardOrderTest {
    private WebDriver driver;

    @BeforeAll
    static void setupAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        driver.get("http://localhost:9999");
    }

    @AfterEach
    void tearDown() {
        driver.quit();
        driver = null;
    }

    // Позитивный тест
    @Test
    void shouldSubmitValidForm() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иванов Иван");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79270000000");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.cssSelector("button")).click();

        String actualText = driver.findElement(By.cssSelector("[data-test-id=order-success]")).getText();
        assertEquals("Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.",
                actualText.trim());
    }

    // Тесты валидации имени
    @Test
    void shouldShowErrorForLatinName() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Ivanov Ivan");
        submitForm();
        assertNameError("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.");
    }

    @Test
    void shouldShowErrorForEmptyName() {
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79270000000");
        submitForm();
        assertNameError("Поле обязательно для заполнения");
    }

    @Test
    void shouldShowErrorForNameWithNumbers() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иван123");
        submitForm();
        assertNameError("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.");
    }

    // Тесты валидации телефона
    @Test
    void shouldShowErrorForShortPhone() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иванов Иван");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+7927000000");
        submitForm();
        assertPhoneError("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.");
    }

    @Test
    void shouldShowErrorForEmptyPhone() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иванов Иван");
        submitForm();
        assertPhoneError("Поле обязательно для заполнения");
    }

    @Test
    void shouldShowErrorForPhoneWithLetters() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иванов Иван");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+7abcdefghij");
        submitForm();
        assertPhoneError("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.");
    }

    // Тест валидации чекбокса
    @Test
    void shouldShowErrorForUncheckedAgreement() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иванов Иван");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79270000000");
        driver.findElement(By.cssSelector("button")).click();
        assertTrue(driver.findElement(By.cssSelector("[data-test-id=agreement].input_invalid")).isDisplayed());
    }

    // Вспомогательные методы
    private void submitForm() {
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.cssSelector("button")).click();
    }

    private void assertNameError(String expectedError) {
        String actualError = driver.findElement(By.cssSelector("[data-test-id=name].input_invalid .input__sub")).getText();
        assertEquals(expectedError, actualError.trim());
    }

    private void assertPhoneError(String expectedError) {
        String actualError = driver.findElement(By.cssSelector("[data-test-id=phone].input_invalid .input__sub")).getText();
        assertEquals(expectedError, actualError.trim());
    }
}