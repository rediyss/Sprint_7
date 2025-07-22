package ru.praktikum_services.qa_scooter.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class CourierCreateTest extends BaseTest{

    protected RequestSpecification requestSpec;
    private String courierId;
    private CreateUser lastCreatedCourier;

    @After
    public void tearDown() {
        if (courierId == null && lastCreatedCourier != null) {
            courierId = getCourierId(lastCreatedCourier.getLogin(), lastCreatedCourier.getPassword());
        }
        if (courierId != null) {
            deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Можно создать курьера")
    @Description("Проверка успешного создания нового курьера с валидными логином, паролем и именем")
    public void canCreateCourierTest() {
        CreateUser courier = new CreateUser("Artas12345991", "1234", "Rediys");
        lastCreatedCourier = courier;

        Response createResponse = createCourier(courier);
        createResponse.then().statusCode(201).body("ok", is(true));
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    @Description("Проверка невозможности создания двух курьеров с одинаковыми данными. Ожидается 409 Conflict.")
    public void cannotCreateDuplicateCourierTest() {
        CreateUser courier = new CreateUser("Artas12345991", "1234", "Rediyss");
        lastCreatedCourier = courier;

        Response first = createCourier(courier);
        first.then().statusCode(anyOf(is(201), is(409)));

        Response second = createCourier(courier);
        second.then().statusCode(409).body("message", containsString("Этот логин уже используется"));
    }

    @Test
    @DisplayName("Создание курьера без логина")
    @Description("Проверка ошибки при попытке создать курьера без логина. Ожидается код 400 и сообщение об ошибке.")
    public void cannotCreateCourierWithoutLoginTest() {
        CreateUser courier = new CreateUser();
        courier.setPassword("1234");
        courier.setFirstName("Rediyss");

        Response response = createCourier(courier);
        response.then().statusCode(400).body("message", not(empty()));
    }

    @Test
    @DisplayName("Создание курьера без пароля")
    @Description("Проверка ошибки при попытке создать курьера без пароля. Ожидается код 400 и сообщение об ошибке.")
    public void cannotCreateCourierWithoutPasswordTest() {
        CreateUser courier = new CreateUser();
        courier.setLogin("noPassLogin123");
        courier.setFirstName("Rediyss");

        Response response = createCourier(courier);
        response.then().statusCode(400).body("message", not(empty()));
    }

    @Test
    @DisplayName("Создание курьера без firstName")
    @Description("Проверка возможности создания курьера без поля firstName. Ожидается успешный ответ 201.")
    public void canCreateCourierWithoutFirstNameTest() {
        CreateUser courier = new CreateUser("noFirstNameLogin123", "1234", null);
        lastCreatedCourier = courier;

        Response response = createCourier(courier);
        response.then().statusCode(201).body("ok", is(true));
    }

    @Step("Создание курьера")
    private Response createCourier(CreateUser courier) {
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
                .body(new CreateUser(login, password, null))
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
                .delete("/api/v1/courier/" + id);
    }
}
