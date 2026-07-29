## ADDED Requirements

### Requirement: Sobrescrita de Configurações (Overrides)
The system SHALL allow individual bot profiles in XML to override global configuration parameters from `fakeplayers.ini`.

#### Scenario: Ativação de Agressividade Individual
- **WHEN** a regra global define `FakePlayerAggroPlayers = False`
- **THEN** e o perfil individual do bot define o override `{"AggroPlayers": true}`
- **THEN** aquele bot específico atacará jogadores reais na sua área de visão, ignorando o comportamento global
