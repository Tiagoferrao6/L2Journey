# quest-solver Specification

## Purpose
TBD - created by archiving change llm-agent-quest-solver. Update Purpose after archive.
## Requirements
### Requirement: Leitura do Estado de Quests do Personagem
The system MUST serialize active quest states, quest items, and quest targets into JSON for consumption by the LLM planner.

#### Scenario: Leitura de Progresso na Quest de Warrior
- **GIVEN** o bot está na quest "Path of the Warrior" e possui 5 de 10 "Medals of Tracker"
- **WHEN** a LLM solicita a percepção do estado da quest
- **THEN** o sistema envia JSON informando que restam 5 Medals em Abandoned Camp.

### Requirement: Mudança de Classe Automática no Nível 20
The system MUST automatically trigger the class change dialog with the High Priest / Master NPC upon presentation of the 1st Class Transfer quest token.

#### Scenario: Conclusão de Mudança de Classe
- **GIVEN** o bot possui a medalha de conclusão da quest no nível 20
- **WHEN** o bot interage com o Master NPC
- **THEN** a classe do personagem é alterada de Human Fighter para Warrior.

