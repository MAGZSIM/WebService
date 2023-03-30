import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WebService {
    private WebDriver driver; //объявляем драйвер (приватное поле для веб-драйвера и через переменную драйвер мы будем работать)

    @BeforeAll // перед всеми тестами
    public static void setupAll() {

        WebDriverManager.chromedriver().setup(); // настройка веб драйвера
    }

    @BeforeEach // перед каждым тестом
    public void beforeEach() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage"); // настройка работы с памятью для стабильного запуска хром-драйвера в среде CI
        options.addArguments("--no-sandbox"); // настройка работы с песочницей для стабильного запуска хром-драйвера в среде CI
        options.addArguments("--headless"); //запуск браузера без показа окна
        options.addArguments("--remote-allow-origins=*"); //обход 111 версии хрома для селениума, пока не обновят, иначе не запускается, даже без headless.
        driver = new ChromeDriver(options); // перед каждым тестом мы делаем новую версию хром-драйвера, чтобы не переносить какие-то артефакты в другой тест и передаем опции в конструктор хром-драйвера.
        driver.get("http://localhost:9999"); // открываем страницу
    }

    @AfterEach // после каждого теста, @AfterAll - выполняется после всех тестов.
    public void afterEach() {
        driver.quit(); //после каждого теста закрываем хром-драйвер
        driver = null; // и обнуляемся
}

    @Test
    void shouldTest () {
        driver.get("http://localhost:9999"); // открываем страницу
        driver.findElements(By.tagName("input")).get(0).sendKeys("Артур Пирожков"); // заполняем поле "Имя, Фамилия"
        driver.findElements(By.className("input__control")).get(1).sendKeys("+79261234567"); // заполняем поле телефон
        driver.findElement(By.cssSelector("[data-test-id = agreement]")).click(); // клик чекбокса
        driver.findElement(By.className("button__text")).click(); // клик кнопки отправить
        String expected = "Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время."; //создаем константу
        String actual = driver.findElement(By.cssSelector("[data-test-id = order-success]")).getText().trim();
        assertEquals(expected, actual);
    }
    // лучший вариант заполнения формы:
    @Test
    public void fillingForm(){
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Синицын Игорь Константинович");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79251234567");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.cssSelector("button.button")).click();
        var actualText = driver.findElement(By.cssSelector("[data-test-id=order-success]")).getText().trim();
        assertEquals("Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.", actualText);
    }

    @Test
    public void withoutName(){
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79251234567");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.cssSelector("button.button")).click();
        assertEquals("Поле обязательно для заполнения",
                driver.findElement(By.cssSelector("[data-test-id=name].input_invalid .input__sub")).getText().trim()); // "пробел перед input__sub" значит, что ищет на разном уровне вложенности
    }

    @Test
    public void nameInvalid(){
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Artur Pirojkov");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79251234567");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.cssSelector("button.button")).click();
        assertEquals("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.",
                driver.findElement(By.cssSelector("[data-test-id=name].input_invalid .input__sub")).getText().trim()); // "пробел перед input__sub" значит, что ищет на разном уровне вложенности
    }

    @Test
    public void withoutPhone(){
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Синицын Игорь Константинович");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.cssSelector("button.button")).click();
        assertEquals("Поле обязательно для заполнения",
                driver.findElement(By.cssSelector("[data-test-id=phone].input_invalid .input__sub")).getText().trim()); // "пробел перед input__sub" значит, что ищет на разном уровне вложенности
    }
    @Test
    public void phoneInvalid(){
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Синицын Игорь Константинович");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("89261234567");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.cssSelector("button.button")).click();
        assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.",
                driver.findElement(By.cssSelector("[data-test-id=phone].input_invalid .input__sub")).getText().trim()); // "пробел перед input__sub" значит, что ищет на разном уровне вложенности
    }

    @Test
    public void checkBoxInvalid(){
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Синицын Игорь Константинович");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79261234567");
        driver.findElement(By.cssSelector("button.button")).click();
        assertTrue(driver.findElement(By.cssSelector("[data-test-id=agreement].input_invalid")).isDisplayed());
    }

}


