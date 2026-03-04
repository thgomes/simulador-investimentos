package com.caixa.dto.simulacao;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public record SimulacaoInvestimento(
    Long id,
    Long clienteId,
    String produto,
    BigDecimal valorInvestido,
    BigDecimal valorFinal,
    Integer prazoMeses,
    LocalDateTime dataSimulacao
) {}
