## 1. Atualização do Banco de Dados

- [x] 1.1 Adicionar novas colunas numéricas de 0-100 para o DNA (`preservacao`, `sociabilidade`, `ganancia`, `rancor`, `altruismo`) na tabela `fake_players_profiles`.
- [x] 1.2 Adicionar a coluna TEXT/JSON para `behavior_overrides` na tabela.
- [x] 1.3 Adicionar coluna para Turno (`MORNING`, `PRIME_TIME`, etc.) e Duração/Vício (0-100).
- [x] 1.4 Modificar o DTO e DAO em Java para ler as novas colunas e o objeto de overrides.

## 2. Refatoração do Motor (Relógio e Turnos)

- [x] 2.1 Modificar `FakePlayerManager` para criar o "Relógio Central" usando `ThreadPool.scheduleAtFixedRate`.
- [x] 2.2 Implementar a checagem de Turnos no relógio central: identificar bots que devem iniciar e bots que devem deslogar.
- [x] 2.3 Aplicar jitter (delay aleatório) para despawns e spawns na transição do turno para evitar lag no servidor.

## 3. Implementação de Regras Globais e Individuais (Overrides)

- [x] 3.1 Integrar a leitura do `.ini` global (`fakeplayers.ini`) nas decisões do AI do bot.
- [x] 3.2 Interceptar a leitura da regra no AI para verificar se o bot atual possui um JSON de Override válido que substitua o valor global.

## 4. Regra de PK e Drop

- [x] 4.1 Modificar o método de `onDie` / `doDie` dos Fake Players.
- [x] 4.2 Adicionar verificação condicional: só processar a tabela de loot/drop se `getKarma() > 0`.
