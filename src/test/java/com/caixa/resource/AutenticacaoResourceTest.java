package com.caixa.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
class AutenticacaoResourceTest {

    @Test
    void login_valido_deveRetornarToken() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "username": "admin",
                          "password": "admin123"
                        }
                        """)
                .when()
                .post("/autenticacao/login")
                .then()
                .statusCode(200)
                .body("accessToken", notNullValue())
                .body("tokenType", is("Bearer"));
    }

    @Test
    void login_invalido_deveRetornar401() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "username": "admin",
                          "password": "senhaErrada"
                        }
                        """)
                .when()
                .post("/autenticacao/login")
                .then()
                .statusCode(401);
    }
}
