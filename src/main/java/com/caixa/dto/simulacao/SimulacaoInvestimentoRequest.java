package com.caixa.dto.simulacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record SimulacaoInvestimentoRequest(
    @NotNull(message = "clienteId é obrigatório")
    @Positive(message = "clienteId deve ser positivo")
    Long clienteId,

    @NotNull(message = "valor é obrigatório")
    @Positive(message = "valor deve ser positivo")
    BigDecimal valor,

    @NotNull(message = "prazoMeses é obrigatório")
    @Positive(message = "prazoMeses deve ser positivo")
    Integer prazoMeses,

    @NotBlank(message = "tipoProduto é obrigatório")
    String tipoProduto
) {}
