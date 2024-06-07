import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SeleniumLocatorsTest {
    public final static String BASE_URL = "https://bonigarcia.dev/selenium-webdriver-java/web-form.html ";
    static ChromeOptions options;
    WebDriver driver;

    @BeforeAll
    static void setup() {
        options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
        options.addArguments("--no-sandbox"); // Bypass OS security model
        options.addArguments("--headless"); // without browser interface
    }

    @BeforeEach
    void start() {
        driver = new ChromeDriver(options);
        driver.get(BASE_URL);
    }

    @AfterEach
    void close() {
        driver.close();
    }

    @Test
    @Tag("selenium")
    void baseLocatorsTest() {
        driver.manage().window().fullscreen();
        List<WebElement> h1Elements = driver.findElements(By.tagName("h1"));
        assertEquals(2, h1Elements.size());
        assertEquals("Hands-On Selenium WebDriver with Java", h1Elements.get(0).getAccessibleName());
        assertEquals("Web form", h1Elements.get(1).getAccessibleName());
        assertEquals("Web form", driver.findElement(By.className("display-6")).getAccessibleName());
        assertEquals("Text input", driver.findElement(By.id("my-text-id")).getAccessibleName());
        assertEquals("Password", driver.findElement(By.name("my-password")).getAccessibleName());
        assertEquals("Textarea", driver.findElement(By.name("my-textarea")).getAccessibleName());
        assertEquals("Disabled input", driver.findElement(By.name("my-disabled")).getAccessibleName());
        assertEquals("Readonly input", driver.findElement(By.name("my-readonly")).getAccessibleName());
        assertEquals("Dropdown (select)", driver.findElement(By.tagName("select")).getAccessibleName());
        assertEquals("Dropdown (datalist)", driver.findElement(By.name("my-datalist")).getAccessibleName());
        assertEquals("Checked checkbox", driver.findElement(By.id("my-check-1")).getAccessibleName());
        assertEquals("Default checkbox", driver.findElement(By.id("my-check-2")).getAccessibleName());
        assertEquals("Checked radio", driver.findElement(By.id("my-radio-1")).getAccessibleName());
        assertEquals("Default radio", driver.findElement(By.id("my-radio-2")).getAccessibleName());
        assertEquals("Submit", driver.findElement(By.tagName("button")).getAccessibleName());
        assertEquals("Color picker", driver.findElement(By.name("my-colors")).getAccessibleName());
        assertEquals("Date picker", driver.findElement(By.name("my-date")).getAccessibleName());
        assertEquals("Example range ", driver.findElement(By.name("my-range")).getAccessibleName());
        assertEquals("Return to index", driver.findElement(By.linkText("Return to index")).getAccessibleName());
    }

    @Test
    @Tag("selenium")
    void cssSelectorsTest() {
        assertEquals("Text input", driver.findElement(By.cssSelector("#my-text-id")).getAccessibleName());
        assertEquals("Web form", driver.findElement(By.cssSelector(".display-6")).getText());
        assertEquals("Password", driver.findElement(By.cssSelector("[name=my-password]")).getAccessibleName());
        assertEquals("submit", driver.findElement(By.cssSelector("button")).getAttribute("type"));
        assertEquals("my-range", driver.findElement(By.cssSelector("[step='1']")).getAttribute("name"));
        assertEquals("my-select", driver.findElement(By.cssSelector("select.form-select")).getAttribute("name"));
        assertTrue(driver.findElement(By.cssSelector("input#my-check-1")).isSelected());
        assertEquals("Submit", driver.findElement(By.cssSelector("button[type='submit']")).getAccessibleName());
        assertEquals("Color picker", driver.findElement(By.cssSelector(".form-control.form-control-color")).getAccessibleName());
        assertEquals("Return to index", driver.findElement(By.cssSelector("a[href^='./']")).getText());
        assertEquals("Color picker", driver.findElement(By.cssSelector("[class$='color']")).getAccessibleName());
        assertEquals("Example range ", driver.findElement(By.cssSelector("[name*='range']")).getAccessibleName());
        assertEquals("Return to index", driver.findElement(By.cssSelector("a[href='./index.html']")).getText());
        assertEquals("Checked radio", driver.findElement(By.cssSelector("label.form-check-label input[type='radio']")).getAccessibleName());
        assertEquals("Default checkbox", driver.findElement(By.cssSelector("label.form-check-label:nth-child(2) input")).getAccessibleName());
    }

    @Test
    @Tag("selenium")
    void xpathSelectorsTest() {
        // абсолютный путь
        assertEquals("Practice site", driver.findElement(By.xpath("/html/body/main/div/div/div/h5")).getText());
        // относительный путь
        assertEquals("Practice site", driver.findElement(By.xpath("//h5")).getText());
        // по тегу
        assertEquals("Dropdown (select)", driver.findElement(By.xpath("//select")).getAccessibleName());
        // по тексту
        assertEquals("Web form", driver.findElement(By.xpath("//h1[text()='Web form']")).getText());
        // по частичному тексту
        assertEquals("Web form", driver.findElement(By.xpath("//h1[contains(text(),'form')]")).getText());
        // по атрибуту
        assertEquals("Example range ", driver.findElement(By.xpath("//input[@type='range']")).getAccessibleName());
        // по предку
        assertEquals("Text input", driver.findElement(By.xpath("//form[@action='submitted-form.html']/div/div/label/input")).getAccessibleName());
        // по потомку
        assertEquals("form-check", driver.findElement(By.xpath("//input[@id='my-radio-1']/../..")).getAttribute("class"));
    }
}
