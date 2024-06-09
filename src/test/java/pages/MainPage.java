package pages;

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

    public MainPage(String browser) {
        super(browser, URL);
    }

    public LoginPage openLoginPage() {
        click(loginFormButton);
        assertThat(driver.getCurrentUrl()).isEqualTo(LoginPage.URL);
        return new LoginPage(driver);
    }

    public WebFormPage openWebFormPage() {
        click(webFormButton);
        assertThat(driver.getCurrentUrl()).isEqualTo(WebFormPage.URL);
        return new WebFormPage(driver);
    }
}
