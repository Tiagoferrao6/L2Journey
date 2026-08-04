# Co-op Companion Specification

## Purpose
Specification for the Autonomous AI Partner Engine (Co-op Companion) for L2Journey GameServer.
## Requirements
### Requirement: Transição de Estado baseada na Conexão do Jogador Humano
The system MUST detect login and logout events of the designated human player ("Tiago") and transition the AI Companion between `ACTIVE_COOP`, `ASSIGNED_MISSION`, and `AUTONOMOUS_SOLO` modes.

#### Scenario: Jogador humano loga no jogo
- **GIVEN** o companheiro IA está no modo `AUTONOMOUS_SOLO`
- **WHEN** o jogador humano "Tiago" efetua login no servidor
- **THEN** o sistema envia uma mensagem privada do bot cumprimentando o jogador humano e altera o estado para `ACTIVE_COOP`.

#### Scenario: Jogador humano envia missão antes de deslogar
- **GIVEN** o jogador humano está em diálogo de chat com o companheiro IA
- **WHEN** o jogador humano envia o comando "Farm Varnish em Abandoned Camp" e desloga
- **THEN** o bot salva a missão na tabela `companion_active_missions` e transita para o modo `ASSIGNED_MISSION`.

### Requirement: Progressão Autônoma de Nível 1 ao 40 e Mudança de Classe
The system MUST allow the AI Companion to farm PvE, sell loot directly to NPC merchants, and complete the 1st (Knight) and 2nd (Paladin) Class Transfer quests.

#### Scenario: Realização da 2nd Class Transfer no Nível 40
- **GIVEN** o bot atinge o Nível 40
- **WHEN** o bot possui os itens das provas de classe de Paladin
- **THEN** o bot interage com o Grand Master NPC e efetua a mudança de classe para Paladin.

### Requirement: Respeito ao Dismiss de Party e Entrada sob Demanda
The system MUST NOT automatically add the AI Companion back into the human player's party when dismissed, and MUST only join the party upon explicit whisper request ("party") or direct party invite.

#### Scenario: Jogador remove o bot da party
- **GIVEN** o bot está na party em modo `ACTIVE_COOP`
- **WHEN** o jogador executa o Dismiss da party
- **THEN** o bot altera o estado para `AUTONOMOUS_SOLO` e permanece fora da party.

### Requirement: Acompanhamento Natural a Pé
The system MUST command the bot to follow the player using walking/running movement (`Intention.MOVE_TO`) for short/medium distances and MUST reserve teleportation for distances greater than 3000 units.

#### Scenario: Acompanhar jogador a pé em curta distância
- **GIVEN** o companheiro IA está a 500 unidades do líder da party
- **WHEN** o líder se movimenta pela área
- **THEN** o bot caminha a pé até a posição do líder sem se teleportar.

### Requirement: Comandos Táticos de Combate por PM
The system MUST support PM (whisper) tactical commands: `caca` (active hunting), `assist` (attack leader's target), `rest` (sit/recover), and `town` (teleport to Gludio town).

#### Scenario: Comando Assist em Combate
- **GIVEN** o jogador humano está focando o monstro "Keltir"
- **WHEN** o jogador envia a mensagem "assist" via PM para o bot
- **THEN** o bot altera seu alvo para "Keltir" e inicia o ataque.

#### Scenario: Comando Rest
- **WHEN** o jogador envia a mensagem "rest" via PM para o bot
- **THEN** o bot senta e entra em estado de recuperação de vida e mana.

#### Scenario: Comando Town
- **WHEN** o jogador envia a mensagem "town" via PM para o bot
- **THEN** o bot teleporta para a zona segura da vila de Gludio.

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

