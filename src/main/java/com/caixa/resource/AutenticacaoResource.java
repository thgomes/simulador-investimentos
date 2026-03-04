package com.caixa.resource;

import com.caixa.dto.autenticacao.LoginRequest;
import com.caixa.dto.autenticacao.LoginResponse;
import io.smallrye.jwt.build.Jwt;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Instant;
import java.util.Set;

@Path("/autenticacao")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AutenticacaoResource {

    @ConfigProperty(name = "app.autenticacao.username")
    String usuarioAplicacao;

    @ConfigProperty(name = "app.autenticacao.password")
    String senhaAplicacao;

    @ConfigProperty(name = "app.autenticacao.token-expiracao-segundos", defaultValue = "86400")
    long tokenExpiracaoSegundos;

    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest request) {
        if (!usuarioAplicacao.equals(request.username()) || !senhaAplicacao.equals(request.password())) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        String token = Jwt.issuer("simulador-investimentos")
                .subject(request.username())
                .groups(Set.of("user"))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(tokenExpiracaoSegundos))
                .sign();

        return Response.ok(new LoginResponse(token, "Bearer")).build();
    }
}
