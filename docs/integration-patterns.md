# Padrões de integração, dados e logging

Fonte de verdade para os agentes de review (especialmente Security e Architecture).
Estas são as regras **deste projeto** — não convenções genéricas.

## Dados pessoais sensíveis (LGPD)

O sistema trata dados de saúde. São **sensíveis**: CPF, CNS, nome completo, data de nascimento,
telefone, endereço. Regras:

- **Nunca** logar dado sensível — nem completo, nem parcial, nem em `debug`. Logue **id** do cidadão.
- CPF é armazenado normalizado (só dígitos) e **sempre mascarado na resposta** (`***.456.789-**`).
- Respostas HTTP usam DTO (`*Response`); a entidade JPA nunca é serializada diretamente.

## Acesso a dados

- Todo acesso a banco via Spring Data JPA (repository) ou JPQL parametrizado.
- **Proibido** montar SQL/JPQL por concatenação de string com input do usuário. Use parâmetros
  vinculados (`:nome`) ou `Specification`/Query Methods.
- Listagens são paginadas (`Pageable`).

## Entrada

- Todo input externo passa pela validação do `service` antes de persistir. Não há "atalho" de
  importação que ignore validação.
- `@RequestBody` mapeia para `*Request` (record), nunca para a entidade.

## Segredos

- Tokens (GitHub, Linear, Sentry) vêm de variáveis de ambiente. Nunca hardcoded, nunca em log,
  nunca no diff.
