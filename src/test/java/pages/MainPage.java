package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;

import static org.assertj.core.api.Assertions.assertThat;

public class MainPage extends BasePage {
    public static final String URL = "https://bonigarcia.dev/selenium-webdriver-java/";

    @FindBy(linkText = "Login form")
    @CacheLookup
    WebElement loginFormButton;

    @FindBy(linkText = "Web form")
    @CacheLookup
    WebElement webFormButton;

    @FindBy(linkText = "Download files")
    @CacheLookup
    WebElement downloadButton;

    public MainPage(String browser) {
        super(browser, URL);
    }

    @Step("Открытие страницы Login Form")
    public LoginPage openLoginPage() {
        click(loginFormButton);
        assertThat(driver.getCurrentUrl()).isEqualTo(LoginPage.URL);
        return new LoginPage(driver);
    }

    @Step("Открытие страницы Web Form")
    public WebFormPage openWebFormPage() {
        click(webFormButton);
        assertThat(driver.getCurrentUrl()).isEqualTo(WebFormPage.URL);
        return new WebFormPage(driver);
    }

    @Step("Открытие страницы Download files")
    public DownloadPage openDownloadPage() {
        click(downloadButton);
        assertThat(driver.getCurrentUrl()).isEqualTo(DownloadPage.URL);
        return new DownloadPage(driver);
    }
}
