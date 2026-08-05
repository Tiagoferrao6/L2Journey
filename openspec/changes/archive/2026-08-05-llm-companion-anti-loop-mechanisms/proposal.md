## Why

AI Companions (`PaladinBot`, `HawkeyeBot`, `BishopBot`) por vezes entram em loops infinitos de tomadas de decisão e navegação urbana. Os principais motivadores identificados incluem:
1. Re-invocação incondicional do gatilho de compras (`needsConsumableReplenishment`) sem verificar se o bot tem Adena suficiente para o pacote básico.
2. Interrupção constante da navegação urbana (`TownWaypointMeshManager`) por chamadas repetidas a cada tick do servidor (3s).
3. Falta de mecanismos de recuperação espacial (geodata) e resiliência financeira adaptativa.

Este projeto introduz um conjunto de mecanismos de prevenção contra loops e resiliência financeira para estabilizar a autonomia 24/7 dos companheiros.

## What Changes

- **Cooldown de Ações com Falha (Action Failure Backoff)**: Quando a compra de suprimentos falha por Adena insuficiente ou indisponibilidade, o gatilho de compras entra em cooldown ajustável (ex: 60s), permitindo que o bot transicione para caça (`FARM_ZONE`).
- **Circuito de Falhas Consecutivas (Failure Circuit Breaker)**: Rastreamento do contador de falhas consecutivas do bot com níveis de escalonamento (reset de intenção -> liberação de rota -> teleport de emergência).
- **Orçamento Adaptativo de Compras (Adaptive Budgeting)**: Ajuste dinâmico das quantidades a comprar com base no saldo real do inventário (ex: comprar pacotes menores se o bot não tiver 7.000 Adena).
- **Detecção de Colisão e Micro-Evasão de Geodata**: Identificação de estagnação espacial do bot ($\Delta X, \Delta Y < 30$) e aplicação de deslocamentos vetoriais laterais antes da re-navegação.
- **Trava de Navegação em Progresso (`isNavigating`)**: Bloqueio de reinício de rotas urbanas enquanto o bot já estiver percorrendo um caminho no `TownWaypointMeshManager`.

## Capabilities

### New Capabilities
- `companion-resilience-anti-loop`: Define requisitos para resiliência financeira, bloqueio de rotas reentrantes e contornamento de geodata para bots de IA.

### Modified Capabilities
- `coop-companion`: Atualiza os requisitos de comportamento autônomo e de consumo de loja dos companions.

## Impact

- `com.l2journey.gameserver.managers.BuyListExecutingEngine`: Atualização de lógica de verificação de Adena e compras proporcionais.
- `com.l2journey.gameserver.managers.TownWaypointMeshManager`: Implementação de trava `_activeNavigatingBots` e micro-evasão.
- `com.l2journey.gameserver.managers.LLMCompanionManager`: Adição de cooldowns de ação e contadores de falhas por bot.
