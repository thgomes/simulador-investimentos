package com.caixa.dto.simulacao;

import java.math.BigDecimal;

public record AgregadoSimulacoesResponse(
    int quantidadeSimulacoes,
    int quantidadeClientes,
    BigDecimal totalInvestido,
    BigDecimal totalValorFinal,
    BigDecimal rentabilidadeTotal,
    BigDecimal ticketMedio,
    BigDecimal prazoMedioMeses
) {}
