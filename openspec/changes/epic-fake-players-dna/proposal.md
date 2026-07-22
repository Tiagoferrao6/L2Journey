## Why

O sistema de Fake Players atual (PoC) funciona bem como um motor básico de spawn/despawn, mas os bots carecem de profundidade, agindo apenas como uma massa genérica controlada por regras globais (`fakeplayers.ini`). Para criar um "Mundo Vivo" (Living World) convincente, precisamos que os bots tenham identidades individuais, perfis psicológicos (DNA de 0 a 100), e horários de atividade (Turnos) que ditarão seu comportamento no mapa e na economia, além de um sistema de memória relacional (rancor, medo, amizade) e substituição de regras padrão (Overrides).

## What Changes

- **Perfis Específicos (Overrides)**: Adição de nomes e regras específicas na tabela de DB (`fake_players_profiles`) que cancelam as configurações globais do `.ini` para aquele bot.
- **Matriz de DNA**: Expansão do perfil para incluir características de 0 a 100 (Agressividade, Preservação, Sociabilidade, Ganância, Rancor, Altruísmo).
- **Tempo de Atividade (Uptime & Shifts)**: Introdução de 'Turnos' (`MORNING`, `PRIME_TIME`, etc.) e 'Vício' (0-100) para determinar quando o bot loga e desloga, criando flutuações populacionais baseadas na hora do servidor.
- **Nova Regra de Drop**: Fake Players genéricos ou específicos só droparão itens se o seu status atual for PK (Player Killer / Karma > 0).

## Capabilities

### New Capabilities
- `fake-players-dna`: A nova infraestrutura de traços de comportamento (0-100) e rotinas (Turnos) no banco de dados e no Core Manager.
- `fake-players-overrides`: Sistema de sobrescrita de propriedades (ex: ignorar a configuração global de `FakePlayerAggroPlayers` em bots específicos).

### Modified Capabilities
- `fake-players-poc`: Mudança na regra global para atrelar os drops dos bots exclusivamente ao estado de PK. Modifica o spawn inicial para respeitar os Turnos em vez de spawnar todos em Gludio instantaneamente.

## Impact

- **Banco de Dados**: Atualização estrutural profunda na tabela `fake_players_profiles` para suportar novas colunas ou um campo JSON de overrides, e estado de relação (`fake_player_relations`).
- **FakePlayerManager**: Deverá implementar um Relógio Central (Tick Engine) responsável por verificar os turnos e orquestrar Spawns/Despawns dinamicamente, além de ler os overrides.
- **Mecânicas Core (Drops e PvP)**: Integração profunda com as mecânicas de Karma e PK do servidor para condicionar a tabela de drop dos bots.
