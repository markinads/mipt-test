package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import steps.AllureSteps;

import java.io.File;
import java.io.IOException;

public class DownloadPage extends BasePage {
    public static final String URL = "https://bonigarcia.dev/selenium-webdriver-java/download.html";

    @FindBy(xpath = "//a[@download='webdrivermanager.png']")
    @CacheLookup
    WebElement wdmImage;

    @FindBy(xpath = "//a[@download='webdrivermanager.pdf']")
    @CacheLookup
    WebElement wdmDoc;

    @FindBy(xpath = "//a[@download='selenium-jupiter.png']")
    @CacheLookup
    WebElement sjImage;

    @FindBy(xpath = "//a[@download='selenium-jupiter.pdf']")
    @CacheLookup
    WebElement sjDoc;

    public DownloadPage(WebDriver driver) {
        super(driver, URL);
    }

    public void downloadWdmImage(File destination) {
        AllureSteps.download(wdmImage.getAttribute("href"), destination);
    }

    public void downloadWdmDoc(File destination) {
        AllureSteps.download(wdmDoc.getAttribute("href"), destination);
    }

    public void downloadSjImage(File destination) {
        AllureSteps.download(sjImage.getAttribute("href"), destination);
    }

    public void downloadSjDoc(File destination) {
        AllureSteps.download(sjDoc.getAttribute("href"), destination);
    }

}
