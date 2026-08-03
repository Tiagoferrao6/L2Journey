## Why

O sistema de Fake Players atual (PoC) carece de profundidade. Para criar um "Mundo Vivo" (Living World) convincente, precisamos que os bots de combate (Hunters/Fighters) tenham identidades individuais, perfis psicológicos (DNA de 0 a 100), e horários de atividade (Turnos) que ditarão seu comportamento no mapa.
No entanto, após a implementação bem-sucedida do `fake-traders-engine`, aprendemos que salvar bots no Banco de Dados causa inchaço (bloat) e riscos de segurança. Portanto, a arquitetura deve ser adaptada para usar **Ghost Objects** (bots instanciados apenas em RAM, bloqueados de escrita no banco) geridos de forma modular.

## What Changes

- **FakeHunterManager (Módulo Dedicado)**: Um gerenciador separado exclusivo para bots de combate, rodando sua própria ThreadPool otimizada para movimento e IA, não interferindo no `FakeTraderManager`.
- **Motor XML de DNA e Spawns**: Remoção total da ideia de usar a tabela SQL `fake_players_profiles`. Todo o DNA (Agressividade, Preservação, Sociabilidade, Ganância) e horários de atividade (Turnos) serão lidos de `fake_hunters_dna.xml` e `fake_hunters_spawns.xml`.
- **Arquitetura Ghost Object**: Os Hunters usarão a classe `FakePlayer extends Player`, sendo estritamente objetos efêmeros de RAM, imunes a exploits de persistência de banco de dados.
- **Toggles no `.ini`**: Adição de configs booleanas para o GM ativar/desativar `EnableFakeTraders` e `EnableFakeHunters` de maneira independente.
- **Nova Regra de Drop**: Fake Players só droparão itens se o seu status atual for PK (Player Killer / Karma > 0).

## Capabilities

### New Capabilities
- `fake-hunters-engine`: A infraestrutura de traços de comportamento (0-100) e rotinas lidas via XML, gerenciada pelo novo `FakeHunterManager`.
- `fake-players-toggles`: Separação dos módulos de bots no `server.ini` para administração independente.

### Modified Capabilities
- `fake-players-poc`: Mudança na regra global de drop de bots (apenas PK).

## Impact

- **Banco de Dados**: Nenhum impacto. A ideia de persistência em DB foi abortada em favor da Arquitetura de Ghost Objects.
- **Gerenciamento Core**: Adição do `FakeHunterManager` com um Relógio Central (Tick Engine rápido) para IA e Turnos.
- **Mecânicas Core (Drops e PvP)**: Integração profunda com as mecânicas de Karma e PK do servidor para condicionar a tabela de drop dos bots.
