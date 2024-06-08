package util;

import com.google.common.io.Files;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

import static com.codeborne.selenide.Screenshots.takeScreenShotAsFile;

public class SeleniumUtils {
    public final static String BASE_URL = "https://bonigarcia.dev/selenium-webdriver-java";

    public static void openPage(WebDriver driver, String pageName) throws InterruptedException {
        WebElement webFormButton = driver.findElement(By.xpath("//a[@href = '" + pageName + "']"));
        Thread.sleep(2000);
        webFormButton.click();
    }

    public static void clickPrevious(WebDriver driver) throws InterruptedException {
        WebElement link = driver.findElement(By.xpath("//a[@class='page-link' and text()='Previous']"));
        link.click();
        Thread.sleep(2000);
    }

    public static void clickNext(WebDriver driver) throws InterruptedException {
        WebElement link = driver.findElement(By.xpath("//a[@class='page-link' and text()='Next']"));
        link.click();
        Thread.sleep(2000);
    }

    // Аргументы:
    // 1) WebDriver
    // 2) pagination panel size
    // 3) active element number
    // 4) disabled|enabled element number
    // 5) false if check disable, true if check enable
    public static boolean checkState(WebDriver driver, int size, int active, int disabled, boolean invert) {
        List<WebElement> pagination = driver.findElements(By.xpath("//ul[@class='pagination']/li"));
        if (pagination.size() != size) {
            return false;
        }
        if (!pagination.get(active).getAttribute("class").contains("active")) {
            return false;
        }
        return pagination.get(disabled).getAttribute("class").contains("disabled") != invert;
    }

    public static List<WebElement> getDropDownElements(WebDriver driver, String ddMenu, String ddList, Function<WebElement, Actions> action) throws InterruptedException {
        action.apply(driver.findElement(By.id(ddMenu))).perform();
        Thread.sleep(2000);
        WebElement ddmenu;
        try {
            ddmenu = driver.findElement(By.id(ddList));
        } catch (NoSuchElementException e) {
            ddmenu = driver.findElement(By.className("dropdown-menu"));
        }
        List<WebElement> elements = ddmenu.findElements(By.className("dropdown-item"));
        if (!elements.isEmpty()) {
            new Actions(driver)
                    .doubleClick(elements.stream().findFirst().get())
                    .perform();
        }
        return elements;
    }

    public static String formatDateField(String value) {
        if (value.length() == 1) {
            return "0" + value;
        } else {
            return value;
        }
    }

    @Attachment(value = "Screenshot", type = "image/png")
    @Step("Capture screenshot with Selenide")
    public static byte[] captureScreenshotSelenide() throws IOException {
        return Files.toByteArray(takeScreenShotAsFile());
    }

    @Step("Capture screenshot with Selenide (extension)")
    public static void captureScreenshotSelenideSpoiler() throws IOException {
        Allure.addAttachment("Screenshot", new ByteArrayInputStream(Files.toByteArray(takeScreenShotAsFile())));
    }
}
