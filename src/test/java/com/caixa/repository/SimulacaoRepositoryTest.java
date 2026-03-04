package com.caixa.repository;

import com.caixa.entity.Simulacao;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class SimulacaoRepositoryTest {

    @Inject
    SimulacaoRepository simulacaoRepository;

    @Inject
    EntityManager entityManager;

    @Test
    @Transactional
    void salvar_semId_deveGerarIdAutomaticamente() {
        entityManager.createQuery("DELETE FROM Simulacao").executeUpdate();

        Simulacao simulacao = new Simulacao();
        simulacao.clienteId = 1L;
        simulacao.produtoNome = "CDB Caixa 2026";
        simulacao.tipoProduto = "CDB";
        simulacao.valorInvestido = new BigDecimal("1000.00");
        simulacao.prazoMeses = 12;
        simulacao.rentabilidadeAplicada = new BigDecimal("0.12");
        simulacao.valorFinal = new BigDecimal("1126.83");
        simulacao.dataSimulacao = LocalDateTime.now();

        Simulacao salvo = simulacaoRepository.salvar(simulacao);

        assertNotNull(salvo.id);
        assertEquals(1L, salvo.id);
    }
}
