# Co-op Companion Specification

## ADDED Requirements

### Requirement: Tomada de Decisão Cognitiva por Motor LLM (LLM Decision Planner Engine)
The system MUST provide a cognitive decision planner engine (`LLMTankerPlannerEngine`) that sends game state snapshots to the LLM (Qwen/Ollama) and parses structured JSON action responses to direct the Tanker companion's actions (hunting, shopping, class quests).

#### Scenario: LLM decide iniciar a Quest de Mudança de Classe ao aproximar-se do Nível 20
- **GIVEN** o bot Tanker `PaladinBot` atinge o Nível 19.5 e possui menos de 100 Soulshots
- **WHEN** o `LLMTankerPlannerEngine` gera o snapshot e consulta o motor de LLM
- **THEN** a LLM retorna a decisão de navegar até a Igreja de Gludio, repor suprimentos e iniciar a quest `Q001_PathToKnight`.

### Requirement: Consulta de Dados do Jogo por Ferramentas de IA (Game Data Tool Queries)
The system MUST expose game data lookup helpers (`LLMGameDataTools`) allowing the LLM engine to query mob drop lists (`NpcData`/`DropHolder`), NPC spawn coordinates (`SpawnData`), and item information (`ItemData`) on demand.

#### Scenario: LLM consulta o local de drop de itens de craft ou quest
- **GIVEN** a LLM precisa determinar a localização de mobs que dropam o item de quest "Knight Medallion"
- **WHEN** a LLM invoca a consulta `get_mob_drops("Knight Medallion")`
- **THEN** o sistema retorna as estatísticas de drop, IDs dos mobs e coordenadas das zonas de spawn.

### Requirement: Ciclo Completo de Progressão Autônoma do Nível 1 ao 40
The system MUST execute a fully autonomous leveling loop from Level 1 to 40 for the Tanker companion, orchestrating PvE combat, town supply replenishment, waypoint navigation, and class promotions (Human Fighter -> Knight -> Paladin).

#### Scenario: Promoção autônoma para Knight e equipagem de Grade D
- **GIVEN** o bot concluiu a quest de classe de Knight
- **WHEN** o `LLMClassChangeManager` executa a mudança de classe
- **THEN** a classe do bot é alterada para Knight (Class ID 9), o conjunto D-Grade é equipado e o bot retorna à zona de caça de Nível 20+.
