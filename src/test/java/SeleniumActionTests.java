import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static util.SeleniumUtils.*;

public class SeleniumActionTests {
    private static final String WEBFORM_PAGE = "web-form.html";
    private static final String NAVIGATION_PAGE = "navigation1.html";
    private static final String DROPDOWN_PAGE = "dropdown-menu.html";
    private static final String DRAGDROP_PAGE = "drag-and-drop.html";
    static WebDriver driver;

    @BeforeEach
    void start() {
        driver = new ChromeDriver();
        driver.get(BASE_URL);
    }
    @AfterEach
    void close() {
        driver.close();
    }

    @Test
    @DisplayName("Проверка ввода в текстовые поля")
    void textInputTest() throws InterruptedException {
        openPage(driver, WEBFORM_PAGE);

        WebElement textInput = driver.findElement(By.cssSelector("#my-text-id"));
        textInput.sendKeys("test1");
        WebElement textArea = driver.findElement(By.cssSelector("[name=my-textarea]"));
        textArea.sendKeys("test2\ntest2\ntest2");
        Thread.sleep(2000);

        assertEquals("test1", textInput.getAttribute("value"));
        assertEquals("test2\ntest2\ntest2", textArea.getAttribute("value"));
    }

    @Test
    @DisplayName("Проверка ввода в disabled поле")
    void disabledTextInputTest() throws InterruptedException {
        openPage(driver, WEBFORM_PAGE);

        WebElement disabledTextInput = driver.findElement(By.cssSelector("[name=my-disabled]"));

        assertThrows(ElementNotInteractableException.class, () -> disabledTextInput.sendKeys("test"));
        assertEquals("Disabled input", disabledTextInput.getAttribute("placeholder"));
    }

    @Test
    @DisplayName("Проверка изменения disabled поля на enabled")
    public void disabledToEnabledTest() throws InterruptedException {
        openPage(driver, WEBFORM_PAGE);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("document.querySelector(\"input[placeholder='Disabled input']\").disabled = false");

        WebElement disabledTextInput = driver.findElement(By.cssSelector("[name=my-disabled]"));
        disabledTextInput.sendKeys("test disabled");

        assertEquals("test disabled", disabledTextInput.getAttribute("value"));
        }

    @ParameterizedTest
    @DisplayName("Проверка списка")
    @CsvSource({"0, Open this select menu", "1, One","2, Two", "3,Three"})
    void selectFromListTests(int index, String value) throws InterruptedException {
        openPage(driver, WEBFORM_PAGE);

        WebElement dropdownSelectMenu = driver.findElement(By.name("my-select"));
        Select select = new Select(dropdownSelectMenu);
        select.selectByIndex(index);
        Thread.sleep(2000);

        assertEquals(value, select.getFirstSelectedOption().getText());
    }

    @Test
    @DisplayName("Проверка загрузки файла")
    void fileUploadTest() throws InterruptedException {
        openPage(driver, WEBFORM_PAGE);
        String fileName = "article.pdf";
        String absolutePath = new File(fileName).getAbsolutePath();

        WebElement fileUpload = driver.findElement(By.name("my-file"));
        fileUpload.sendKeys(absolutePath);
        Thread.sleep(5000);
        WebElement submit = driver.findElement(By.xpath("//button[text()='Submit']"));
        submit.click();
        Thread.sleep(5000);
        assertThat(driver.getCurrentUrl()).contains(fileName);
    }

    @Test
    @DisplayName("Проверка чекбоксов")
    void checkBoxTest() throws InterruptedException {
        openPage(driver, WEBFORM_PAGE);

        WebElement checkbox1 = driver.findElement(By.id("my-check-1"));
        WebElement checkbox2 = driver.findElement(By.id("my-check-2"));

        assertTrue(checkbox1.isSelected());
        assertFalse(checkbox2.isSelected());

        new Actions(driver)
                .click(checkbox1)
                .perform();
        Thread.sleep(2000);

        assertFalse(checkbox1.isSelected());
        assertFalse(checkbox2.isSelected());

        checkbox2.sendKeys(Keys.SPACE);
        Thread.sleep(2000);

        assertFalse(checkbox1.isSelected());
        assertTrue(checkbox2.isSelected());
    }

    @ParameterizedTest
    @DisplayName("Проверка элемента выбора даты'")
    @ValueSource(strings = {"1", "28"})
    void datePickerTest(String day) throws InterruptedException {
        openPage(driver, WEBFORM_PAGE);

        WebElement datePicker = driver.findElement(By.name("my-date"));
        datePicker.click();
        Thread.sleep(2000);
        WebElement dayPicker = driver.findElement(By.xpath("//td[@class='day' and contains(text(),'" + day + "')]"));
        new Actions(driver)
                .click(dayPicker)
                .perform();
        Thread.sleep(2000);

        Calendar cal= Calendar.getInstance();
        assertEquals(formatDateField(String.valueOf(cal.get(Calendar.MONTH) + 1)) + "/" + formatDateField(day) + "/" + cal.get(Calendar.YEAR), datePicker.getAttribute("value"));
    }

    @ParameterizedTest
    @DisplayName("Проверка ссылок")
    @CsvSource({"1,Lorem ipsum","2,Ut enim","3,Excepteur"})
    void pageLinkTest(int pageNumber, String startText) throws InterruptedException {
        openPage(driver, NAVIGATION_PAGE);

        WebElement link = driver.findElement(By.xpath("//a[@class='page-link' and text()='" + pageNumber + "']"));
        link.click();

        assertTrue(driver.findElement(By.cssSelector(".lead")).getText().startsWith(startText));
    }

    @Test
    @DisplayName("Проверка навигации")
    void navigationTest() throws InterruptedException {
        openPage(driver, NAVIGATION_PAGE);
        List<WebElement> pagination = driver.findElements(By.xpath("//ul[@class='pagination']/li"));

        assertTrue(checkState(driver,5,1,0,false));

        clickNext(driver);
        assertTrue(checkState(driver,5,2,0,true));

        clickNext(driver);
        assertTrue(checkState(driver,5,3,4,false));

        clickPrevious(driver);
        assertTrue(checkState(driver,5,2,4,true));

        clickPrevious(driver);
        assertTrue(checkState(driver,5,1,0,false));
    }

    @ParameterizedTest
    @DisplayName("Выпадающие списки")
    @MethodSource("dropDownArgs")
    void dropDownTest(String ddMenu, String ddList, Function<WebElement, Actions> action) throws InterruptedException {
        openPage(driver, DROPDOWN_PAGE);

        List<WebElement> elements = getDropDownElements(driver, ddMenu, ddList, action);

        assertEquals(4, elements.size());
        assertEquals("Action", elements.get(0).getAttribute("innerText"));
        assertEquals("Another action", elements.get(1).getAttribute("innerText"));
        assertEquals("Something else here", elements.get(2).getAttribute("innerText"));
        assertEquals("Separated link", elements.get(3).getAttribute("innerText"));
    }

    static Stream<Arguments> dropDownArgs() {
        Function<WebElement, Actions> click = e -> new Actions(driver).click(e);
        Function<WebElement, Actions> contextClick = e -> new Actions(driver).contextClick(e);
        Function<WebElement, Actions> doubleClick = e -> new Actions(driver).doubleClick(e);

        return Stream.of(
                Arguments.of("my-dropdown-1", "context-menu-1", click),
                Arguments.of("my-dropdown-2", "context-menu-2", contextClick),
                Arguments.of("my-dropdown-3", "context-menu-3", doubleClick));
    }

    @Test
    @DisplayName("drag & drop")
    void dragDropTest() throws InterruptedException {
        openPage(driver, DRAGDROP_PAGE);

        WebElement draggable = driver.findElement(By.id("draggable"));
        WebElement droppable = driver.findElement(By.id("target"));
        new Actions(driver)
                .dragAndDrop(draggable, droppable)
                .perform();
        Thread.sleep(2000);

        assertEquals(droppable.getRect(), draggable.getRect());
    }
}
