import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.qameta.allure.Epic;
import io.qameta.allure.Story;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.LoginPage;
import pages.MainPage;
import pages.WebFormPage;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Appium")
@Story("Appium Browser")
public class AppiumBrowserTests {
    //Запуск: appium --allow-insecure chromedriver_autodownload
    private static final String SERVER = "http://127.0.0.1:4723/";
    private AndroidDriver driver;
    MainPage mainPage;

    @BeforeEach
    void setup() throws MalformedURLException {
        UiAutomator2Options options = new UiAutomator2Options();
        options
                .setPlatformName("Android")
                .setPlatformVersion("14")
                .setAutomationName("UiAutomator2")
                .setDeviceName("emulator-5554")
                .noReset()
                .withBrowserName("Chrome");

        driver = new AndroidDriver(new URL(SERVER), options);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Базовый тест Web form")
    void webFormTest() {
        String title = "Hands-On Selenium WebDriver with Java";
        String item = "Web form";
        mainPage = new MainPage(driver);
        WebFormPage webFormPage = mainPage.openWebFormPage();
        assertThat(webFormPage.getTitle()).isEqualTo(title);
        assertThat(webFormPage.getWebformText()).isEqualTo(item);
    }

    @Test
    @DisplayName("Успешный логин")
    void testLoginSuccess() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.with("user", "user");
        assertThat(loginPage.successBoxPresent()).isTrue();
        assertThat(loginPage.invalidCredentialsBoxPresent()).isFalse();
    }

    @Test
    @DisplayName("Негативный кейс - неверный пароль")
    void testLoginFailure() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.with("user", "test");
        assertThat(loginPage.successBoxPresent()).isFalse();
        assertThat(loginPage.invalidCredentialsBoxPresent()).isTrue();
    }
}
