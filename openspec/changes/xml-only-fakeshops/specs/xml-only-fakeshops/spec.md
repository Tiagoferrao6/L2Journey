## ADDED Requirements

### Requirement: XML-Only FakeShops Initialization
O sistema SHALL carregar Lojas Falsas Estáticas (FakeShops e Traders) exclusivamente a partir do arquivo XML legado (`fake_shops.xml`). A inicialização de instâncias estáticas por tabelas SQL não é permitida.

#### Scenario: Sistema inicializa FakeShops a partir do XML
- **WHEN** o servidor de jogo é iniciado e `Config.FAKE_SHOPS_ENABLED` é true
- **THEN** o `FakePlayerManager` carrega as instâncias (ex: Gimli, Thorin) apenas do arquivo XML via `FakeShopData.getInstance()` e ignora criação genérica no SQL.

### Requirement: FakeHunters SQL Filter
Quando o `FakePlayerManager` ativar zonas (ex: Gludio), ele SHALL ler o Banco de Dados `fake_players_profiles` e filtrar para carregar e iniciar apenas os perfis com `bot_type` igual a `HUNTER`.

#### Scenario: FakePlayerManager filtra apenas Hunters
- **WHEN** uma zona fica ativa (ex: Gludio é populada)
- **THEN** o gerenciador consulta a tabela e spawna exclusivamente os Bots do tipo `HUNTER`, ignorando registros corrompidos/legados com `bot_type = 'TRADER'`.
