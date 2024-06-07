package util;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;
import java.util.function.Function;

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
}
