package com.caixa.entity;

import java.time.LocalDateTime;
import java.math.BigDecimal;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "simulacoes")
public class Simulacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JdbcTypeCode(SqlTypes.INTEGER)
    @Column(name = "id", columnDefinition = "INTEGER")
    public Long id;

    public Long clienteId;
    public String produtoNome;
    public String tipoProduto;
    public BigDecimal valorInvestido;
    public Integer prazoMeses;
    public BigDecimal rentabilidadeAplicada;
    public BigDecimal valorFinal;
    public LocalDateTime dataSimulacao;
}
