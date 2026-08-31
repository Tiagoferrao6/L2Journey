## Context

Atualmente o sistema possui dois vetores independentes que tentam gerir lojas falsas (FakeShops):
1. O mecanismo legado e altamente configurável de XML (`data/fakeplayers/fake_shops.xml`).
2. Uma injeção "Hardcoded" em DB via `initGludioProfilesIfEmpty()` no `FakePlayerManager` que, em caso de base de dados vazia, cria 30 "Traders" (class_id=53) e 30 "Hunters" em Gludio.

O segundo mecanismo gerou colisão: ao ativarmos a tabela de banco de dados, o manager encheu a cidade de bots genéricos. A visão para o projeto é que *Lojas Estáticas* sejam puramente definidas no XML para facilitar edições (estoque, coords e classes) sem a complexidade de um banco de dados, enquanto *Bots de IA que andam pelo mapa* (FakeHunters e o futuro GTA V mode) sejam salvos no BD com máquina de estados.

## Goals / Non-Goals

**Goals:**
- Desacoplar inteiramente o conceito de Lojas Estáticas (FakeShops) do conceito de Caçadores Dinâmicos (FakeHunters).
- Eliminar o código que injeta dados genéricos e fixos na tabela SQL.
- Garantir que `FakePlayerManager` só recupere entidades do tipo `HUNTER` do banco de dados na hora do spawn de zona.

**Non-Goals:**
- Não iremos deletar a tabela SQL. Ela continuará lá para os Hunters.
- Não iremos refatorar o `FakeHunterAI` ainda, este foco é apenas limpar a responsabilidade dos FakeShops.

## Decisions

1. **Remoção Absoluta de Auto-Populate**
   - *Decisão:* O bloco `initGludioProfilesIfEmpty()` será completamente apagado.
   - *Rationale:* Sistemas em produção não devem popular tabelas vazias silenciosamente com dados hardcoded de teste, especialmente quando interferem no design level. Se a DB estiver vazia, não haverá Hunters, e isso está correto.

2. **Filtro de Spawn no FakePlayerManager**
   - *Decisão:* No método `spawnGludioBotsForActiveSchedule`, ao ler da DAO, vamos carregar e iterar, mas os blocos que checam `if ("TRADER".equalsIgnoreCase(profile.getBotType()))` serão removidos ou ignorados no load da DB. Todo e qualquer `TRADER` virá estritamente do `initFakeShops()` via XML.
   - *Rationale:* Evita duplicação de lógicas de "Trading" entre DB e XML.

## Risks / Trade-offs

- **[Falta de Hunters Nativos]** Com a remoção da auto-população de Hunters no banco, caso o usuário limpe a base, a cidade de Gludio ficará sem os 30 Hunters genéricos.
  - *Mitigação:* Se quisermos voltar a testar Hunters genéricos, faremos isso através de scripts de injeção SQL separados (seeds), e não hardcoded no Manager Java.
