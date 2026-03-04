package com.caixa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JdbcTypeCode(SqlTypes.INTEGER)
    @Column(name = "id", columnDefinition = "INTEGER")
    public Long id;

    public String nome;
    public String tipoProduto;
    public BigDecimal rentabilidadeAnual;
    public String risco;
    public Integer prazoMinMeses;
    public Integer prazoMaxMeses;
    public BigDecimal valorMin;
    public BigDecimal valorMax;
}
