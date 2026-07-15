# Routine de polling — Code Reviewer (Paperclip)

Configuração da rotina que faz o Paperclip orquestrar o review **sem webhook e sem túnel**.
Rode como Routine do agente **Code Reviewer**, com cadência de 2–5 min (na demo, 1 min ou trigger manual).

## Agendamento sugerido

- Demo: a cada **1 minuto** (ou dispare o heartbeat do agente na hora, para ser determinístico).
- Produção: a cada **2–5 minutos** (mais tarde, trocar por webhook do GitHub quando houver endpoint público).

## Prompt da routine

```
Você é o orquestrador de code review. A cada execução:

1. Via GitHub MCP, liste PRs abertas no repositório <owner>/paperclipai.
2. Para cada PR, procure um comentário "/review" que ainda NÃO tenha a reação 👀 (acknowledged).
   - Se não houver, encerre (nada a fazer nesta rodada).
3. Ao encontrar um "/review" pendente:
   a. Reaja ao comentário com 👀 (marca como acknowledged — feedback imediato ao dev).
   b. Monte o contexto: diff da PR, título/body, Linear Ticket ID (extraia do título/body),
      spec files (.specs/ se existir), e comentários existentes.
   c. Crie/atualize uma issue no Paperclip para rastrear este review (audit log).
   d. Despache os 6 subagentes em PARALELO, cada um com sua skill:
      pr-security-reviewer, pr-requirements-reviewer, pr-tests-reviewer,
      pr-architecture-reviewer, pr-regression-reviewer, pr-performance-reviewer.
   e. Passe os resultados ao pr-review-consolidator, que posta inline comments + summary na PR.
   f. Ao concluir, troque a reação para 🚀 (done) e feche a issue de rastreio no Paperclip.

Regras:
- Você NÃO faz merge, approve, commit ou push. Somente comenta.
- Respeite o limiar de confiança de 80% dos subagentes.
- Todo o trabalho fica registrado no audit log do Paperclip (tool-call tracing).
```

## Comando `/fix` (segundo gatilho, opcional)

```
Quando um comentário "/fix" aparecer numa PR já revisada:
1. Reaja com 👀.
2. Filtre os achados que o humano marcou com 👍 (ou os de severidade 🔒/🐛 se ele pedir "aplicar críticos").
3. Aplique as correções mínimas nos arquivos em disco (SOMENTE arquivos; nada de git).
4. Marque cada achado corrigido como [RESOLVED] no comentário correspondente.
5. Reaja com 🚀. O COMMIT e PUSH são feitos pelo workflow do GitHub Actions, não por você.
```

## Por que polling e não webhook (para a demo)

As integrações GitHub/Linear/Sentry são chamadas de **saída** (o agente chama as APIs) — funcionam
de localhost sem problema. Webhook exigiria endpoint público (túnel), risco desnecessário numa demo.
Polling (ou trigger manual) dá o mesmo efeito visual e é determinístico na apresentação.
