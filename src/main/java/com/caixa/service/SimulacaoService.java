package com.caixa.service;

import com.caixa.dto.simulacao.AgregadoSimulacoesResponse;
import com.caixa.dto.simulacao.SimulacaoInvestimento;
import com.caixa.dto.simulacao.SimulacaoInvestimentoRequest;
import com.caixa.dto.simulacao.SimulacaoInvestimentoResponse;
import com.caixa.entity.Produto;
import com.caixa.entity.Simulacao;
import com.caixa.exception.ProdutoNaoElegivelException;
import com.caixa.repository.ProdutoRepository;
import com.caixa.repository.SimulacaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class SimulacaoService {

    private static final Logger LOG = Logger.getLogger(SimulacaoService.class);
    private static final MathContext MC = MathContext.DECIMAL64;
    private static final BigDecimal TWELVE = BigDecimal.valueOf(12);
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    @Inject
    ProdutoRepository produtoRepository;

    @Inject
    SimulacaoRepository simulacaoRepository;

    public static BigDecimal calcularValorFinal(BigDecimal valor, BigDecimal rentabilidadeAnual, int prazoMeses) {
        BigDecimal fatorMensal = BigDecimal.ONE.add(rentabilidadeAnual.divide(TWELVE, MC), MC);
        return valor.multiply(fatorMensal.pow(prazoMeses, MC), MC).setScale(2, RoundingMode.HALF_UP);
    }

    public SimulacaoInvestimentoResponse simular(SimulacaoInvestimentoRequest request) {
        LOG.infof("Simulação solicitada: clienteId=%d, valor=%s, prazoMeses=%d, tipoProduto=%s",
                request.clienteId(), request.valor(), request.prazoMeses(), request.tipoProduto());

        Produto produto = buscarProdutoElegivel(
                request.tipoProduto().trim(),
                request.valor(),
                request.prazoMeses()
        );

        BigDecimal valorFinal = calcularValorFinal(
                request.valor(),
                produto.rentabilidadeAnual,
                request.prazoMeses()
        );

        LocalDateTime dataSimulacao = LocalDateTime.now();

        Simulacao simulacao = new Simulacao();
        simulacao.clienteId = request.clienteId();
        simulacao.produtoNome = produto.nome;
        simulacao.tipoProduto = produto.tipoProduto;
        simulacao.valorInvestido = request.valor();
        simulacao.prazoMeses = request.prazoMeses();
        simulacao.rentabilidadeAplicada = produto.rentabilidadeAnual;
        simulacao.valorFinal = valorFinal;
        simulacao.dataSimulacao = dataSimulacao;
        simulacaoRepository.salvar(simulacao);

        LOG.infof("Simulação persistida: id=%d, produto=%s, valorFinal=%s", simulacao.id, produto.nome, valorFinal);

        return new SimulacaoInvestimentoResponse(
                simulacao.id,
                new SimulacaoInvestimentoResponse.ProdutoValidado(
                        produto.id,
                        produto.nome,
                        produto.tipoProduto,
                        produto.rentabilidadeAnual,
                        produto.risco
                ),
                new SimulacaoInvestimentoResponse.ResultadoSimulacao(valorFinal, request.prazoMeses()),
                dataSimulacao
        );
    }

    public Produto buscarProdutoElegivel(String tipoProduto, BigDecimal valor, Integer prazoMeses) {
        List<Produto> produtos = produtoRepository.buscarPorTipoProduto(tipoProduto);

        return produtos.stream()
                .filter(p -> valor.compareTo(p.valorMin) >= 0 && valor.compareTo(p.valorMax) <= 0)
                .filter(p -> prazoMeses >= p.prazoMinMeses && prazoMeses <= p.prazoMaxMeses)
                .findFirst()
                .orElseThrow(() -> new ProdutoNaoElegivelException(
                        "Nenhum produto elegível encontrado para tipoProduto=%s, valor=%s, prazoMeses=%d"
                                .formatted(tipoProduto, valor, prazoMeses)
                ));
    }

    public List<SimulacaoInvestimento> listarHistorico(Long clienteId) {
        return simulacaoRepository.buscarPorClienteId(clienteId)
                .stream()
                .filter(Objects::nonNull)
                .map(s -> new SimulacaoInvestimento(
                        s.id,
                        s.clienteId,
                        s.produtoNome,
                        s.valorInvestido,
                        s.valorFinal,
                        s.prazoMeses,
                        s.dataSimulacao
                ))
                .toList();
    }

    public AgregadoSimulacoesResponse obterAgregado() {
        List<Simulacao> simulacoes = simulacaoRepository.buscarTodas().stream()
                .filter(Objects::nonNull)
                .toList();
        BigDecimal totalInvestido = simulacoes.stream()
                .map(s -> s.valorInvestido)
                .filter(Objects::nonNull)
                .reduce(ZERO, BigDecimal::add);
        BigDecimal totalValorFinal = simulacoes.stream()
                .map(s -> s.valorFinal)
                .filter(Objects::nonNull)
                .reduce(ZERO, BigDecimal::add);
        int quantidadeSimulacoes = simulacoes.size();
        int quantidadeClientes = (int) simulacoes.stream()
                .map(s -> s.clienteId)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        BigDecimal ticketMedio = quantidadeSimulacoes == 0
                ? ZERO
                : totalInvestido.divide(BigDecimal.valueOf(quantidadeSimulacoes), 2, RoundingMode.HALF_UP);
        long somaPrazos = simulacoes.stream()
                .map(s -> s.prazoMeses)
                .filter(Objects::nonNull)
                .mapToLong(Integer::longValue)
                .sum();
        BigDecimal prazoMedioMeses = quantidadeSimulacoes == 0
                ? ZERO
                : BigDecimal.valueOf(somaPrazos).divide(BigDecimal.valueOf(quantidadeSimulacoes), 2, RoundingMode.HALF_UP);

        return new AgregadoSimulacoesResponse(
                quantidadeSimulacoes,
                quantidadeClientes,
                totalInvestido,
                totalValorFinal,
                totalValorFinal.subtract(totalInvestido),
                ticketMedio,
                prazoMedioMeses
        );
    }
}
