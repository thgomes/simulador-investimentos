package com.caixa.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@QuarkusTest
class SimulacaoResourceTest {

    @Test
    void postSimulacoes_comDadosValidos_deveRetornar200() {
        String token = tokenAcesso();
        String body = """
            {
                "clienteId": 123,
                "valor": 10000.00,
                "prazoMeses": 12,
                "tipoProduto": "CDB"
            }
            """;

        given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body(body)
                .when()
                .post("/simulacoes")
                .then()
                .statusCode(200)
                .body("produtoValidado.id", notNullValue())
                .body("produtoValidado.nome", is("CDB Caixa 2026"))
                .body("produtoValidado.tipo", is("CDB"))
                .body("produtoValidado.rentabilidade", is(0.12f))
                .body("resultadoSimulacao.valorFinal", notNullValue())
                .body("resultadoSimulacao.prazoMeses", is(12))
                .body("dataSimulacao", notNullValue());
    }

    @Test
    void postSimulacoes_produtoNaoElegivel_deveRetornar422() {
        String token = tokenAcesso();
        String body = """
            {
                "clienteId": 123,
                "valor": 100,
                "prazoMeses": 12,
                "tipoProduto": "CDB"
            }
            """;

        given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body(body)
                .when()
                .post("/simulacoes")
                .then()
                .statusCode(422)
                .body("mensagem", containsString("Nenhum produto elegível"));
    }

    @Test
    void postSimulacoes_tipoProdutoInexistente_deveRetornar422() {
        String token = tokenAcesso();
        String body = """
            {
                "clienteId": 123,
                "valor": 10000,
                "prazoMeses": 12,
                "tipoProduto": "TESOURO_DIRETO"
            }
            """;

        given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body(body)
                .when()
                .post("/simulacoes")
                .then()
                .statusCode(422);
    }

    @Test
    void postSimulacoes_camposInvalidos_deveRetornar400() {
        String token = tokenAcesso();
        String body = """
            {
                "clienteId": null,
                "valor": -100,
                "prazoMeses": 0,
                "tipoProduto": ""
            }
            """;

        given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body(body)
                .when()
                .post("/simulacoes")
                .then()
                .statusCode(400)
                .body("mensagem", notNullValue());
    }

    @Test
    void getSimulacoes_comClienteId_deveRetornarHistorico() {
        String token = tokenAcesso();
        String postBody = """
            {
                "clienteId": 456,
                "valor": 5000,
                "prazoMeses": 12,
                "tipoProduto": "LCI"
            }
            """;
        given().auth().oauth2(token).contentType("application/json").body(postBody).when().post("/simulacoes");

        given()
                .auth().oauth2(token)
                .queryParam("clienteId", 456)
                .when()
                .get("/simulacoes")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1))
                .body("[0].clienteId", is(456))
                .body("[0].produto", is("LCI Caixa Premium"))
                .body("[0].valorInvestido", is(5000))
                .body("[0].valorFinal", notNullValue())
                .body("[0].prazoMeses", is(12))
                .body("[0].dataSimulacao", notNullValue());
    }

    @Test
    void getSimulacoes_semClienteId_deveRetornar400() {
        String token = tokenAcesso();
        given()
                .auth().oauth2(token)
                .when()
                .get("/simulacoes")
                .then()
                .statusCode(400);
    }

    @Test
    void getSimulacoesAgregado_global_deveRetornarAgregadosRelevantes() {
        String token = tokenAcesso();
        given().auth().oauth2(token).contentType("application/json")
                .body("""
                    {"clienteId": 999, "valor": 10000, "prazoMeses": 12, "tipoProduto": "CDB"}
                    """)
                .when().post("/simulacoes");

        given().auth().oauth2(token).contentType("application/json")
                .body("""
                    {"clienteId": 123, "valor": 5000, "prazoMeses": 12, "tipoProduto": "LCI"}
                    """)
                .when().post("/simulacoes");

        given()
                .auth().oauth2(token)
                .when()
                .get("/simulacoes/agregado")
                .then()
                .statusCode(200)
                .body("quantidadeSimulacoes", greaterThanOrEqualTo(2))
                .body("quantidadeClientes", greaterThanOrEqualTo(2))
                .body("totalInvestido", notNullValue())
                .body("totalValorFinal", notNullValue())
                .body("rentabilidadeTotal", notNullValue())
                .body("ticketMedio", notNullValue())
                .body("prazoMedioMeses", notNullValue());
    }

    @Test
    void endpointsProtegidos_semToken_deveRetornar401() {
        given()
                .when()
                .get("/simulacoes/agregado")
                .then()
                .statusCode(401);
    }

    private String tokenAcesso() {
        return given()
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
                .extract()
                .path("accessToken");
    }
}
