package com.caixa.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionMappersTest {

    @Test
    void badRequestExceptionMapper_deveRetornarStatus400EMensagem() {
        BadRequestExceptionMapper mapper = new BadRequestExceptionMapper();
        BadRequestException exception = new BadRequestException("clienteId é obrigatório");

        Response response = mapper.toResponse(exception);

        assertEquals(400, response.getStatus());
        BadRequestExceptionMapper.ErroResponse body = (BadRequestExceptionMapper.ErroResponse) response.getEntity();
        assertEquals("clienteId é obrigatório", body.mensagem());
    }

    @Test
    void produtoNaoElegivelExceptionMapper_deveRetornarStatus422EMensagem() {
        ProdutoNaoElegivelExceptionMapper mapper = new ProdutoNaoElegivelExceptionMapper();
        ProdutoNaoElegivelException exception = new ProdutoNaoElegivelException("Nenhum produto elegível");

        Response response = mapper.toResponse(exception);

        assertEquals(422, response.getStatus());
        ProdutoNaoElegivelExceptionMapper.ErroResponse body =
                (ProdutoNaoElegivelExceptionMapper.ErroResponse) response.getEntity();
        assertEquals("Nenhum produto elegível", body.mensagem());
    }

    @Test
    void constraintViolationExceptionMapper_deveRetornarStatus400ComMensagens() {
        ConstraintViolationExceptionMapper mapper = new ConstraintViolationExceptionMapper();
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        Set<ConstraintViolation<RequisicaoInvalida>> violations = validator.validate(new RequisicaoInvalida(null));
        ConstraintViolationException exception = new ConstraintViolationException(violations);

        Response response = mapper.toResponse(exception);

        assertEquals(400, response.getStatus());
        ConstraintViolationExceptionMapper.ErroResponse body =
                (ConstraintViolationExceptionMapper.ErroResponse) response.getEntity();
        assertEquals("campo é obrigatório", body.mensagem());
    }

    private record RequisicaoInvalida(
            @NotNull(message = "campo é obrigatório")
            String campo
    ) {
    }
}
