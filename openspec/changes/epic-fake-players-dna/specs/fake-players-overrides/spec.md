## ADDED Requirements

### Requirement: Sobrescrita de Configurações (Overrides)
O sistema DEVE permitir que configurações individuais no banco de dados sobrescrevam as configurações globais do arquivo `fakeplayers.ini`.

#### Scenario: Ativação de Agressividade Individual
- **WHEN** a regra global define `FakePlayerAggroPlayers = False`
- **THEN** e o perfil individual do bot define o override `{"AggroPlayers": true}`
- **THEN** aquele bot específico atacará jogadores reais na sua área de visão, ignorando o comportamento global
