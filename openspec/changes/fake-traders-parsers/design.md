## Architecture

1. **Pacotes**: As classes serão criadas em `com.l2journey.gameserver.data.xml.impl` (ou similar) seguindo o padrão do L2J.
2. **Bibliotecas de Parsing**: Usaremos a org.w3c.dom.* (DocumentBuilderFactory), padrão no parser de XML nativo do L2J.
3. **Injeção**: As invocações no `GameServer.java` serão colocadas logo após a inicialização dos NPCs, pois os FakePlayers precisam que o mundo e o Geodata já estejam carregados para chamarem o método `spawnMe(x, y, z)`.

## Data Structures

- `EconomyProfile` (Classe): Conterá o ID (ex: `basic_mats_seller`) e uma lista de `EconomyItem` (itemId, minPrice, maxPrice, minQty, maxQty).
- O `FakeTradersEconomyParser` terá um `Map<String, EconomyProfile> _profiles`.

## Edge Cases

- **Validação de Tags**: Se o parser encontrar uma tag vazia ou atributo ausente, ele deve logar um Warning e ignorar o bot, sem crashar o Boot do servidor inteiro.
- **Ordem de Inicialização**: É imperativo que o `FakeTradersEconomyParser.getInstance().load()` seja chamado ANTES do `FakeTradersSpawnParser.getInstance().load()`, pois os bots tentam ler seu perfil de economia na hora em que nascem.
