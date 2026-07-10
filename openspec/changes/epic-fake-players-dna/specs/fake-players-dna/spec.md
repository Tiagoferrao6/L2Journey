## ADDED Requirements

### Requirement: Perfis Psicológicos (Matriz de DNA)
O sistema DEVE permitir a configuração de valores de 0 a 100 para traços comportamentais (Agressividade, Preservação, Sociabilidade, Ganância, Rancor, Altruísmo) por bot.

#### Scenario: Avaliação de Preservação
- **WHEN** o HP de um bot cai abaixo de 20%
- **THEN** o sistema verifica o seu valor de `preservacao`
- **THEN** se for alto (>80), ele executa um Scroll of Escape
- **THEN** se for baixo (<20), ele luta até a morte

### Requirement: Agendamento de Atividade (Turnos e Vício)
O sistema DEVE gerenciar o ciclo de vida dos bots com base em turnos agendados e durações de sessão (vício).

#### Scenario: Troca de Turno (Despawn)
- **WHEN** o relógio central atinge o horário de término do turno de um bot (ex: fim do `PRIME_TIME`)
- **THEN** o `FakePlayerManager` agenda o despawn do bot
- **THEN** o bot utiliza um método de saída orgânica (ex: SoE para cidade ou deslogar em área segura)

#### Scenario: Troca de Turno (Spawn)
- **WHEN** o relógio central atinge o horário de início do turno de um bot (ex: início do `MORNING`)
- **THEN** o `FakePlayerManager` realiza o spawn do bot nos spots pré-determinados
