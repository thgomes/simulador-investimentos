package com.caixa.repository;

import com.caixa.entity.Produto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class ProdutoRepository {

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public List<Produto> buscarPorTipoProduto(String tipoProduto) {
        return entityManager
                .createQuery("SELECT p FROM Produto p WHERE p.tipoProduto = :tipoProduto", Produto.class)
                .setParameter("tipoProduto", tipoProduto)
                .getResultList();
    }
}
