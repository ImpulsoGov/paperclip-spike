# Por que Paperclip para o review multi-agent — defesa técnica

*Documento de apoio à demo. Público: diretor técnico e tech leads. Preços e recursos verificados em julho/2026 (ver Fontes).*

## 1. Tese

A proposta **não** é "adotar mais um bot de code review". É adotar um **orquestrador de agentes**
sobre o qual o review multi-agent é apenas o primeiro caso de uso. O mesmo control plane já roda,
na nossa PoC, outros três agentes — triagem de tickets no Linear e RCA/fix-suggestion de erros no
Sentry — com governança, orçamento e trilha de auditoria unificados.

A pergunta certa não é *"qual bot revisa PR melhor?"*, e sim:
**"queremos definir o que é um bom review — e operar uma frota de agentes — ou terceirizar essa definição a um produto fechado?"**

## 2. O que estamos comparando (e por que a comparação não é 1:1)

| | O que é | Você controla o review? | Faz mais que review? |
|---|---|---|---|
| **Paperclip** | Plataforma open-source de orquestração de agentes | **Sim** — skills versionadas no repo | **Sim** — qualquer agente (Linear, Sentry, …) |
| CodeRabbit | Produto SaaS de code review | Parcial (YAML + checks) | Parcial (Issue Planner) |
| Copilot Code Review | Feature dentro do GitHub | Limitado (custom instructions) | Não (é review) |
| Cursor BugBot | Produto SaaS de code review | Parcial (BUGBOT.md rules) | Não |
| Greptile | Produto SaaS de code review | Parcial (.greptile.yaml) | Não |
| DIY (GH Actions + Claude API) | Você constrói tudo | **Sim** | Sim, mas você reconstrói a orquestração |

Os quatro produtos SaaS resolvem "revisar PR" muito bem e com pouco esforço. Paperclip e DIY
resolvem "operar agentes que fazem o que **nós** definimos". A escolha é entre **conveniência**
(SaaS) e **controle + amplitude** (Paperclip/DIY). O diferencial do Paperclip sobre o DIY é dar
esse controle **sem** reconstruir a camada operacional.

## 3. Critérios de avaliação

Derivados dos 7 pilares do review confiável e das necessidades da Impulso:

1. **Controle do prompt/lógica** — conseguimos escrever exatamente o que cada agente checa?
2. **Especialização multi-agent + second pass** — vários especialistas, não um prompt genérico.
3. **Grounding nos nossos docs** — regras SUAS, LGPD, domínio saúde — não "conhecimento geral".
4. **Rastreabilidade de requisitos** — verificar entrega contra o ticket do Linear.
5. **Governança operacional** — orçamento por agente, aprovações, audit log, isolamento de dados.
6. **Amplitude** — a mesma plataforma serve outros agentes (Linear, Sentry) num só control plane.
7. **Custo e lock-in** — modelo de preço e dependência de fornecedor.
8. **Esforço de adoção** — quão rápido chega a valor.

## 4. Tabela comparativa

Legenda: ✅ forte · ⚠️ parcial/limitado · ❌ ausente.

| Critério | **Paperclip (proposto)** | CodeRabbit | Copilot Code Review | Cursor BugBot | Greptile |
|---|---|---|---|---|---|
| Controle total do prompt | ✅ skills em Markdown, versionadas | ⚠️ YAML + checks | ⚠️ custom instructions | ⚠️ regras BUGBOT.md | ⚠️ .greptile.yaml |
| Multi-agent especializado | ✅ 6 + consolidador, você define | ⚠️ multi-agente fixo | ❌ single-agent | ❌ single-agent | ⚠️ "swarm" fechado |
| Second pass obrigatório | ✅ no prompt | ❌ | ❌ | ❌ | ❌ |
| Detecção de alucinação (meta-review) | ✅ agente dedicado | ❌ | ❌ | ❌ | ❌ |
| Grounding nos nossos docs | ✅ arbitrário (repo) | ⚠️ limitado | ⚠️ instructions | ⚠️ rules | ✅ índice semântico |
| Rastreabilidade Linear (requisitos) | ✅ via MCP, nós definimos | ✅ Issue Planner (Linear/Jira) | ❌ não no review | ❌ | ❌ |
| Human-in-the-loop (nunca aprova/rejeita) | ✅ por design | ⚠️ configurável | ⚠️ sugere fixes | ⚠️ | ⚠️ |
| Auto-fix com aprovação humana | ✅ `/fix` + commit trusted | ⚠️ aplica sugestões | ✅ coding agent gera PR | ✅ autofix | ⚠️ |
| Orçamento/custo por agente | ✅ nativo | ❌ | ❌ | ⚠️ usage-based | ⚠️ usage-based |
| Aprovações + audit log | ✅ nativo (governança) | ⚠️ enterprise | ⚠️ enterprise | ❌ | ⚠️ |
| Faz além de review (Linear, Sentry…) | ✅ mesma plataforma | ❌ | ⚠️ coding agent | ❌ | ❌ |
| Open-source / self-host | ✅ MIT, self-host | ❌ SaaS | ❌ | ❌ | ❌ |
| Integrações | GitHub, GitLab… via MCP | GitHub/GitLab/ADO/Bitbucket | **GitHub only** | **GitHub only** | GitHub/GitLab (sem ADO/Bitbucket) |
| Esforço de adoção | ⚠️ **alto** (nós construímos) | ✅ baixo | ✅ baixíssimo | ✅ baixo | ✅ baixo |
| Custo (jul/2026) | API pay-per-use + infra self-host | Free / ~US$24–30 · Plus US$48 user/mês | Pro US$10 · Business US$19 · Ent. US$39/user (review consome 13× da cota premium) | usage-based ~US$1–1,50/run | usage-based US$1/review após 50 · Pro US$30/user |

> Nota de honestidade: os números de preço mudam rápido e alguns variam entre fontes (ex.: CodeRabbit
> Pro aparece entre US$15 e US$30/user dependendo da fonte e do ciclo). Trate a coluna de custo como
> ordem de grandeza, não cotação. Ver Fontes.

## 5. Análise por alternativa (com o lado bom de cada uma)

**CodeRabbit** — provavelmente o SaaS mais completo. Line-by-line, 40+ linters determinísticos,
SAST multi-tool, e o **Issue Planner** já integra Linear/Jira (concorre direto com o nosso pilar de
rastreabilidade). Se o objetivo fosse *só* revisar PR com o mínimo de esforço, seria a escolha padrão.
Limites para nós: é caixa-parcial (não escrevemos a lógica dos agentes), sem second-pass/meta-review
explícitos, e não faz nada além de review — continuaríamos precisando de outra coisa para Linear/Sentry.

**GitHub Copilot Code Review** — imbatível em fricção: já vive dentro do GitHub, custo marginal baixo
se já pagamos Copilot, e o coding agent gera PRs de correção. Porém é **single-agent**, custom
instructions são limitadas (e melhores só em Business/Enterprise), cada review consome **13× da cota**
de premium requests, e não há especialização/second-pass. Ótimo como rede de segurança genérica;
fraco como "review que o time confia e age".

**Cursor BugBot** — rápido (90% dos runs < 3 min), barato por run, bom se o time já vive no Cursor.
Mas é single-agent, **GitHub-only**, e o controle se resume a um arquivo de regras. Não cobre
rastreabilidade de requisitos nem governança de frota.

**Greptile** — o grounding é o ponto forte: índice semântico do repositório inteiro, não só do diff.
Alega alta taxa de detecção. Mas é SaaS fechado, sem geração de teste, sem Bitbucket/ADO, e — como os
demais — não sai do escopo de review.

**DIY (GitHub Actions + Claude API)** — nos dá 100% de controle, como o Paperclip. É, na prática, o
modelo "TLC DIY" dos diagramas. O problema é o que **não** aparece no diagrama: teríamos que construir e
manter orçamento por agente, aprovações, audit log, retry/estado, org chart de agentes e o painel
operacional. Paperclip é exatamente essa camada pronta — mantendo o controle do DIY sem o custo de
reconstruí-la.

## 6. Por que Paperclip para a Impulso, especificamente

1. **Uma frota, um control plane.** Review de PR é o agente nº 1. Já temos Linear Reporter e Sentry
   Investigator na mesma instância, com o mesmo orçamento, audit e governança. Nenhum SaaS de review
   entrega isso — comprar quatro produtos separados fragmenta operação, custo e dados.
2. **Domínio sensível (saúde/LGPD).** Podemos codificar regras nossas — "nunca logar CPF/CNS",
   "entidade não cruza a borda HTTP" — como skills versionadas e auditáveis. Não dependemos do que um
   fornecedor considera "boa prática".
3. **Skills como fonte de verdade, no nosso repo.** O que é um bom review vira código revisável e
   versionado, não configuração opaca num painel de terceiro.
4. **Self-host + open-source (MIT).** Sem lock-in, dado sob nosso controle — relevante para dados de saúde.
5. **Human-in-the-loop por design.** A IA nunca aprova/rejeita; o humano decide via 👍/`/fix`. É política,
   não um checkbox.

## 7. Riscos e mitigações (o que a concorrência vai levantar — e nós levantamos primeiro)

| Risco | Real? | Mitigação |
|---|---|---|
| "Vocês vão construir e manter isso — custo de engenharia." | Sim, o maior. | Skills são Markdown, não serviço. A camada de orquestração/ops é o Paperclip (pronta). Escopo inicial enxuto: 1 caso de uso sólido antes de escalar. |
| "Paperclip é jovem/OSS — risco de maturidade." | Parcial. | Self-host = não dependemos de uptime de terceiro; MIT = podemos forkar. Começar com PoC de baixo risco. |
| "Um SaaS entrega review amanhã; vocês, em semanas." | Sim. | Verdade para *só review*. Nosso ganho é a frota + controle. Podemos rodar CodeRabbit/Copilot **em paralelo** como rede de segurança durante a adoção. |
| "Falsos positivos vão minar a confiança." | Risco de qualquer AI review. | Pilares 3–4: second pass + limiar de 80% + meta-review. É justamente onde os single-agent falham. |
| "E a segurança de deixar IA mexer no repo?" | Legítimo. | Agente é deny-listed para push/commit/merge; quem versiona é step trusted. Audit log completo. |
| "Custo de API imprevisível." | Gerenciável. | Orçamento por agente nativo do Paperclip — o agente para ao atingir o teto. |

## 8. Objeções antecipadas → respostas curtas (para o Q&A)

- **"Por que não só o Copilot, já que pagamos GitHub?"** — Copilot é single-agent, sem
  especialização/second-pass, review consome 13× da cota, e não resolve Linear/Sentry. Serve como rede
  de segurança genérica, não como o review que o time confia e age. Podem coexistir.
- **"CodeRabbit já integra Linear e tem 40+ linters. O que ganhamos reescrevendo?"** — Ganhamos controle
  da lógica (nossas regras de LGPD/arquitetura como código auditável), second-pass e meta-review que ele
  não tem, e — decisivo — a mesma plataforma operando Sentry e outros agentes. CodeRabbit resolve review;
  não resolve frota.
- **"Isso não é over-engineering para revisar PR?"** — Seria, se fosse só PR. É uma plataforma de agentes;
  o review é a prova de conceito. O ROI vem de reusar orquestração/governança nos próximos agentes.
- **"E se o Paperclip morrer como projeto?"** — MIT + self-host: temos o código e os dados. O
  investimento real (as skills) é portável — são Markdown com a nossa lógica.
- **"Como sabemos que é confiável e não vai fazer besteira no repo?"** — Deny-list de git no agente,
  commit por mecanismo trusted, human-in-the-loop obrigatório, e audit log imutável de cada tool-call.

## 9. Recomendação

Adotar o Paperclip como **plataforma de agentes**, com o review multi-agent como PoC de produção.
Durante a adoção, manter um SaaS (CodeRabbit ou Copilot) como rede de segurança em paralelo — baixo
custo, remove o risco de "e se a nossa PoC falhar?". Reavaliar em 1–2 sprints com métricas: % de
comentários endereçados pelo autor, falsos positivos, tempo de review, e reuso da plataforma pelos
agentes de Linear/Sentry.

---

## Fontes

- [CodeRabbit — Pricing](https://www.coderabbit.ai/pricing) · [CostBench: CodeRabbit 2026](https://costbench.com/software/ai-code-review/coderabbit/)
- [Cursor — Bugbot updates (jun/2026)](https://cursor.com/blog/bugbot-updates-june-2026) · [Cursor — Bugbot](https://cursor.com/bugbot)
- [GitHub Copilot — Plans & pricing](https://github.com/features/copilot/plans) · [GitHub Docs — Copilot code review](https://docs.github.com/copilot/using-github-copilot/code-review/using-copilot-code-review)
- [Greptile — Pricing](https://www.greptile.com/pricing) · [Greptile v4 + New Pricing](https://www.greptile.com/blog/greptile-v4)
- [Paperclip (repo)](https://github.com/paperclipai/paperclip) · [paperclip-mcp](https://github.com/wizarck/paperclip-mcp)

*Preços e recursos verificados em 15/07/2026; variam por fonte e ciclo de cobrança — confirmar antes de decisão formal.*
