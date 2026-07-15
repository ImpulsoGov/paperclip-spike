# Setup passo a passo — API + review multi-agent no Paperclip

Há **dois** "builds" independentes aqui. Faça na ordem.

- **A. Buildar/rodar a API Java** — compilar e subir o Spring Boot (código).
- **B. Ligar o sistema de review** — os arquivos já estão versionados no lugar certo;
  falta configurar o Paperclip (skills + routine) e o GitHub (Action + token).

> Onde cada arquivo vive já está resolvido no repositório. Você **não precisa mover nada**.
> A tabela na seção B.0 mostra o que é "arquivo versionado (pronto)" vs "configuração (você faz na UI)".

---

## Pré-requisitos

- **JDK 17+** instalado (o projeto é Spring Boot 4, exige Java 17). Confirme: `java -version`.
- **Git** configurado e com acesso ao repositório remoto.
- **Paperclip** rodando (você já tem: `localhost:3100`) com o agente **Code Reviewer** criado.
- **Token do GitHub** com escopo `repo` (para o agente ler PRs e postar comentários).
- (Opcional) **Token do Linear** para o agente de requisitos.

---

## A. Buildar e rodar a API Java

No terminal, na raiz do projeto (`C:\Users\Pedro\Documents\Impulso\paperclipai`):

```bash
# 1. Compilar + rodar os testes (a primeira vez baixa o Maven e as dependências)
./mvnw clean verify        # Windows PowerShell: .\mvnw.cmd clean verify

# 2. Subir a aplicação
./mvnw spring-boot:run     # Windows: .\mvnw.cmd spring-boot:run
```

A API sobe em `http://localhost:8080`. Teste:

```bash
# lista os 3 cidadãos do seed
curl http://localhost:8080/api/cidadaos

# cadastra um novo (CPF fictício válido)
curl -X POST http://localhost:8080/api/cidadaos \
  -H "Content-Type: application/json" \
  -d '{"nomeCompleto":"José Ferreira Lima","cpf":"390.533.447-05","dataNascimento":"1978-05-30","sexo":"MASCULINO","municipio":"Recife","uf":"PE"}'
```

Console do banco (H2): `http://localhost:8080/h2-console` — JDBC URL `jdbc:h2:mem:cidadaos`, user `sa`, sem senha.

> Se o primeiro `./mvnw clean verify` acusar erro de compilação, me avise — não consegui rodar o build
> no ambiente da sessão (só tinha Java 11 sem internet), então essa é a primeira validação real.

---

## B. Ligar o sistema de review

### B.0 — Onde cada arquivo vive (já está pronto)

| Arquivo (no repo) | O que é | Ação sua |
|---|---|---|
| `.paperclip/review/agents/01…06.md` | Skills dos 6 especialistas | Registrar como Skills no Paperclip (B.3) |
| `.paperclip/review/agents/07-consolidator.md` | Skill do consolidador | Idem |
| `.paperclip/review/routine-poll.md` | Prompt da routine de polling | Criar uma Routine com esse texto (B.4) |
| `.paperclip/agent-cli-denylist.json` | Deny-list de git do agente | Aplicar na config do agente (B.5) |
| `docs/integration-patterns.md` | Grounding de segurança/LGPD | Nada — o agente de Security lê do repo |
| `.github/workflows/code-review.yml` | Acknowledgment 👀/🚀 + commit trusted | Já roda ao dar push; ver B.6 |
| `scripts/commit-fix.sh` | Push trusted do `/fix` (modelo local) | Deixar executável; ver B.6 |

### B.1 — Publicar no GitHub

```bash
git push origin main
git push -u origin feature/busca-avancada-cidadaos
```

Abra o PR de `feature/busca-avancada-cidadaos` → `main` no GitHub. **Não faça merge** — ele é a "cobaia" da demo.

### B.2 — Conectar o agente Code Reviewer ao GitHub (e Linear)

No Paperclip, no agente **Code Reviewer**, adicione as credenciais/integrações (via MCP ou
`Settings → API Keys`, conforme sua instância):

- **GitHub** — token com escopo `repo`. É o que dá ao agente ler o diff da PR e postar comentários.
- **Linear** (opcional) — token, para o subagente de requisitos buscar o ticket `IMP-<id>`.

> Estas são chamadas de **saída** (o agente chama as APIs). Funcionam de localhost, sem túnel.

### B.3 — Registrar as skills

No menu lateral **Skills**, crie uma skill para cada arquivo de `.paperclip/review/agents/`
(cole o conteúdo do `.md`). Depois, no agente **Code Reviewer**, associe as 7 skills a ele.

Se sua instância suportar subagentes/roles, crie 6 subagentes especialistas (um por skill 01–06)
e o Code Reviewer como orquestrador que chama os 6 + o consolidador (07). Se não, o Code Reviewer
executa as 7 skills em sequência — o resultado é o mesmo, só não roda em paralelo.

### B.4 — Criar a routine de polling

No menu lateral **Routines**, crie uma routine para o agente **Code Reviewer**:

- **Cadência**: 1 minuto (para a demo) — ou deixe manual e dispare o heartbeat na hora.
- **Prompt**: cole o bloco "Prompt da routine" de `.paperclip/review/routine-poll.md`
  (troque `<owner>` pelo seu usuário/org do GitHub).

Isso faz o agente verificar PRs com `/review` pendente e orquestrar o review.

### B.5 — Aplicar a deny-list de git

Na configuração do agente Code Reviewer (ex.: `cli.json`/permissões de comando da sua instância),
aplique o conteúdo de `.paperclip/agent-cli-denylist.json`. Objetivo: o agente **nunca** executa
`git push/commit/merge`. Quem versiona o auto-fix é o `scripts/commit-fix.sh` (ou o job `trusted-commit`
da Action). Este é o pilar human-in-the-loop — vale ressaltar na demo.

### B.6 — GitHub Action e commit do `/fix`

- A Action `.github/workflows/code-review.yml` **já funciona** assim que o push chega ao GitHub —
  ela reage 👀 e comenta ao ver `/review` ou `/fix`. Usa o `GITHUB_TOKEN` padrão; **não precisa de secret**.
- Para o auto-fix no **modelo de polling local**, o push é feito por você (mecanismo trusted):
  ```bash
  chmod +x scripts/commit-fix.sh
  ./scripts/commit-fix.sh feature/busca-avancada-cidadaos "IMP-<id>"
  ```
- Só se quiser o commit **pela CI** (modelo in-cloud), troque `false` por `true` no job `trusted-commit`.

---

## C. Teste de ponta a ponta (ensaie antes da demo)

1. API rodando (A) e PR aberto (B.1).
2. Skills (B.3), routine (B.4), tokens (B.2) e deny-list (B.5) configurados.
3. No PR, comente **`/review`**.
4. Em segundos aparece a reação 👀 e o comentário de acknowledgment (Action).
5. Na próxima rodada da routine (≤1 min) ou no heartbeat manual, o Code Reviewer orquestra os
   6 subagentes + consolidador e posta **inline comments + summary**. Esperado que apareçam:
   SQL injection e PII em log (Security), entidade exposta (Architecture), validação ignorada
   (Requirements/Architecture), falta de testes (E2E).
6. Marque 👍 nos achados que quer corrigir e comente **`/fix`**.
7. O agente edita os arquivos; rode `scripts/commit-fix.sh` para commitar e dar push.
8. Reação muda para 🚀. Mostre o **audit log** do Paperclip (tool-call tracing) — é o momento mais forte.

## D. Plano B para a demo ao vivo

- Rode o ensaio 2×. Se a routine falhar ao vivo, dispare o **heartbeat manual** do agente.
- Tenha **screenshots/gravação** de um review bem-sucedido como fallback.
- Deixe a API já rodando e o PR já aberto antes de começar a apresentar.
