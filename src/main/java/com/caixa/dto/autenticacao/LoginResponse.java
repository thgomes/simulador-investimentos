package com.caixa.dto.autenticacao;

public record LoginResponse(
    String accessToken,
    String tokenType
) {
}
