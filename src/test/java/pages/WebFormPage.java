package pages;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class WebFormPage extends BasePage {
    public static final String URL = "https://bonigarcia.dev/selenium-webdriver-java/web-form.html";

    @FindBy(css = ".display-6")
    @CacheLookup
    WebElement webformText;

    @FindBy(id = "my-text-id")
    @CacheLookup
    WebElement textInput;

    @FindBy(name = "my-textarea")
    @CacheLookup
    WebElement textArea;

    @FindBy(name = "my-disabled")
    @CacheLookup
    WebElement disabled;

    @FindBy(name = "my-select")
    @CacheLookup
    WebElement select;

    @FindBy(name = "my-file")
    @CacheLookup
    WebElement fileInput;

    @FindBy(xpath = "//button['submit']")
    @CacheLookup
    WebElement submitButton;

    @FindBy(id = "my-check-1")
    @CacheLookup
    WebElement checkbox1;

    @FindBy(id = "my-check-2")
    @CacheLookup
    WebElement checkbox2;

    @FindBy(xpath = "//p[text() = 'Received!']")
    @CacheLookup
    WebElement successSubmit;

    public WebFormPage(WebDriver driver) {
        super(driver, URL);
    }

    public void setInputTextText(String text) { type(textInput, text); }
    public String getInputTextText() { return textInput.getAttribute("value"); }

    public void setTextAreaText(String text) { type(textArea, text); }
    public String getTextAreaText() { return textArea.getAttribute("value"); }

    public void setDisabledText(String text) { type(disabled, text); }
    public void setDisabledToEnabled() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("document.querySelector(\"input[placeholder='Disabled input']\").disabled = false");
    }
    public String getDisabledText() { return disabled.getAttribute("value"); }

    public void selectByIndex(int index) {
        new Select(select).selectByIndex(index);
    }
    public String getSelectText() {
        return new Select(select).getFirstSelectedOption().getText();
    }
    public void uploadFile(String filePath) {
        type(fileInput, filePath);
        wait.until(ExpectedConditions.elementToBeClickable(submitButton));
        submitButton.click();
        wait.until(ExpectedConditions.visibilityOf(successSubmit));
    }
    public boolean urlContains(String value) {
        return driver.getCurrentUrl().contains(value);
    }
    public boolean isSubmitted() {
        return successSubmit.isDisplayed();
    }
    public void changeCheck1State(String method) {
        changeState(checkbox1, method);
    }
    public void changeCheck2State(String method) {
        changeState(checkbox2, method);
    }
    public boolean isSelectedCheck1() {
        return isSelected(checkbox1);
    }
    public boolean isSelectedCheck2() {
        return isSelected(checkbox2);
    }

    public String getTitle() { return driver.getTitle();}
    public String getWebformText() { return webformText.getText();}
}
