package com.caixa.dto.simulacao;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public record SimulacaoInvestimentoResponse(
    Long simulacaoId,
    ProdutoValidado produtoValidado,
    ResultadoSimulacao resultadoSimulacao,
    LocalDateTime dataSimulacao
) {

    public record ProdutoValidado(
        Long id,
        String nome,
        String tipo,
        BigDecimal rentabilidade,
        String risco
    ) {}

    public record ResultadoSimulacao(
        BigDecimal valorFinal,
        Integer prazoMeses
    ) {}
}
