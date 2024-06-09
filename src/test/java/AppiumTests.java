import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Epic;
import io.qameta.allure.Story;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Interaction;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import steps.AppiumSteps;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Appium")
public class AppiumTests {
    private static final String APP = "https://github.com/appium-pro/TheApp/releases/download/v1.12.0/TheApp.apk";
    private static final String SERVER = "http://127.0.0.1:4723/";
    private AndroidDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setup() throws MalformedURLException {
        DesiredCapabilities ds = new DesiredCapabilities();
        ds.setCapability("platformName", "Android");
        ds.setCapability("platformVersion", "14");
        ds.setCapability("deviceName", "emulator-5554");
        ds.setCapability("app", APP);
        ds.setCapability("automationName", "UiAutomator2");

        driver = new AndroidDriver(new URL(SERVER), ds);
        wait = new WebDriverWait(driver, Duration.ofSeconds(3));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }


    @Test
    @DisplayName("Проверка заголовка")
    void testTitleApp() {
        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.xpath("//android.widget.TextView")));
        assertThat(title.isDisplayed()).isTrue();
        assertThat(title.getText()).isEqualTo("TheApp");
    }

    @Test
    @DisplayName("Проверка Echo Box")
    void testEcho() {
        String text = "Some text";
        assertThat(AppiumSteps.echoTo(driver, text)).isEqualTo(text);
    }

    @Test
    @DisplayName("Проверка успешного логина")
    void testSuccessfulLogin() {
        String username = "alice";
        AppiumSteps.loginWith(driver, username, "mypassword");
        WebElement loginMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.xpath("//android.widget.TextView[contains(@text, 'You are logged in')]")));
        assertThat(loginMessage.getText()).contains(username);
    }

    @Test
    @DisplayName("Проверка неуспешного логина")
    void testUnsuccessfulLogin() {
        String username = "alice";
        AppiumSteps.loginWith(driver, username, username);
        WebElement loginMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.xpath("//android.widget.TextView[contains(@text, 'Invalid login credentials')]")));
        assertThat(loginMessage.getText()).contains("Invalid login");
    }
}
