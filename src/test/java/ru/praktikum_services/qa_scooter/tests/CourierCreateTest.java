package ru.praktikum_services.qa_scooter.tests;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CourierCreateTest {

    protected RequestSpecification requestSpec;
    private String courierId;

    @Before
    public void setup() {
        requestSpec = new RequestSpecBuilder()
                .setBaseUri("https://qa-scooter.praktikum-services.ru")
                .build();
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Можно создать курьера")
    public void canCreateCourier() {
        Createuser courier = new Createuser("Artas12345991", "1234", "Rediys");

        Response createResponse = createCourier(courier);
        createResponse.then().statusCode(201).body("ok", is(true));

        // Получаем ID для удаления
        courierId = getCourierId(courier.getLogin(), courier.getPassword());
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    public void cannotCreateDuplicateCourier() {
        Createuser courier = new Createuser("Artas12345991", "1234", "Rediyss");

        Response first = createCourier(courier);
        first.then().statusCode(anyOf(is(201), is(409)));

        Response second = createCourier(courier);
        second.then().statusCode(409).body("message", containsString("Этот логин уже используется"));


        if (first.statusCode() == 201) {
            courierId = getCourierId(courier.getLogin(), courier.getPassword());
        }
    }

    @Test
    @DisplayName("Создание курьера без логина")
    public void cannotCreateCourierWithoutLogin() {
        Createuser courier = new Createuser();
        courier.setPassword("1234");
        courier.setFirstName("Rediyss");

        Response response = createCourier(courier);
        response.then().statusCode(400).body("message", not(empty()));
        // Не устанавливаем courierId, так как курьер не создан
    }

    @Step("Создание курьера")
    private Response createCourier(Createuser courier) {
        return given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .body(courier)
                .post("/api/v1/courier");
    }

    @Step("Получение ID курьера")
    private String getCourierId(String login, String password) {
        Response response = given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .body(new Createuser(login, password, null))
                .post("/api/v1/courier/login");

        return response.jsonPath().getString("id");
    }

    @Step("Удаление курьера")
    private void deleteCourier(String id) {
        given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .body("{\"id\": \"" + id + "\"}")
                .when()
                .delete("/api/v1/courier/" + id)
                .then()
                .statusCode(200);
    }
}