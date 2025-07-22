package ru.praktikum_services.qa_scooter.tests;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.BeforeClass;

public class BaseTest {
    protected static RequestSpecification requestSpec;

    @BeforeClass
    public static void setUpClass() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(RestAssured.baseURI)
                .setContentType(ContentType.JSON)
                .build();
    }
}
