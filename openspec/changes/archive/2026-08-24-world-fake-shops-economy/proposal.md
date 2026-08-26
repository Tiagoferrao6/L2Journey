## Why

Para ajustar a distribuição econômica do L2Journey aos diferentes níveis de evolução dos jogadores, estruturamos Gludio e Dion como mercados de transição (15 lojas em cada uma para C e D Grade), Giran como o grande mega centro comercial (50 lojas cobrindo do Grade C ao S, incluindo sets e itens encantados) e Aden como a capital suprema de elite (20 lojas focadas em S80, S84, Pedras de Elemento, Enchants S e Life Stones).

## What Changes

- **Gludio & Dion (15 Lojas cada - Transição C e D Grade)**:
  - 15 FakeShops em Gludio e 15 FakeShops em Dion oferecendo armas, armaduras, joias, shots, enchants e consumíveis dos graus D e C.
- **Giran Town (50 Lojas - Mega Hub de C a S Grade com Encantados)**:
  - 50 FakeShops com variedade massiva cobrindo do Grade C até o Grade S, incluindo armas e sets encantados (+4 a +8), matérias-primas refinadas, oficinas e compradores.
- **Aden Castle Town (20 Lojas - Elite S80 / S84 High Five)**:
  - 20 FakeShops de elite focados em **S80 (Dynasty, Icarus) e S84 (Vesper Noble, Vorpal, Elegy)** com encantamentos (+4 a +8), Pedras de Atributo Elementar (Attribute Stones/Crystals), Enchants Blessed S, Top Life Stones Lvl 80/84.
- **Cidades Secundárias (5 Lojas cada)**:
  - Oren, Hunters Village, Heine, Goddard, Rune e Schuttgart com 5 lojas de suprimentos e materiais primários.
- **Contas Persistentes e Validação SQL**: Script automatizado de inserção de contas e personagens para todas as capitais no MySQL.

## Capabilities

### New Capabilities
- `world-fake-shops-economy`: Sistema global de economia estruturado (15 Lojas em Gludio/Dion para C/D Grade, 50 Lojas em Giran para C-S Grade encantados, 20 Lojas em Aden para S80/S84 e elementos, e 5 Lojas nas cidades secundárias).

### Modified Capabilities
- Nenhuma capacidade existente teve seus requisitos quebrados.

## Impact

- `com.l2journey.gameserver.data.xml.FakeShopData`: Leitura dos catálogos expandidos por cidade.
- `dist/game/data/fakeplayers/city_catalogs.xml` e `fake_shops.xml`: Atualização dos catálogos por grau e encantamentos.
- Banco de dados (`accounts`, `characters`): Inserção SQL das contas de todas as cidades.
