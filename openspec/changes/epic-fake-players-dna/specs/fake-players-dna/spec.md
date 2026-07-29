## ADDED Requirements

### Requirement: Perfis Psicológicos (Matriz de DNA)
The system SHALL allow configuration of behavioral traits (0-100 for Preservation, Sociability, Greed, Aggressiveness, Altruism) per bot via XML.

#### Scenario: Avaliação de Preservação
- **WHEN** o HP de um bot cai abaixo de 20%
- **THEN** o sistema verifica o seu valor de preservacao
- **THEN** se for alto (>80), ele executa um Scroll of Escape
- **THEN** se for baixo (<20), ele luta até a morte

### Requirement: Agendamento de Atividade (Turnos e Shifts)
The system SHALL manage bot life cycles based on scheduled shifts and session durations via XML.

#### Scenario: Troca de Turno (Despawn)
- **WHEN** o relógio central atinge o horário de término do turno de um bot
- **THEN** o FakeHunterManager agenda o despawn do bot com jitter aleatório

#### Scenario: Troca de Turno (Spawn)
- **WHEN** o relógio central atinge o horário de início do turno de um bot
- **THEN** o FakeHunterManager realiza o spawn do bot nos spots pré-determinados
