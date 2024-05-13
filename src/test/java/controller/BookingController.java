package controller;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.specification.RequestSpecification;
import model.Booking;
import org.apache.commons.codec.binary.Base64;

import static io.restassured.RestAssured.given;

public class BookingController {
    private static final String BOOKING_URL = "https://restful-booker.herokuapp.com/booking";
    private static final String TOKEN_URL = "https://restful-booker.herokuapp.com/auth";
    private static final String TEST_USERNAME = "admin";
    private static final String TEST_PASSWORD = "password123";

    static RequestSpecification requestSpecification = given();

    public BookingController() {
        RestAssured.defaultParser = Parser.JSON;
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept("*/*");

        // Для PUT/PATCH требуется авторизация - либо с указанием токена в cookie, либо Basic Base64(username:password) в заголовке
        //requestSpecification.cookie("token", createToken());
        String auth = TEST_USERNAME + ":" + TEST_PASSWORD;
        requestSpecification.header("Authorization", "Basic " + Base64.encodeBase64String(auth.getBytes()));
    }

    @Step("Создание токена для пользователя по умолчанию")
    public static String createToken() {
        String authJson = "{ \"username\": \"" + TEST_USERNAME + "\", \"password\": \"" + TEST_PASSWORD + "\" }";
        requestSpecification.body(authJson);
        return given(requestSpecification).
                log().all().
                post(TOKEN_URL).
                then().
                log().all().
                assertThat().
                statusCode(200).
                extract().
                response().
                body().
                jsonPath().
                get("token");
    }

    @Step("Получение бронирования")
    public Booking getBooking(int bookingId) {
        return given(requestSpecification).
                log().all().
                get(BOOKING_URL + "/" + bookingId).
                then().
                log().all().
                assertThat().
                statusCode(200).
                extract().
                response().
                body().
                as(Booking.class);
    }

    @Step("Создание бронирования")
    public int postBooking(Booking booking) {
        requestSpecification.body(booking);
        return given(requestSpecification).
                log().all().
                post(BOOKING_URL).
                then().
                assertThat().
                statusCode(200).
                log().all().
                extract().
                response().
                body().
                jsonPath().
                get("bookingid");
    }

    @Step("Изменение бронирования")
    public int putBooking(int bookingId, Booking booking) {
        requestSpecification.body(booking);
        return given(requestSpecification).
                log().all().
                put(BOOKING_URL + "/" + bookingId).
                then().
                log().all().
                extract().
                statusCode();
    }

    @Step("Частичное изменение бронирования")
    public int patchBooking(int bookingId, Booking booking) {
        requestSpecification.body(booking);
        return given(requestSpecification).
                log().all().
                patch(BOOKING_URL + "/" + bookingId).
                then().
                log().all().
                extract().
                statusCode();
    }
}
