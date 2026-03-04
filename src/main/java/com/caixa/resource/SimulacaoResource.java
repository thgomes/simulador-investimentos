package com.caixa.resource;

import com.caixa.dto.simulacao.AgregadoSimulacoesResponse;
import com.caixa.dto.simulacao.SimulacaoInvestimento;
import com.caixa.dto.simulacao.SimulacaoInvestimentoRequest;
import com.caixa.dto.simulacao.SimulacaoInvestimentoResponse;
import com.caixa.service.SimulacaoService;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/simulacoes")
@Authenticated
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SimulacaoResource {

    @Inject
    SimulacaoService simulacaoService;

    @POST
    public SimulacaoInvestimentoResponse criarSimulacao(@Valid SimulacaoInvestimentoRequest request) {
        return simulacaoService.simular(request);
    }

    @GET
    public List<SimulacaoInvestimento> listarHistorico(@QueryParam("clienteId") Long clienteId) {
        if (clienteId == null) {
            throw new BadRequestException("O parâmetro clienteId é obrigatório");
        }
        return simulacaoService.listarHistorico(clienteId);
    }

    @GET
    @Path("/agregado")
    public AgregadoSimulacoesResponse obterAgregado() {
        return simulacaoService.obterAgregado();
    }
}
