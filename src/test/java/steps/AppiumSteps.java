package steps;

import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AppiumSteps {

    @Step("Echo для {text}")
    public static String echoTo(WebDriver driver, String text) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement echo = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId("Echo Box")));
        echo.click();
        WebElement messageInput = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId("messageInput")));
        messageInput.sendKeys(text);
        WebElement saveBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.xpath("//android.widget.Button[@resource-id='messageSaveBtn']")));
        saveBtn.click();
        WebElement savedMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.xpath("//android.widget.TextView[@resource-id='savedMessage']")));
        return savedMessage.getText();
    }

    @Step("Логин с {user}/{password}")
    public static void loginWith(WebDriver driver, String user, String password) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        WebElement login = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId("Login Screen")));
        login.click();
        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(AppiumBy.accessibilityId("username")));
        username.sendKeys(user);
        driver.findElement(AppiumBy.accessibilityId("password")).sendKeys(password);
        driver.findElement(AppiumBy.accessibilityId("loginBtn")).click();
    }
}
