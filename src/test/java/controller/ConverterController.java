package controller;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;
import lombok.Setter;
import model.Job;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static io.restassured.RestAssured.given;

public class ConverterController {
    private static final String CONVERTER_JOB_BASE_URL = "https://dragon.pdf2go.com/api/jobs";
    private static final String ASYNC_PARAMETER_STRING = "?async=true";
    private static final String UPLOAD_STRING = "/upload-file/";
    private static final String CONVERSIONS_STRING = "/conversions";
    private static final String MAIN_JOB_BODY = "{\"operation\":\"convertPdfToText\",\"fail_on_conversion_error\":false,\"fail_on_input_error\":false}";
    private static final String CONVERSION_JOB_BODY = "{\"category\":\"document\",\"options\":{\"allow_multiple_outputs\":true,\"ocr\":false,\"language\":\"rus\"},\"target\":\"txt\"}";

    RequestSpecification requestSpecification = given();
    @Getter
    @Setter
    private Job mainJob;
    private Job conversionJob;

    public ConverterController() {
        RestAssured.defaultParser = Parser.JSON;
        requestSpecification.accept(ContentType.JSON);
        mainJob = new Job();
        conversionJob = new Job();
    }

    @Step("Создание основного задания")
    public void startMainJob() {
        Response response = given(requestSpecification).
                when().
                contentType("application/json").
                body(MAIN_JOB_BODY).
                log().all().
                post(CONVERTER_JOB_BASE_URL + ASYNC_PARAMETER_STRING).
                then().
                assertThat().
                statusCode(200).
                log().all().
                extract().
                response();
        mainJob.setId(response.jsonPath().getString("sat.id_job"));
    }

    @Step("Получение параметров задания конвертации")
    public Response getConversionJob() {
        Response response = given(requestSpecification).
                log().all().
                get(CONVERTER_JOB_BASE_URL + "/" + mainJob.getId() + ASYNC_PARAMETER_STRING).
                then().
                assertThat().
                statusCode(200).
                log().all().
                extract().
                response();
        conversionJob.setId(response.jsonPath().getString("id"));
        conversionJob.setToken(response.jsonPath().getString("token"));
        conversionJob.setServer(response.jsonPath().getString("server"));
        return response;
    }

    @Step("Загрузка файла")
    public void uploadFile(String fileName) {
        File srcFile = new File(fileName);
        getConversionJob();
        given(requestSpecification).
                when().
                multiPart("file", srcFile, "application/pdf").
                contentType("multipart/form-data").
                accept("application/json").
                header("X-Oc-Token", conversionJob.getToken()).
                log().all().
                post(conversionJob.getServer() + UPLOAD_STRING + conversionJob.getId()).
                then().
                assertThat().
                statusCode(200).
                log().all().
                extract().
                response();
    }

    @Step("Конвертация файла")
    public void convertFile() {
        getConversionJob();
        given(requestSpecification).
                when().
                contentType("application/json").
                accept("application/json").
                header("X-Oc-Token", conversionJob.getToken()).
                body(CONVERSION_JOB_BODY).
                log().all().
                post(CONVERTER_JOB_BASE_URL + "/" + conversionJob.getId() + CONVERSIONS_STRING).
                then().
                assertThat().
                statusCode(200).
                log().all().
                extract().
                response();
    }

    @Step("Скачивание файла")
    public void downloadFile(String fileName) throws InterruptedException {
        // пауза для завершения конвертации
        Thread.sleep(3000);
        Response completedJobResponse = getConversionJob();
        Response resultFile = given(requestSpecification).
                log().all().
                get(completedJobResponse.jsonPath().getString("output[0].download_uri")).
                then().
                assertThat().
                statusCode(200).
                log().all().
                extract().
                response();
        if (resultFile != null) {
            // Получение содержимого ответа в виде InputStream
            try (InputStream inputStream = resultFile.getBody().asInputStream()) {
                // Создание файла для сохранения полученного текстового файла
                OutputStream outputStream = new FileOutputStream(fileName);

                // Копирование содержимого InputStream в файл
                int bytesRead;
                byte[] buffer = new byte[4096];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                // Закрытие потоков
                inputStream.close();
                outputStream.close();

                System.out.printf("Файл %s успешно загружен\n", fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
