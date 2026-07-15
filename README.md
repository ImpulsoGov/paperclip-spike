# API de Cadastro de Cidadãos

API REST de exemplo para cadastro de cidadãos em um sistema municipal de saúde.
Spring Boot 4 · Java 17 · JPA · H2 em memória.

## Rodando

```bash
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`. Console do H2 em `/h2-console` (JDBC URL: `jdbc:h2:mem:cidadaos`, user `sa`, sem senha).

## Endpoints

| Método | Rota | Descrição |
|--------|------|-----------|
| POST   | `/api/cidadaos` | Cadastra cidadão (valida CPF, CNS, UF, data de nascimento) |
| GET    | `/api/cidadaos` | Lista paginada; filtros `?municipio=` ou `?nome=` |
| GET    | `/api/cidadaos/{id}` | Busca por id |
| PUT    | `/api/cidadaos/{id}` | Atualiza cadastro |
| DELETE | `/api/cidadaos/{id}` | Remove cadastro |

O CPF é armazenado normalizado (somente dígitos) e **sempre retornado mascarado** nas respostas (LGPD).

### Exemplo

```bash
curl -X POST http://localhost:8080/api/cidadaos \
  -H "Content-Type: application/json" \
  -d '{
    "nomeCompleto": "José Ferreira Lima",
    "cpf": "390.533.447-05",
    "cns": "898001160660022",
    "dataNascimento": "1978-05-30",
    "sexo": "MASCULINO",
    "municipio": "Recife",
    "uf": "PE",
    "telefone": "81966665555"
  }'
```

O banco sobe com 3 cidadãos de exemplo (CPFs fictícios válidos).
