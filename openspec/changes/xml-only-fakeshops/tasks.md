## 1. FakePlayerManager Cleanup

- [x] 1.1 Remover o método `initGludioProfilesIfEmpty()` de `FakePlayerManager.java` e apagar sua respectiva chamada no construtor
- [x] 1.2 Atualizar `spawnGludioBotsForActiveSchedule()` em `FakePlayerManager.java` para não processar mais `TRADER` vindos da tabela SQL

## 2. Database Cleanup

- [x] 2.1 Fornecer um script SQL (ou executar via bash) para deletar os registros `TRADER` ou `HUNTER` genéricos da tabela `fake_players_profiles` caso o administrador queira limpar os clones
