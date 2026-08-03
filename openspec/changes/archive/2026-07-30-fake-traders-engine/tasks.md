## 1. Engine Core (Classes base)
- [x] 1.1 Criar a classe `FakePlayer extends Player`. Implementar construtor básico para instanciar como player (sem conta de rede atrelada).
- [x] 1.2 Criar `FakeTraderManager` com o loop de verificação de turnos/atividade (`renewTime`).

## 2. Parsers XML (Motor Econômico)
- [x] 2.1 Criar `fake_traders_economy.xml` e seu parser `FakeTradersEconomyParser`. Suportar `<marketProfile>` (itens) e `<craftProfile>` (receitas).
- [x] 2.2 Criar `fake_traders_spawns.xml` e seu parser `FakeTradersSpawnParser`. Suportar localização, raça, classe, e link para o profile econômico.

## 3. Lógica de Lojas e Inventário
- [x] 3.1 Adicionar método `setupSellStore()` no `FakePlayer`: escolhe 1-3 itens do profile, randomiza preço e quantidade, adiciona ao inventário, configura a `TradeList` e senta.
- [x] 3.2 Adicionar método `setupBuyStore()` no `FakePlayer`: injeta 1 Bilhão de Adena no bot, configura a lista de compra, e senta.
- [x] 3.3 Adicionar método `setupCraftStore()` no `FakePlayer`: adiciona receitas ao `RecipeBook` do bot, configura taxa (Fee) de manufacture, e senta.
- [x] 3.4 Implementar a rotina de limpeza (`wipeState()`) para destruir inventário e adena no reset do turno.

## 4. Integração e Ajustes Finos
- [x] 4.1 Adicionar interceptação para restaurar MP do `FakePlayer` quando usado para CRAFT.
- [x] 4.2 Impedir que `FakePlayers` sejam atacados em zonas de paz, mas permitir inspeção normal.
- [x] 4.3 Sobrescrever `store()` e `storeCharBase()` no `FakePlayer` para bloquear escrita no banco de dados (Ghost Objects).
- [x] 4.4 Adicionar verificação de transação ativa (Renewal Collision) no loop do `FakeTraderManager` antes de resetar um bot.
- [x] 4.5 Injetar automaticamente os nomes dos bots carregados no `FakeTradersSpawnParser` para dentro da `CharNameTable` (Forbidden Names) no boot do servidor.
