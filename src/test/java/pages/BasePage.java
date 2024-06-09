package pages;

import component.FooterComponent;
import component.HeaderComponent;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import patterns.WebDriverFactory;

import java.time.Duration;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

public class BasePage {
    static final Logger log = getLogger(lookup().lookupClass());

    WebDriver driver;
    WebDriverWait wait;
    int timeoutSec = 3;

    HeaderComponent header;
    FooterComponent footer;

    public BasePage(String browser, String url) {
        driver = WebDriverFactory.createWebDriver(browser);
        this.header = new HeaderComponent(driver);
        this.footer = new FooterComponent(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec));
        PageFactory.initElements(driver, this);
        visit(url);
        log.info("Open {} in \"{}\" browser", url, browser);
    }

    public BasePage(WebDriver driver, String url) {
        this.driver = driver;
        this.header = new HeaderComponent(driver);
        this.footer = new FooterComponent(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSec));
        PageFactory.initElements(driver, this);
        visit(url);
        log.info("Open {}", url);
    }

    public void quit() {
        if (driver != null) {
            log.info("Close {}", driver.getCurrentUrl());
            driver.quit();
        }
    }

    public void visit(String url) {
        driver.get(url);
    }

    public void back() {
        driver.navigate().back();
    }

    public void refresh() {
        driver.navigate().refresh();
        PageFactory.initElements(driver, this);
    }

    public void click(WebElement element) {
        element.click();
    }

    public void type(WebElement element, String text) {
        element.sendKeys(text);
    }

    public void changeState(WebElement element, String method) {
        switch (method) {
            case ("mouse") ->
                new Actions(driver)
                        .click(element)
                        .perform();
            case ("keyboard") ->
                element.sendKeys(Keys.SPACE);
            default -> log.warn("Unknown action method \"{}\". Click or press space on \"{}\"", method, element.getAccessibleName());
        }
    }

    public boolean isDisplayed(WebElement element) {
        return isDisplayed(ExpectedConditions.visibilityOf(element));
    }

    public boolean isDisplayed(ExpectedCondition<?> expectedCondition) {
        try {
            wait.until(expectedCondition);
        } catch (TimeoutException e) {
            log.warn("Timeout of {} wait for element ", timeoutSec);
            return false;
        }
        return true;
    }

    public boolean isSelected(WebElement element) {
        return element.isSelected();
    }

    public HeaderComponent header() {
        return header;
    }
    public FooterComponent footer() { return footer; }
}
