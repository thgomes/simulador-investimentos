package com.caixa.exception;

public class ProdutoNaoElegivelException extends RuntimeException {

    public ProdutoNaoElegivelException(String mensagem) {
        super(mensagem);
    }
}
