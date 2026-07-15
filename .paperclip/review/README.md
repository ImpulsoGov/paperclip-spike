# Multi-Agent PR Review — orquestrado no Paperclip

Sistema de revisão de Pull Requests baseado nos 7 pilares do review confiável com IA:
6 subagentes especialistas rodando em paralelo + 1 consolidador, cada um com escopo,
docs e checklist próprios. A IA **nunca** aprova nem rejeita o PR — ela reporta; o humano decide.

## Como funciona (visão geral)

```
/review na PR
   │  (polling via GitHub MCP — sem webhook, sem túnel)
   ▼
Code Reviewer (Paperclip) monta o contexto:
   PR diff + título/body + Linear Ticket ID + spec files + comentários existentes
   │
   ├─ despacha 6 subagentes em paralelo ─────────────────────────┐
   │   1. Security          (.paperclip/review/agents/01-security.md)
   │   2. Requirements      (.paperclip/review/agents/02-requirements.md)
   │   3. E2E Tests         (.paperclip/review/agents/03-e2e-tests.md)
   │   4. Architecture      (.paperclip/review/agents/04-architecture.md)
   │   5. Regression/Halluc.(.paperclip/review/agents/05-regression-hallucination.md)
   │   6. Performance       (.paperclip/review/agents/06-performance.md)
   │
   ▼
7. Consolidador (.paperclip/review/agents/07-consolidator.md)
   agrupa por severidade · deduplica · detecta gaps · posta summary + inline comments
```

## Os 7 pilares aplicados aqui

1. **Especialização** — 6 agentes focados, não 1 prompt genérico.
2. **Grounding** — cada agente carrega docs do projeto antes de tocar no diff (regras SUAS, não inventadas).
3. **Second pass** — todo agente relê o diff inteiro e lista explicitamente o que NÃO comentou.
4. **Precision > recall** — limiar de confiança de 80%; na dúvida, não reporta (evita o efeito cry-wolf).
5. **Human-in-the-loop** — a IA sugere, o humano autoriza via 👍 e `/fix`. Nunca faz merge.
6. **Rastreabilidade** — cada requisito do ticket Linear é verificado item a item contra o diff.
7. **Meta-review** — o agente de Regression/Hallucination revisa a própria IA (phantom imports, dead code).

## Regras de segurança (não-negociáveis)

- Os agentes **não** têm permissão de `git push` / `git commit` (deny-list na config do agente).
- O commit do auto-fix é feito pelo workflow do GitHub Actions, não pela IA.
- A IA apenas modifica arquivos em disco e posta comentários.
- Segredos (tokens GitHub/Linear/Sentry) ficam no ambiente do agente, nunca no diff nem em log.

## Mapa de arquivos

| Arquivo | Papel |
|---|---|
| `agents/01-security.md` … `06-*.md` | Skills dos 6 especialistas |
| `agents/07-consolidator.md` | Consolidação e postagem |
| `routine-poll.md` | Prompt/config da routine de polling no Paperclip |
| `../../.github/workflows/code-review.yml` | Trigger `/review` + acknowledgment + auto-fix |
| `../../docs/integration-patterns.md` | Doc de grounding do agente de Security |
