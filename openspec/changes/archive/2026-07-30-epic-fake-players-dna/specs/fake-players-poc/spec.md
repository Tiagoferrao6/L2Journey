## MODIFIED Requirements

### Requirement: Ciclos de Teste Acelerado
The system SHALL use the central clock engine for turn transitions instead of accelerated fixed schedules.

#### Scenario: Renovação Rápida e Turnos
- **WHEN** o relógio central executa o tick
- **THEN** ele renova o estado e verifica início/término de turno do bot

## ADDED Requirements

### Requirement: Restrição de Drops baseada em Karma
The system SHALL drop items from fake players only if their karma status indicates Player Killer (PK).

#### Scenario: Drop em Morte
- **WHEN** um Fake Player morre
- **THEN** o sistema verifica seu Karma atual
- **THEN** se Karma > 0 (PK), ele aplica a tabela de drop punitiva de PK
- **THEN** se Karma == 0, ele não dropa nenhum item
