package ru.praktikum_services.qa_scooter.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CourierLoginTest {

    private String courierId;

    @Before
    public void setup() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        // Создаем курьера перед тестами
        CreateUser courier = new CreateUser("Artas9r", "1234", "Rediys");
        Response createResponse = createCourier(courier);
        createResponse.then().statusCode(201).body("ok", is(true));
    }

    @After
    public void tearDown() {
        // Получаем ID курьера перед удалением, если он еще не был установлен
        if (courierId == null) {
            try {
                courierId = getCourierId("Artas9r", "1234");
            } catch (Exception e) {
                System.out.println("Не удалось получить ID курьера для удаления: " + e.getMessage());
                return;
            }
        }

        if (courierId != null) {
            deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Курьер может авторизоваться")
    @Description("Проверка, что курьер может авторизоваться с валидными логином и паролем")
    public void courierCanLoginTest() {
        LoginCourier loginData = new LoginCourier("1234", "Artas9r");

        Response loginResponse = loginCourier(loginData);
        loginResponse.then()
                .statusCode(200)
                .body("id", notNullValue());

        courierId = loginResponse.jsonPath().getString("id");
    }

    @Test
    @DisplayName("Авторизация только с паролем")
    @Description("Проверка, что авторизация без логина невозможна. Ожидается код 400 и сообщение об ошибке.")
    public void loginRequiresAllFieldsTest() {
        // Без логина
        LoginCourier withoutLogin = new LoginCourier("1234", null);
        loginCourier(withoutLogin)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }
        @Test
        @DisplayName("Авторизация без пароля")
        @Description("Проверка, что авторизация без пароля невозможна. Ожидается код 400 и сообщение об ошибке.")
        public void passRequiresAllFieldsTest() {
        // Без пароля
        LoginCourier withoutPassword = new LoginCourier(null, "Artas9r");
        loginCourier(withoutPassword)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Неверный логин возвращает ошибку")
    @Description("Проверка, что при попытке авторизации с неверным логином возвращается код 404 и сообщение об ошибке.")
    public void wrongLoginReturnsErrorTest() {
        // Неверный логин
        LoginCourier wrongLogin = new LoginCourier("1234", "WrongLogin");
        loginCourier(wrongLogin)
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }
        @Test
        @DisplayName("Неверный пароль возвращает ошибку")
        @Description("Проверка, что при авторизации с неверным паролем возвращается код 404 и сообщение об ошибке.")
        public void wrongPasswordReturnsErrorTest() {
        // Неверный пароль
        LoginCourier wrongPassword = new LoginCourier("wrongpass", "Artas9r");
        loginCourier(wrongPassword)
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Авторизация несуществующего пользователя возвращает ошибку")
    @Description("Проверка, что попытка авторизации несуществующего пользователя возвращает 404 и сообщение об ошибке.")
    public void nonExistentUserLoginFailsTest() {
        LoginCourier nonExistent = new LoginCourier("wrongpass", "nonexistentuser");
        loginCourier(nonExistent)
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Успешная авторизация возвращает id курьера")
    @Description("Проверка, что успешная авторизация возвращает непустой id в теле ответа")
    public void successfulLoginReturnsIdTest() {
        LoginCourier loginData = new LoginCourier("1234", "Artas9r");

        Response loginResponse = loginCourier(loginData);
        loginResponse.then()
                .statusCode(200)
                .body("id", notNullValue());

        courierId = loginResponse.jsonPath().getString("id");
    }

    @Step("Создание курьера")
    private Response createCourier(CreateUser courier) {
        return given()
                .contentType(ContentType.JSON)
                .body(courier)
                .post("/api/v1/courier");
    }

    @Step("Авторизация курьера")
    private Response loginCourier(LoginCourier loginData) {
        return given()
                .contentType(ContentType.JSON)
                .body(loginData)
                .post("/api/v1/courier/login");
    }

    @Step("Получение ID курьера")
    private String getCourierId(String login, String password) {
        Response response = given()
                .contentType(ContentType.JSON)
                .body(new LoginCourier(password, login))
                .post("/api/v1/courier/login");

        if (response.statusCode() != 200) {
            throw new RuntimeException("Не удалось авторизовать курьера");
        }
        return response.jsonPath().getString("id");
    }

    @Step("Удаление курьера")
    private void deleteCourier(String id) {
        given()
                .contentType(ContentType.JSON)
                .body("{\"id\": \"" + id + "\"}")
                .when()
                .delete("/api/v1/courier/" + id)
                .then()
                .statusCode(200);
    }
}