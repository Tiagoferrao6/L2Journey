## Context

Atualmente, `WebAPIManager` consulta apenas `FakeHunterManager.getInstance().getHunters()` e `FakeTraderManager.getInstance().getTraders()`. O trio de bots AI Companions (`PaladinBot`, `HawkeyeBot`, `BishopBot`) é gerenciado exclusivamente por `LLMCompanionManager` e, por isso, não é retornado nas métricas `/api/admin/metrics` nem nas rotas `/api/admin/fakeplayers`.

## Goals / Non-Goals

**Goals:**
- Incluir as instâncias ativas do trio de companions (`LLMCompanionManager.getInstance().getTrio()`) nas respostas de `/api/admin/fakeplayers`.
- Atribuir o tipo `"COMPANION"` para diferenciar visualmente no dashboard entre Hunter, Trader e Companion.
- Permitir a busca e inspeção individual de equipamentos, buffs e inventário de companions via `/api/admin/fakeplayers/{name}`.

**Non-Goals:**
- Não alterar as permissões de acesso do Administrador GM.

## Decisions

### 1. Consulta Consolidada de Bots no WebAPIManager
- **Decisão**: Criar um método auxiliar `getAllFakePlayers()` em `WebAPIManager` que une Hunters, Traders e os bots ativos obtidos via `LLMCompanionManager.getInstance().getTrio()`.
- **Alternativas consideradas**: Criar um endpoint separado `/api/admin/companions`. Rejeitada para manter a interface unificada na tabela de bots do Dashboard.

### 2. Identificador de Tipo `"COMPANION"`
- **Decisão**: A propriedade `"type"` no JSON do bot retornará `"COMPANION"` quando a instância pertencer ao trio do Companion Manager.

## Risks / Trade-offs

- **[Risk] Bot companion inativo/deslogado ser consultado** → **Mitigação**: O endpoint só inspeciona companions com `botInstance != null && botInstance.isOnline()`.
