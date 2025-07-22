package ru.praktikum_services.qa_scooter.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.praktikum_services.qa_scooter.tests.Order;
import java.util.Map;

public class OrderClient {

    public Response createOrder(Order order) {
        return RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(order)
                .post("/api/v1/orders");
    }

    public void cancelOrder(int track) {
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(Map.of("track", track))
                .put("/api/v1/orders/cancel");
    }
}