#!/usr/bin/env bash
# Mecanismo TRUSTED de commit+push do auto-fix (modelo de polling local).
#
# Por que existe: o agente do Paperclip é DENY-LISTED para `git commit`/`git push`
# (ver .paperclip/agent-cli-denylist.json). A IA só edita arquivos em disco. Este script
# — executado pelo operador ou por um step confiável, não pelo agente — faz o commit e push.
#
# Uso:  ./scripts/commit-fix.sh <branch-da-pr> "IMP-42"
set -euo pipefail

BRANCH="${1:?informe a branch da PR}"
TICKET="${2:-}"

git checkout "$BRANCH"

if [ -z "$(git status --porcelain)" ]; then
  echo "Nenhuma alteração pendente — a IA não modificou arquivos."
  exit 0
fi

MSG="fix: aplica correções aprovadas no review (via Paperclip) [RESOLVED]"
[ -n "$TICKET" ] && MSG="$MSG

Refs: $TICKET"

git add -A
git commit -m "$MSG"
git push origin "$BRANCH"
echo "✅ Correções commitadas e enviadas em $BRANCH."
