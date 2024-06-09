import extension.AllureExtension;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pages.LoginPage;
import pages.MainPage;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Selenium")
@Feature("Bonigarcia")
@Story("Login form")
public class LoginPageTests {
    MainPage mainPage;

    @BeforeEach
    void setup() {
        mainPage = new MainPage("chrome");
    }

    @AfterEach
    void teardown() {
        mainPage.quit();
    }

    @Test
    @DisplayName("Страница авторизации - успешный логин")
    void testLoginSuccess() {
        LoginPage loginPage = mainPage.openLoginPage();
        loginPage.with("user", "user");
        assertThat(loginPage.successBoxPresent()).isTrue();
        assertThat(loginPage.invalidCredentialsBoxPresent()).isFalse();
    }

    @Test
    @DisplayName("Страница авторизации - негативный кейс - неверный пароль")
    void testLoginFailure() {
        LoginPage loginPage = mainPage.openLoginPage();
        loginPage.with("user", "test");
        assertThat(loginPage.successBoxPresent()).isFalse();
        assertThat(loginPage.invalidCredentialsBoxPresent()).isTrue();
    }

    @Test
    @DisplayName("Страница авторизации - проверка содержимого header/footer")
    void testLoginPageHeaderFooter() {
        LoginPage loginPage = mainPage.openLoginPage();
        assertThat(loginPage.header().getTitleText()).isEqualTo("Hands-On Selenium WebDriver with Java");
        assertThat(loginPage.header().getSubTitleText()).isEqualTo("Practice site");
        assertThat(loginPage.footer().getCopyrightText()).isEqualTo("Copyright © 2021-2024 Boni García");
        assertThat(loginPage.footer().getAuthorLink()).isEqualTo("https://bonigarcia.dev/");
    }
}
