package steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import patterns.WebDriverFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.time.Instant;

import static io.restassured.RestAssured.given;

public class AllureSteps {
    static RequestSpecification requestSpecification = given();

    @Step("Capture screenshot (extension)")
    public void captureScreenshotSpoiler() {
        Allure.addAttachment("Screenshot " + Instant.now(), new ByteArrayInputStream(((TakesScreenshot) WebDriverFactory.getDriver()).getScreenshotAs(OutputType.BYTES)));
    }

    @Step("Download file: {destination}")
    public static void download(String link, File destination) {
            Response response = given(requestSpecification).get(link).then().extract().response();
            InputStream inputStream = response.getBody().asInputStream();
            Allure.addAttachment(destination.getName(), inputStream);
    }
}
