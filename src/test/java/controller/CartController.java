package controller;

import io.qameta.allure.Step;
import io.restassured.http.Cookies;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;
import model.Item;
import model.Items;
import model.Product;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;

import static io.restassured.RestAssured.given;

public class CartController {
    private static final String STORE_URL = "https://treeseed.ru/";
    private static final String GET_CART_URL = STORE_URL + "cart/getCart";
    private static final String ADD_TO_CART_URL = STORE_URL + "cart/addToCart";
    private static final String UPDATE_CART_URL = STORE_URL + "cart/updateCart";
    private static final String REMOVE_FROM_CART_URL = STORE_URL + "cart/removeFromCart";

    public static final String COOKIE_CUSTOMER_NAME = "customer";
    public static final String COOKIE_TOKEN_NAME = "f";
    public static final String HEADER_TOKEN_NAME = "__RequestVerificationToken";
    public static final String HEADER_TOKEN_PATH = "html.body.input.@name==\"" + HEADER_TOKEN_NAME + "\".@value";

    RequestSpecification requestSpecification = given();
    @Getter
    private String headerToken;
    @Getter
    private String cookieToken;
    @Getter
    private String cookieCustomer;

    public CartController() {
        init();
        RestAssured.defaultParser = Parser.JSON;
        this.requestSpecification.header(HEADER_TOKEN_NAME, headerToken);
        this.requestSpecification.cookie(COOKIE_TOKEN_NAME, cookieToken);
        this.requestSpecification.cookie(COOKIE_CUSTOMER_NAME, cookieCustomer);
        this.requestSpecification.contentType(ContentType.JSON);
        this.requestSpecification.accept(ContentType.JSON);
    }

    public void init() {
        Response response = given(this.requestSpecification).
                post(STORE_URL).
                then().
                assertThat().
                statusCode(200).
                extract().
                response();
        Cookies cookies = response.getDetailedCookies();
        cookieCustomer = (cookies.asList().stream().
                filter(c -> c.getName().contains(COOKIE_CUSTOMER_NAME))).
                findFirst().map(h -> h.getValue()).
                orElse(null);
        cookieToken = (cookies.asList().stream().
                filter(c -> c.getName().contains(COOKIE_TOKEN_NAME))).
                findFirst().map(h -> h.getValue()).
                orElse(null);
        headerToken = response.body().htmlPath().getString(HEADER_TOKEN_PATH);
    }

    @Step("Добавление товара в корзину")
    public Response addToCart(Product product) {
        this.requestSpecification.body(product);
        return given(this.requestSpecification).
                log().all().
                post(ADD_TO_CART_URL).
                then().
                assertThat().
                statusCode(200).
                log().body().
                extract().response();
    }

    @Step("Получение текущего содержимого корзины")
    public Response getCart() {
        this.requestSpecification.body("");
        return given(this.requestSpecification).
                log().all().
                post(GET_CART_URL).
                then().
                assertThat().
                statusCode(200).
                log().body().
                extract().response();
    }

    @Step("Изменение содержимого корзины")
    public Response updateCart(Items items) {
        this.requestSpecification.body(items);
        return given(this.requestSpecification).
                log().all().
                post(UPDATE_CART_URL).
                then().
                assertThat().
                statusCode(200).
                log().body().
                extract().response();
    }

    @Step("Удаление товара из корзины")
    public Response removeFromCart(Item item) {
        this.requestSpecification.body(item);
        return given(this.requestSpecification)
                .post(REMOVE_FROM_CART_URL)
                .then()
                .statusCode(200)
                .log().body()
                .extract().response();
    }
}