import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.ElementNotInteractableException;
import pages.MainPage;
import pages.WebFormPage;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class WebFormPageTests {
    static MainPage mainPage;
    static WebFormPage webFormPage;

    @BeforeAll
    static void setup() {
        mainPage = new MainPage("chrome");
        webFormPage = mainPage.openWebFormPage();
    }

    @AfterAll
    static void teardown() {
        webFormPage.quit();
    }

    @Test
    @DisplayName("Проверка ввода в текстовые поля")
    void testSuccessTextInput() {
        webFormPage.setInputTextText("test1");
        assertThat(webFormPage.getInputTextText()).isEqualTo("test1");
        webFormPage.setTextAreaText("test2\ntest2\ntest2");
        assertThat(webFormPage.getTextAreaText()).isEqualTo("test2\ntest2\ntest2");
    }

    @Test
    @DisplayName("Проверка ввода в disabled поле")
    void testDisabledTextInput() {
        assertThrows(ElementNotInteractableException.class, () -> webFormPage.setDisabledText("test"));
        assertThat(webFormPage.getDisabledText()).isEqualTo("");
    }

    @Test
    @DisplayName("Проверка изменения disabled поля на enabled")
    void testDisabledToEnabled() {
        webFormPage.setDisabledToEnabled();
        webFormPage.setDisabledText("test");
        assertThat(webFormPage.getDisabledText()).isEqualTo("test");
        // восстановление исходного состояния
        webFormPage.refresh();
    }

    @ParameterizedTest
    @DisplayName("Проверка списка")
    @CsvSource({"0, Open this select menu", "1, One","2, Two", "3,Three"})
    void testSelect(int index, String value) {
        webFormPage.selectByIndex(index);
        assertEquals(value, webFormPage.getSelectText());
    }

    @Test
    @DisplayName("Проверка загрузки файла")
    void fileUploadTest() {
        String fileName = "article.pdf";
        String absolutePath = new File(fileName).getAbsolutePath();
        webFormPage.uploadFile(absolutePath);
        assertThat(webFormPage.urlContains(fileName)).isTrue();
        assertThat(webFormPage.isSubmitted()).isTrue();
        // восстановление исходного состояния
        webFormPage.back();
        webFormPage.refresh();
    }

    @Test
    @DisplayName("Проверка чекбоксов")
    void checkBoxTest() {
        assertThat(webFormPage.isSelectedCheck1()).isTrue();
        assertThat(webFormPage.isSelectedCheck2()).isFalse();

        webFormPage.changeCheck1State("mouse");
        webFormPage.changeCheck2State("keyboard");

        assertThat(webFormPage.isSelectedCheck1()).isFalse();
        assertThat(webFormPage.isSelectedCheck2()).isTrue();

        webFormPage.changeCheck1State("XXX");
        assertThat(webFormPage.isSelectedCheck1()).isFalse();
    }

}
