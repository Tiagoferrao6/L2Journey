## Why

A base arquitetural do `fake-traders-engine` está completa (Ghost Objects, FakePlayer base e FakeTraderManager), e os arquivos XML de configuração já foram criados. O que falta é a "Ponte" entre os arquivos XML e o servidor. Precisamos criar os Parsers (Leitores de XML) em Java e injetá-los no boot do servidor (`GameServer.java`) para que os bots de fato ganhem vida no mapa quando o servidor for ligado.

## What Changes

1. **FakeTradersEconomyParser**: Uma classe singleton que usa `DocumentBuilderFactory` para ler o `fake_traders_economy.xml`. Ela vai extrair as tags `<marketProfile>` e armazenar as faixas de preços e quantidades na memória RAM.
2. **FakeTradersSpawnParser**: Uma classe singleton que lerá o `fake_traders_spawns.xml`. Para cada tag `<spawn>`, ela vai:
   - Sortear um nome da lista `<names>`.
   - Adicionar o nome aos Restritos no `FakeTraderManager`.
   - Instanciar o `FakePlayer` com a aparência correta e o link para o profile de economia.
   - Executar o `spawnMe()` nas coordenadas exatas e acionar os métodos `setupSellStore()` / `setupBuyStore()`.
3. **Injeção no Boot**: Modificar o `GameServer.java` para inicializar esses parsers logo após o carregamento da base de dados e dos NPCs convencionais.

## Capabilities

### New Capabilities
- `fake-traders-xml-parsers`: Classes de leitura de DOM XML e estruturas de dados em memória (Data Holders) para suportar a economia dinâmica dos bots.

### Modified Capabilities
- `gameserver-boot-sequence`: Alteração no fluxo de inicialização do L2J.

## Impact

- **Database**: Nenhum.
- **Boot Time**: O servidor demorará alguns milissegundos a mais para ligar, pois estará lendo os 2 novos arquivos XML.
- **Server Core**: `GameServer.java` será alterado (1 linha por parser).
