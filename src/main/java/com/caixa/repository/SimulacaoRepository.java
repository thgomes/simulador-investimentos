package com.caixa.repository;

import com.caixa.entity.Simulacao;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class SimulacaoRepository {

    @PersistenceContext
    EntityManager entityManager;

    @Transactional
    public Simulacao salvar(Simulacao simulacao) {
        entityManager.persist(simulacao);
        return simulacao;
    }

    @Transactional
    public List<Simulacao> buscarPorClienteId(Long clienteId) {
        return entityManager
                .createQuery("""
                        SELECT s
                        FROM Simulacao s
                        WHERE s.clienteId = :clienteId
                        ORDER BY s.dataSimulacao DESC
                        """, Simulacao.class)
                .setParameter("clienteId", clienteId)
                .getResultList();
    }

    @Transactional
    public List<Simulacao> buscarTodas() {
        return entityManager
                .createQuery("""
                        SELECT s
                        FROM Simulacao s
                        ORDER BY s.dataSimulacao DESC
                        """, Simulacao.class)
                .getResultList();
    }
}
