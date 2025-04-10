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

    @Test
    void shouldSubmitValidForm() {
        fillValidName();
        fillValidPhone();
        checkAgreement();
        submitForm();

        String actualText = driver.findElement(By.cssSelector("[data-test-id=order-success]")).getText();
        assertEquals("Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.",
                actualText.trim());
    }

    @Test
    void shouldShowErrorForLatinName() {
        fillInvalidLatinName();
        fillValidPhone();
        checkAgreement();
        submitForm();

        assertNameError("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.");
    }

    @Test
    void shouldShowErrorForEmptyName() {
        // Не заполняем имя (оставляем пустым)
        fillValidPhone();
        checkAgreement();
        submitForm();

        assertNameError("Поле обязательно для заполнения");
    }

    @Test
    void shouldShowErrorForNameWithNumbers() {
        fillNameWithNumbers();
        fillValidPhone();
        checkAgreement();
        submitForm();

        assertNameError("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.");
    }

    @Test
    void shouldShowErrorForShortPhone() {
        fillValidName();
        fillInvalidShortPhone();
        checkAgreement();
        submitForm();

        assertPhoneError("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.");
    }

    @Test
    void shouldShowErrorForEmptyPhone() {
        fillValidName();
        // Не заполняем телефон (оставляем пустым)
        checkAgreement();
        submitForm();

        assertPhoneError("Поле обязательно для заполнения");
    }

    @Test
    void shouldShowErrorForPhoneWithLetters() {
        fillValidName();
        fillPhoneWithLetters();
        checkAgreement();
        submitForm();

        assertPhoneError("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.");
    }

    @Test
    void shouldShowErrorForUncheckedAgreement() {
        fillValidName();
        fillValidPhone();
        // Не отмечаем чекбокс согласия
        submitForm();

        assertTrue(driver.findElement(By.cssSelector("[data-test-id=agreement].input_invalid")).isDisplayed());
    }

    // Вспомогательные методы для заполнения полей
    private void fillValidName() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иванов Иван");
    }

    private void fillInvalidLatinName() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Ivanov Ivan");
    }

    private void fillNameWithNumbers() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иван123");
    }

    private void fillValidPhone() {
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79270000000");
    }

    private void fillInvalidShortPhone() {
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+7927000000");
    }

    private void fillPhoneWithLetters() {
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+7abcdefghij");
    }

    private void checkAgreement() {
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
    }

    private void submitForm() {
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