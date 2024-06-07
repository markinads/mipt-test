import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.html5.LocalStorage;
import org.openqa.selenium.html5.SessionStorage;
import org.openqa.selenium.html5.WebStorage;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static util.SeleniumUtils.*;

public class SeleniumBrowserAgnosticTests {
    private static final String INFINITE_SCROLL_PAGE = "infinite-scroll.html";
    private static final String SHADOW_DOM_PAGE = "shadow-dom.html";
    private static final String COOKIES_PAGE = "cookies.html";
    private static final String IFRAMES_PAGE = "iframes.html";
    private static final String DIALOG_BOXES_PAGE = "dialog-boxes.html";
    private static final String WEB_STORAGE_PAGE = "web-storage.html";
    static WebDriver driver;

    @BeforeEach
    void start() {
        driver = new ChromeDriver();
        driver.get(BASE_URL);
    }

    @AfterEach
    void close() {
        driver.close();
    }

    @Test
    @DisplayName("Infinite Scroll Test")
    void infiniteScrollTest() throws InterruptedException, IOException {
        openPage(driver, INFINITE_SCROLL_PAGE);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        By pLocator = By.tagName("p");
        List<WebElement> paragraphs;
        int initParagraphsNumber = 1;
        WebElement lastParagraph = driver.findElement(By.xpath("//p[1]"));

        String script = "arguments[0].scrollIntoView();";
        try {
            for (int i = 0; i < 30; i++) {
                js.executeScript(script, lastParagraph);
                paragraphs = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(pLocator, initParagraphsNumber));
                initParagraphsNumber = paragraphs.size();
                System.out.println(initParagraphsNumber);
                lastParagraph = driver.findElement(By.xpath(String.format("//p[%d]", initParagraphsNumber)));
            }
        }
        catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
            File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(scrFile, new File("./image.png"));
        }
    }

    @Test
    @DisplayName("Shadow DOM Test")
    void shadowDOMTest() throws InterruptedException {
        openPage(driver, SHADOW_DOM_PAGE);
        assertThrows(NoSuchElementException.class, () -> driver.findElement(By.cssSelector("p")));
        WebElement content = driver.findElement(By.id("content"));
        SearchContext shadowRoot = content.getShadowRoot();
        WebElement textElement = shadowRoot.findElement(By.cssSelector("p"));
        assertThat(textElement.getText()).contains("Hello Shadow DOM");
    }

    @Test
    @DisplayName("Cookies Test")
    public void cookiesTest() throws InterruptedException {
        openPage(driver, COOKIES_PAGE);
        WebDriver.Options options = driver.manage();
        Set<Cookie> cookies = options.getCookies();
        assertThat(cookies).hasSize(2);

        Cookie username = options.getCookieNamed("username");
        assertThat(username.getValue()).isEqualTo("John Doe");

        Cookie date = options.getCookieNamed("date");
        assertThat(date.getValue()).isEqualTo("10/07/2018");

        Cookie newCookie = new Cookie("new-cookie-key", "new-cookie-value");
        options.addCookie(newCookie);
        options.deleteCookie(date);

        driver.findElement(By.id("refresh-cookies")).click();
        WebElement cookiesList = driver.findElement(By.id("cookies-list"));
        System.out.println(cookiesList.getText());

        assertThat(cookiesList.getText()).contains("new-cookie-key=new-cookie-value");
        assertThat(cookiesList.getText()).doesNotContain("date=");
    }

    @Test
    @DisplayName("iFrames Test")
    void iFramesTests() throws InterruptedException {
        openPage(driver, IFRAMES_PAGE);
        assertThat(driver.findElement(By.className("display-6")).getText()).contains("IFrame");
        WebElement iframeElement = driver.findElement(By.id("my-iframe"));
        driver.switchTo().frame(iframeElement);
        assertThrows(NoSuchElementException.class, () -> driver.findElement(By.className("display-6")));
        assertThat(driver.findElement(By.className("lead")).getText()).contains("Lorem ipsum dolor sit amet");
        driver.switchTo().defaultContent();
        assertThrows(NoSuchElementException.class, () -> driver.findElement(By.className("lead")));
    }

    @Test
    @DisplayName("Dialog Boxes Test")
    void dialogBoxesTest() throws InterruptedException {
        openPage(driver, DIALOG_BOXES_PAGE);
        String initHtml = driver.getPageSource();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        WebElement alertElement = driver.findElement(By.id("my-alert"));
        alertElement.click();
        Alert alert = driver.switchTo().alert();
        assertThat(alert.getText()).isEqualTo("Hello world!");
        alert.accept();
        // Проверяем, что html код не изменился
        assertThat(driver.getPageSource()).isEqualTo(initHtml);

        WebElement confirmElement = driver.findElement(By.id("my-confirm"));
        confirmElement.click();
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();
        assertThat(driver.findElement(By.id("confirm-text")).getText()).isEqualTo("You chose: true");
        driver.findElement(By.id("my-confirm")).click();
        driver.switchTo().alert().dismiss();
        assertThat(driver.findElement(By.id("confirm-text")).getText()).isEqualTo("You chose: false");

        driver.findElement(By.id("my-prompt")).click();
        driver.switchTo().alert().sendKeys("Test");
        driver.switchTo().alert().accept();
        assertThat(driver.findElement(By.id("prompt-text")).getText()).isEqualTo("You typed: Test");

        WebElement modalElement = driver.findElement(By.id("my-modal"));
        modalElement.click();
        WebElement save = driver.findElement(By.xpath("//button[normalize-space() = 'Save changes']"));
        wait.until(ExpectedConditions.elementToBeClickable(save));
        save.click();

        // При клике вне модального окна оно должно закрыться
        modalElement.click();
        WebElement modalDialog = driver.findElement(By.className("modal-dialog"));
        wait.until(ExpectedConditions.elementToBeClickable(modalDialog));
        Rectangle modalRect = modalDialog.getRect();
        new Actions(driver)
                .moveToLocation(modalRect.x - 1, modalRect.y - 1)
                .click()
                .perform();

        // Соседняя кнопка должна быть недоступна при активном алерте
        confirmElement.click();
        assertThatThrownBy(alertElement::click).isInstanceOf(UnhandledAlertException.class);
    }

    @Test
    @DisplayName("Web Storage Test")
    void webStorageTest() throws InterruptedException {
        openPage(driver, WEB_STORAGE_PAGE);
        WebStorage webStorage = (WebStorage) driver;
        LocalStorage localStorage = webStorage.getLocalStorage();
        SessionStorage sessionStorage = webStorage.getSessionStorage();

        assertThat(localStorage.size()).isEqualTo(0);
        localStorage.setItem("new element", "new value");
        driver.findElement(By.id("display-local")).click();
        assertThat(driver.findElement(By.id("local-storage")).getText())
                .isEqualTo("{\"new element\":\"new value\"}");

        assertThat(sessionStorage.size()).isEqualTo(2);
        sessionStorage.removeItem("name");
        driver.findElement(By.id("display-session")).click();
        assertThat(driver.findElement(By.id("session-storage")).getText())
                .isEqualTo("{\"lastname\":\"Doe\"}");
    }
}
