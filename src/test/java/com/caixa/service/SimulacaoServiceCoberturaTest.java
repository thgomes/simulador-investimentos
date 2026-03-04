package com.caixa.service;

import com.caixa.dto.simulacao.AgregadoSimulacoesResponse;
import com.caixa.entity.Produto;
import com.caixa.entity.Simulacao;
import com.caixa.exception.ProdutoNaoElegivelException;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
class SimulacaoServiceCoberturaTest {

    @Inject
    SimulacaoService simulacaoService;

    @Inject
    EntityManager entityManager;

    @Test
    @Transactional
    void obterAgregado_semSimulacoes_deveRetornarTotaisZerados() {
        entityManager.createQuery("DELETE FROM Simulacao").executeUpdate();

        AgregadoSimulacoesResponse agregado = simulacaoService.obterAgregado();

        assertEquals(0, agregado.quantidadeSimulacoes());
        assertEquals(0, agregado.quantidadeClientes());
        assertEquals(0, BigDecimal.ZERO.compareTo(agregado.totalInvestido()));
        assertEquals(0, BigDecimal.ZERO.compareTo(agregado.totalValorFinal()));
        assertEquals(0, BigDecimal.ZERO.compareTo(agregado.rentabilidadeTotal()));
        assertEquals(0, BigDecimal.ZERO.compareTo(agregado.ticketMedio()));
        assertEquals(0, BigDecimal.ZERO.compareTo(agregado.prazoMedioMeses()));
    }

    @Test
    void buscarProdutoElegivel_nosLimitesMinimos_deveRetornarProduto() {
        Produto produto = simulacaoService.buscarProdutoElegivel("CDB", new BigDecimal("1000"), 6);
        assertNotNull(produto);
        assertEquals("CDB Caixa 2026", produto.nome);
    }

    @Test
    void buscarProdutoElegivel_abaixoDoMinimo_deveLancarExcecao() {
        assertThrows(ProdutoNaoElegivelException.class,
                () -> simulacaoService.buscarProdutoElegivel("CDB", new BigDecimal("999.99"), 6));
    }

    @Test
    void buscarProdutoElegivel_prazoAbaixoDoMinimo_deveLancarExcecao() {
        assertThrows(ProdutoNaoElegivelException.class,
                () -> simulacaoService.buscarProdutoElegivel("CDB", new BigDecimal("1000.00"), 5));
    }

    @Test
    @Transactional
    void obterAgregado_comValoresNulos_deveIgnorarValoresParaTotais() {
        entityManager.createQuery("DELETE FROM Simulacao").executeUpdate();

        Simulacao simulacao = new Simulacao();
        simulacao.clienteId = 10L;
        simulacao.produtoNome = "CDB Caixa 2026";
        simulacao.tipoProduto = "CDB";
        simulacao.valorInvestido = null;
        simulacao.valorFinal = null;
        simulacao.prazoMeses = null;
        simulacao.dataSimulacao = java.time.LocalDateTime.now();
        entityManager.persist(simulacao);

        AgregadoSimulacoesResponse agregado = simulacaoService.obterAgregado();

        assertEquals(1, agregado.quantidadeSimulacoes());
        assertEquals(1, agregado.quantidadeClientes());
        assertEquals(0, BigDecimal.ZERO.compareTo(agregado.totalInvestido()));
        assertEquals(0, BigDecimal.ZERO.compareTo(agregado.totalValorFinal()));
        assertEquals(0, BigDecimal.ZERO.compareTo(agregado.rentabilidadeTotal()));
        assertEquals(0, BigDecimal.ZERO.setScale(2).compareTo(agregado.ticketMedio()));
        assertEquals(0, BigDecimal.ZERO.setScale(2).compareTo(agregado.prazoMedioMeses()));
    }
}
