package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {
    public static final String URL = "https://bonigarcia.dev/selenium-webdriver-java/login-form.html";

    @FindBy(id = "username")
    @CacheLookup
    WebElement usernameInput;

    @FindBy(id = "password")
    @CacheLookup
    WebElement passwordInput;

    @FindBy(css = "button")
    @CacheLookup
    WebElement submitButton;

    @FindBy(id = "success")
    @CacheLookup
    WebElement successBox;

    @FindBy(id = "invalid")
    @CacheLookup
    WebElement invalidCredentialsBox;

    public LoginPage(WebDriver driver) {
        super(driver, URL);
    }

    @Step("Попытка авторизации с логином {username}, паролем {password}")
    public void with(String username, String password) {
        type(usernameInput, username);
        type(passwordInput, password);
        click(submitButton);
    }

    @Step("Проверка сообщения об успешной авторизации")
    public boolean successBoxPresent() {
        return isDisplayed(successBox);
    }

    @Step("Проверка сообщения о неуспешной авторизации")
    public boolean invalidCredentialsBoxPresent() {
        return isDisplayed(invalidCredentialsBox);
    }
}
