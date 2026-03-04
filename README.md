# API de Simulação de Investimentos

API back-end para simulação de investimentos com persistência de histórico, construída para o desafio técnico da CAIXA.

## Visao geral

### Stack
- ✅ Java 21 + Quarkus (REST, JPA/Hibernate) + SQLite

### Estrutura do projeto

```text
src/main/java/com/caixa/
├── dto/
├── entity/
├── exception/
├── repository/
├── resource/
└── service/
```

### Escopo MVP
- ✅ Banco SQLite com tabelas `produtos` e `simulacoes`
- ✅ Seed de dados automatico (`import.sql`)
- ✅ `POST /simulacoes` com validacoes e regra de elegibilidade
- ✅ Retorno HTTP 422 para produto nao elegivel
- ✅ `GET /simulacoes?clienteId=...` para historico
- ✅ Validações e tratamento de exceção
- ✅ Testes automatizados com cobertura superior a 99%

### Recursos extras
- ✅ Dockerfile
- ✅ Autenticacao JWT simples
- ✅ Endpoint de agregacao
- ✅ Logs estruturados (JSON)

## Como rodar

### Pre-requisitos
- Para execucao local: Java 21 e Maven 3.9+
- Para execucao com Docker: apenas Docker

### Opcao 1 - Subir localmente
```bash
./mvnw quarkus:dev
```

API em: `http://localhost:8080`

Observacao: o banco SQLite (`investimentos.db`) e criado/usado automaticamente, com carga inicial via `src/main/resources/import.sql`.

### Opcao 2 - Subir com Docker
```bash
docker build -f src/main/docker/Dockerfile.jvm -t simulador-investimentos .
docker run --rm -p 8080:8080 simulador-investimentos
```

## Endpoints da API

### POST `/autenticacao/login` (JWT)
Request:
```json
{
  "username": "admin",
  "password": "admin123"
}
```

Response 200 (exemplo):
```json
{
  "accessToken": "<jwt-token>",
  "tokenType": "Bearer"
}
```

Erros esperados:
- 400: payload invalido (campos obrigatorios)
- 401: credenciais invalidas

Observacao:
- As credenciais de login sao configuradas em `src/main/resources/application.properties` nas chaves `app.autenticacao.username` e `app.autenticacao.password`.

Uso do token:
- Enviar `Authorization: Bearer <accessToken>` nos endpoints protegidos em `/simulacoes`.

### POST `/simulacoes`
Request:
```json
{
  "clienteId": 123,
  "valor": 10000.00,
  "prazoMeses": 12,
  "tipoProduto": "CDB"
}
```

Response 200 (exemplo):
```json
{
  "simulacaoId": 10,
  "produtoValidado": {
    "id": 1,
    "nome": "CDB Caixa 2026",
    "tipo": "CDB",
    "rentabilidade": 0.12,
    "risco": "Baixo"
  },
  "resultadoSimulacao": {
    "valorFinal": 11268.25,
    "prazoMeses": 12
  },
  "dataSimulacao": "2026-03-03T14:00:00"
}
```

Erros esperados:
- 400: validacao de campos obrigatorios/positivos
- 422: nenhum produto elegivel
- 401: sem token JWT

### GET `/simulacoes?clienteId=123`
Response 200 (exemplo):
```json
[
  {
    "id": 1,
    "clienteId": 123,
    "produto": "CDB Caixa 2026",
    "valorInvestido": 10000.00,
    "valorFinal": 11268.25,
    "prazoMeses": 12,
    "dataSimulacao": "2026-03-03T14:00:00"
  }
]
```

Erros esperados:
- 400: parametro `clienteId` ausente
- 401: sem token JWT

### GET `/simulacoes/agregado` (bonus)
Response 200 (exemplo):
```json
{
  "quantidadeSimulacoes": 4,
  "quantidadeClientes": 2,
  "totalInvestido": 30000.00,
  "totalValorFinal": 33583.64,
  "rentabilidadeTotal": 3583.64,
  "ticketMedio": 7500.00,
  "prazoMedioMeses": 12.00
}
```

Erros esperados:
- 401: sem token JWT

## Regras de negocio implementadas

- Campos obrigatorios e valores positivos validados via Bean Validation
- Produto elegivel por:
  - `tipoProduto` igual ao solicitado
  - `valor` entre `valorMin` e `valorMax`
  - `prazoMeses` entre `prazoMinMeses` e `prazoMaxMeses`
- Formula usada:
  - `valorFinal = valor * (1 + rentabilidadeAnual / 12) ^ prazoMeses`
- Persistencia da simulacao com historico em SQLite

## Banco de dados

- Tabelas:
  - `produtos`
  - `simulacoes`
- Seed inicial em `src/main/resources/import.sql` com CDB, LCI e LCA
- Configuracao em `src/main/resources/application.properties`

## Testes automatizados

Rodar testes:
```bash
./mvnw test
```

Gerar cobertura:
```bash
./mvnw test -Dquarkus.jacoco.report=true
```

Relatorio: `target/jacoco-report/index.html`

Principais testes implementados:
- `SimulacaoServiceTest` (calculo)
- `SimulacaoResourceTest` (POST/GET, validacoes, 422, agregado, 401 sem token)
- `AutenticacaoResourceTest` (login valido/invalido)
- `ExceptionMappersTest` (400 e 422)
- `SimulacaoRepositoryTest` (persistencia/ID)
- `SimulacaoServiceCoberturaTest` (cenarios adicionais de cobertura)

