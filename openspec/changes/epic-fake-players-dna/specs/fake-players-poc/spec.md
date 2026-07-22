## MODIFIED Requirements

### Requirement: Ciclos de Teste Acelerado
Os bots DEVEM usar o Relógio Central em vez de schedules acelerados fixos.

#### Scenario: Renovação Rápida e Turnos
- **WHEN** um trader realiza seu ciclo
- **THEN** ele renova seu inventário através do Relógio Central (baseado no seu Turno de atividade)
- **WHEN** um hunter tem que avaliar sua saída
- **THEN** ele executa o despawn quando o seu turno programado termina

## ADDED Requirements

### Requirement: Restrição de Drops baseada em Karma
Os jogadores falsos DEVEM dropar itens apenas se o sistema identificá-los como Player Killers.

#### Scenario: Drop em Morte
- **WHEN** um Fake Player morre
- **THEN** o sistema verifica seu Karma atual
- **THEN** se Karma > 0 (PK), ele aplica a tabela de drop punitiva de PK
- **THEN** se Karma == 0, ele não dropa nenhum item
