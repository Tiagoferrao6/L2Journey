## Why

O L2Journey está sendo adaptado para um ecossistema Single Player autossuficiente. Para validar a arquitetura de IA dos bots e medir o impacto no Game Server sem sobrecarga, precisamos criar uma Prova de Conceito (PoC) com um laboratório controlado em Gludio contendo exatamente 10 bots com persistência de dados.

## What Changes

- Criação de uma tabela SQL para persistência do DNA dos bots.
- Otimização do Zone Listener para que os bots apenas sejam processados quando o jogador principal estiver em Gludio ou zonas adjacentes.
- Configuração de cronogramas curtos para testes acelerados (1 hora de inventário, turnos curtos de caça).
- Povoamento específico de 10 bots (2 traders e 8 hunters em localizações designadas).

## Capabilities

### New Capabilities
- `fake-players-poc`: Prova de conceito do sistema de bots com persistência, otimização de zonas e testes acelerados.

### Modified Capabilities

## Impact

- **SQL**: Nova tabela `fake_players_profiles`.
- **Game Server**: Implementação de lógica de Zone Listener e schedules de teste.
- **Documentação**: Atualização do `/docs/SPEC.md` refletindo os requisitos.
