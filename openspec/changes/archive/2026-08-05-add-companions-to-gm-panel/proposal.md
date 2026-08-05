## Why

Atualmente, os bots da IA Co-op Companions (`PaladinBot`, `HawkeyeBot`, `BishopBot`) mantidos pelo `LLMCompanionManager` não aparecem na lista de Fake Players do Painel de Administrador/GM e da Web API REST (`/api/admin/fakeplayers`).
Como o `WebAPIManager` consulta exclusivamente o `FakeHunterManager` e o `FakeTraderManager`, os Administradores não conseguem monitorar o status, inventário, localização, HP/MP nem inspecionar os AI Companions pelo Dashboard Web.

Esta mudança inclui a integração total dos AI Companions à lista e ao modal de inspeção do Painel de GM.

## What Changes

- **Integração na REST API de FakePlayers (`GET /api/admin/fakeplayers`)**: Inclui as instâncias ativas do trio de AI Companions (`PaladinBot`, `HawkeyeBot`, `BishopBot`) na resposta JSON listando o tipo `"COMPANION"`.
- **Inspeção Detalhada (`GET /api/admin/fakeplayers/{name}`)**: Suporte a busca e inspeção completa de equipamentos, inventário e buffs de bots do tipo Companion.
- **Métricas do Servidor (`GET /api/admin/metrics`)**: Atualização do cálculo de contagem de bots e players reais no dashboard para contabilizar os AI Companions ativos.

## Capabilities

### New Capabilities

*(Nenhuma nova capacidade criada; atualização de funcionalidade existente)*

### Modified Capabilities
- `gm-control-panel`: Atualiza os requisitos do painel de controle GM para listar e inspecionar bots do tipo `COMPANION`.

## Impact

- `com.l2journey.gameserver.managers.WebAPIManager`: Atualização das rotas `/api/admin/fakeplayers` e `/api/admin/metrics` para consultar o `LLMCompanionManager`.
- Frontend Web do Painel de GM: Exibição da badge/tipo `COMPANION` na tabela de gerenciamento de bots.
