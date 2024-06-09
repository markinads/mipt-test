package component;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class FooterComponent {
    private WebDriver driver;

    @FindBy(xpath = "//span[@class = 'text-muted']")
    @CacheLookup
    private WebElement copyright;

    @FindBy(xpath = "//span[@class = 'text-muted']/a")
    @CacheLookup
    private WebElement author;

    public FooterComponent(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public String getCopyrightText() {
        return copyright.getText();
    }

    public String getAuthorLink() {return author.getAttribute("href");
    }
}
