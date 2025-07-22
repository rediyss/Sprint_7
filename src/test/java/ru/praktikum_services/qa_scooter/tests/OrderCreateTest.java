package ru.praktikum_services.qa_scooter.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.*;
import static org.hamcrest.Matchers.*;


@RunWith(Parameterized.class)
public class OrderCreateTest extends BaseTest {
    private final String[] colors;
    private final String name;
    private OrderClient orderClient; // ← добавлено

    public OrderCreateTest(String name, String[] colors) {
        this.name = name;
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
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Создание заказа с параметрами цвета")
    @Description("Проверка возможности создания заказа с определенными параметрами")
    public void createOrderWithColorsTest() {
        Order order = new Order(
                "Kek",
                "Lol",
                "Magadan, 142 apt.",
                4,
                "+7 800 589 33 96",
                5,
                "2025-06-09",
                "bebeb",
                Arrays.asList(colors) // ← преобразуем массив в список
        );

        Response response = orderClient.createOrder(order);
        int track = response.then()
                .statusCode(201)
                .body("track", notNullValue())
                .extract()
                .path("track");

        orderClient.cancelOrder(track);
    }
}
