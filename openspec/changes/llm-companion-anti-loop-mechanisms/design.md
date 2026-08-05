## Context

Os bots de IA do L2Journey (`PaladinBot`, `HawkeyeBot`, `BishopBot`) enfrentavam travamentos por loops repetitivos de tomada de decisão e navegação urbana. Os motivos principais foram:
- Re-invocação contínua de rota no `TownWaypointMeshManager` a cada tick do servidor (3s).
- Falha contínua no `BuyListExecutingEngine` por requisição fixa de 7.000 Adena contra saldo inicial de 200 Adena.
- Falta de mecanismos de contorno espacial de geodata e bloqueio de re-entrância.

## Goals / Non-Goals

**Goals:**
- Implementar trava de navegação ativa (`_activeNavigatingBots`) no `TownWaypointMeshManager`.
- Adicionar controle de cooldown (`actionCooldowns`) e contador escalonado de falhas no `LLMCompanionManager`.
- Implementar verificação de saldo mínimo e compra proporcional no `BuyListExecutingEngine`.
- Adicionar lógica de micro-evasão de geodata no `TownWaypointMeshManager`.

**Non-Goals:**
- Não implementar nesta etapa o Watchdog de Timeout de Intenção (Item 5 da Parte 1, adiado a pedido do usuário).
- Não alterar os motores táticos individuais de combate das personas (`ShirouTacticalEngine`, `CrystalTacticalEngine`).

## Decisions

### 1. Trava de Navegação Ativa (`_activeNavigatingBots`)
- **Decisão**: Manter um `Set<Integer> _activeNavigatingBots` em `TownWaypointMeshManager`. Se um bot já está navegando, qualquer nova tentativa de navegação urbana para o mesmo bot é ignorada até que o percurso seja concluído ou explicitamente cancelado.
- **Alternativas consideradas**: Fila de comandos de navegação. Rejeitada por gerar atrasos e acumulação de movimentação defasada.

### 2. Cooldown de Ação e Contador de Falhas no Companion Manager
- **Decisão**: Adicionar `Map<Integer, Long> _shopCooldowns` no `LLMCompanionManager`. Quando `executePurchase` falha por Adena insuficiente, o bot ganha 60 segundos de cooldown onde `needsConsumableReplenishment` retorna `false`.
- **Alternativas consideradas**: Forçar o bot a sentar. Rejeitada pois o bot precisa continuar caçando mobs para obter moedas.

### 3. Compras Proporcionais e Saldo Mínimo
- **Decisão**: No `BuyListExecutingEngine`, se a Adena total do bot for menor que 7.000, mas maior que a Adena necessária para 50 shots (500 Adena), o bot compra uma quantidade menor e ativa os shots. Se for menor que 500 Adena, cancela a compra e aplica cooldown.

### 4. Micro-Evasão de Geodata
- **Decisão**: Se $\Delta X^2 + \Delta Y^2 < 30^2$ após 3 checagens de movimento (7.5s), o bot executa um passo lateral randômico (`randomOffset(80, 80)`) antes de disparar o fallback de teleport direto.

## Risks / Trade-offs

- **[Risk] Bot ficar sem Soulshots em combate prolongado** → **Mitigação**: O bot caça sem Soulshots até acumular Adena suficiente para o menor pacote de reposição (50 shots = 500 Adena).
- **[Risk] Rotas de navegação ficarem presas se o bot for atacado no caminho** → **Mitigação**: O callback de rota remove a trava do bot e reseta o nó se a navegação falhar após as tentativas de evasão.
