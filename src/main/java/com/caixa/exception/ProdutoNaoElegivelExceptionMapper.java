package com.caixa.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ProdutoNaoElegivelExceptionMapper implements ExceptionMapper<ProdutoNaoElegivelException> {

    @Override
    public Response toResponse(ProdutoNaoElegivelException exception) {
        return Response.status(422)
                .entity(new ErroResponse(exception.getMessage()))
                .build();
    }

    public record ErroResponse(String mensagem) {}
}
