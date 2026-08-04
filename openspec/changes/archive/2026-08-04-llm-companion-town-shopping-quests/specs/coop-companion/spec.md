# Co-op Companion Specification

## ADDED Requirements

### Requirement: Navegação por Malha de Waypoints em Cidades (Town Waypoint Mesh)
The system MUST provide a node-based waypoint graph for major towns (Gludio, Giran) integrated with `GeoEngine` A* pathfinding, allowing AI Companions to navigate between Gatekeepers, Town Squares, Merchants, and Quest NPCs without colliding with buildings or terrain.

#### Scenario: Bot navega da Gatekeeper até a Loja de Consumíveis em Gludio
- **GIVEN** o companheiro IA está localizado na Gatekeeper de Gludio
- **WHEN** o sistema solicita a ida do bot até a Grocery Trader
- **THEN** o bot percorre a malha de waypoints da cidade passo a passo, usando pathfinding para contornar obstáculos e chegando a menos de 150 unidades da Grocery Trader.

### Requirement: Execução Autônoma de Compras (BuyList Executing Engine)
The system MUST allow AI Companions to target Merchant NPCs (`L2MerchantInstance`), inspect `L2TradeList` items, verify Adena balances, and execute purchase operations (`doBuy`) for required consumables (Soulshots, Health Potions, Scrolls of Escape).

#### Scenario: Reabastecimento automático de Soulshots ao ficar sem suprimentos
- **GIVEN** o inventário do bot possui menos de 100 Soulshots e Adena suficiente
- **WHEN** o bot atinge a zona da cidade ou o comando de descanso/cidade é ativado
- **THEN** o bot caminha até o NPC Trader de armas/artigos gerais, compra o pacote de Soulshots via `doBuy`, adiciona ao inventário e ativa a auto-shot.

### Requirement: Navegação e Execução Autônoma de Quests (Quest Navigator)
The system MUST link `LLMQuestDialogExecutor` with the waypoint navigation system, enabling AI Companions to travel across town and field locations to interact with Quest NPCs, execute dialog bypasses, and advance quest conditions (`cond`).

#### Scenario: Deslocamento e interação autônoma com NPC de Quest
- **GIVEN** o bot precisa entregar ou iniciar uma etapa de Quest com um NPC em Gludio
- **WHEN** o `LLMQuestNavigator` ativa a rota para a Quest
- **THEN** o bot navega até as coordenadas do NPC, aciona `talkToQuestNpc`, executa o bypass correto e avança o estado da Quest.
