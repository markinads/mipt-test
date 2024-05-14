import controller.ConverterController;
import io.qameta.allure.Epic;
import io.qameta.allure.Story;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("API Tests")
@Story("Онлайн-конвертер форматов https://www.pdf2go.com/")
@Tag("additionalApi")
public class FileConverterTest {
    /*    Интеграционный тест для работы с файлами на примере онлайн-конвертера форматов
          Загрузка, конвертация PDF -> TXT и скачивание файла
          */
    private static final String SOURCE_FILE_NAME = "article.pdf";
    private static final String RESULT_FILE_NAME = "article.txt";

    ConverterController controller = new ConverterController();

    @SneakyThrows
    @Test
    @DisplayName("Интеграционный тест онлайн-конвертера файлов")
    void endToEndTest() {
        controller.startMainJob();
        controller.uploadFile(SOURCE_FILE_NAME);
        controller.convertFile();
        controller.downloadFile(RESULT_FILE_NAME);

        File resultFile = new File(RESULT_FILE_NAME);
        assertTrue(resultFile.exists());
        String text = Files.readString(Paths.get(RESULT_FILE_NAME));
        assertTrue(text.contains("ОКВЭД: нужно ли менять старые коды?"));
    }

    @AfterAll
    static void clearTestResources() {
        File resultFile = new File(RESULT_FILE_NAME);
        resultFile.delete();
    }
}
