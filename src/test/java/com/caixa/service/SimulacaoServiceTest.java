package com.caixa.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimulacaoServiceTest {

    @Test
    void calcularValorFinal_deveRetornarValorCorreto() {
        BigDecimal valor = new BigDecimal("10000.00");
        BigDecimal rentabilidadeAnual = new BigDecimal("0.12");
        int prazoMeses = 12;

        BigDecimal resultado = SimulacaoService.calcularValorFinal(valor, rentabilidadeAnual, prazoMeses);

        assertEquals(0, new BigDecimal("11268.25").compareTo(resultado));
    }

    @Test
    void calcularValorFinal_prazoUmMes_retornaValorComRentabilidadeMensal() {
        BigDecimal resultado = SimulacaoService.calcularValorFinal(new BigDecimal("1000"), new BigDecimal("0.12"), 1);
        assertEquals(0, new BigDecimal("1010.00").compareTo(resultado));
    }

    @Test
    void calcularValorFinal_valorZero_retornaZero() {
        BigDecimal resultado = SimulacaoService.calcularValorFinal(BigDecimal.ZERO, new BigDecimal("0.12"), 12);
        assertEquals(0, BigDecimal.ZERO.setScale(2).compareTo(resultado));
    }
}
