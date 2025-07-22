package ru.praktikum_services.qa_scooter.tests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class OrderListTest extends BaseTest{

    @Test
    @DisplayName("Список заказов не пустой")
    @Description("Проверка, что при GET-запросе к /api/v1/orders возвращается список заказов и он не пустой")
    public void orderListIsNotEmptyTest() {
        given()
                .get("/api/v1/orders")
                .then()
                .statusCode(200)
                .body("orders", not(empty()));
    }
}
