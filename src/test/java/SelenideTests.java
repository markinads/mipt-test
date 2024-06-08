import com.codeborne.selenide.*;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import util.SeleniumUtils;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.attributeMatching;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static util.SeleniumUtils.BASE_URL;

@Tag("selenide")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SelenideTests {
    private static final String WEBFORM_PAGE = "web-form.html";
    private static final String INFINITE_SCROLL_PAGE = "infinite-scroll.html";
    private static final String IMAGES_PAGE = "loading-images.html";

    @BeforeAll
    void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
        options.addArguments("--no-sandbox"); // Bypass OS security model
        options.addArguments("--headless"); // without browser interface
        Configuration.browserCapabilities = options;
    }

    @Test
    void openForm() {
        open(BASE_URL);
        WebElement webFormButton = $(By.xpath("//div[@class = 'card-body']")).find(By.xpath(".//a[contains(@class, 'btn')]"));
        webFormButton.click();
        SelenideElement actualH1 = $(By.xpath("//h1[@class='display-6']"));
        actualH1.shouldHave(text("Web form"));
    }

    @Test
    @DisplayName("Проверка загрузки файла")
    void fileUploadTest() {
        open(BASE_URL + "/" + WEBFORM_PAGE);

        String fileName = "article.pdf";
        String absolutePath = new File(fileName).getAbsolutePath();

        $(By.name("my-file")).sendKeys(absolutePath);
        $(By.xpath("//button[text()='Submit']")).click();

        SelenideElement result = $(By.xpath("//p[@class = 'lead']"));
        result.shouldHave(text("Received!"));
    }

    @Test
    @DisplayName("Check screenshot attachment")
    void infiniteScrollTestWithAttach() throws IOException {
        open(BASE_URL + "/" + INFINITE_SCROLL_PAGE);
        WebDriver driver = webdriver().object();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        By pLocator = By.tagName("p");
        List<WebElement> paragraphs = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(pLocator, 0));
        int initParagraphsNumber = paragraphs.size();

        WebElement lastParagraph = $(By.xpath(String.format("//p[%d]", initParagraphsNumber)));
        String script = "arguments[0].scrollIntoView();";
        js.executeScript(script, lastParagraph);

        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(pLocator, initParagraphsNumber));
        SeleniumUtils.captureScreenshotSelenide();
        SeleniumUtils.captureScreenshotSelenideSpoiler();
    }

    @Test
    void loadingImagesDefaultWaitTest() {
        open(BASE_URL + "/" + IMAGES_PAGE);
        $("#compass").shouldHave(attributeMatching("src", ".*compass.*"));
    }

    @Test
    void loadingImagesWithUpdatedTimeoutWaitTest() {
        open(BASE_URL + "/" + IMAGES_PAGE);
        Configuration.timeout = 10_000;
        $("#landscape").shouldHave(attributeMatching("src", ".*landscape.*"));
    }

    @Test
    void loadingImagesWithExplicitTimeoutWaitTest() {
        open(BASE_URL + "/" + IMAGES_PAGE);
        ElementsCollection images = $$("img").filter(Condition.visible);
        images.shouldHave(size(4), Duration.ofSeconds(10));
    }
}
