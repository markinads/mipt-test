import extension.AllureExtension;
import io.qameta.allure.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pages.DownloadPage;
import pages.MainPage;

import java.io.File;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Selenium")
@Feature("Bonigarcia")
@Story("Download")
public class DownloadPageTests {
    static MainPage mainPage;
    static DownloadPage downloadPage;

    @BeforeAll
    static void setup() {
        mainPage = new MainPage("chrome");
        downloadPage = mainPage.openDownloadPage();
    }

    @AfterAll
    static void teardown() {
        downloadPage.quit();
    }

    @DisplayName("Проверка скачивания файла (с прикреплением файла в Allure Report)")
    @ParameterizedTest
    @MethodSource("downloadArgs")
    @Severity(SeverityLevel.CRITICAL)
    @ExtendWith(AllureExtension.class)
    void testDownloadFile(String filename, Consumer<File> method) {
        File file = new File(".", filename);
        method.accept(file);
        assertThat(file).exists();
    }

    static Stream<Arguments> downloadArgs() {
            Consumer<File> downloadWdmImage = e -> downloadPage.downloadWdmImage(e);
            Consumer<File> downloadWdmDoc = e -> downloadPage.downloadWdmDoc(e);
            Consumer<File> downloadSjImage = e -> downloadPage.downloadSjImage(e);
            Consumer<File> downloadSjDoc = e -> downloadPage.downloadSjDoc(e);

        return Stream.of(
                Arguments.of("webdrivermanager.png", downloadWdmImage),
                Arguments.of("webdrivermanager.pdf", downloadWdmDoc),
                Arguments.of("selenium-jupiter.png", downloadSjImage),
                Arguments.of("selenium-jupiter.pdf", downloadSjDoc));
    }
}
