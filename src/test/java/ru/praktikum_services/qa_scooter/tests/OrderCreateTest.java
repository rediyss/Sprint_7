package ru.praktikum_services.qa_scooter.tests;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class OrderCreateTest {

    private final String[] colors;
    private final String testCaseDescription;

    public OrderCreateTest(String testCaseDescription, String[] colors) {
        this.testCaseDescription = testCaseDescription;
        this.colors = colors;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                {"Создание заказа с одним цветом BLACK", new String[]{"BLACK"}},
                {"Создание заказа с одним цветом GREY", new String[]{"GREY"}},
                {"Создание заказа с двумя цветами (BLACK и GREY)", new String[]{"BLACK", "GREY"}},
                {"Создание заказа без указания цвета", new String[]{}}
        });
    }

    @Before
    public void setup() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    @DisplayName("Создание заказа с параметрами цвета")
    public void createOrderWithColors() {
        Map<String, Object> order = new HashMap<>();
        order.put("firstName", "Kek");
        order.put("lastName", "Lol");
        order.put("address", "Magadan, 142 apt.");
        order.put("metroStation", 4);
        order.put("phone", "+7 800 589 33 96");
        order.put("rentTime", 5);
        order.put("deliveryDate", "2025-06-09");
        order.put("comment", "bebeb");
        order.put("color", colors);
        Response response = given()
                .contentType(ContentType.JSON)
                .body(order)
                .post("/api/v1/orders");

        response.then().
                statusCode(201)
                .body("track", notNullValue());
    }
}