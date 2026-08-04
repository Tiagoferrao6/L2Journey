# gm-control-panel Specification

## Purpose
Specification for the Embedded Web API & Live GM Control Panel for L2Journey GameServer.

## Requirements

### Requirement: Autenticação de Administrador GM
The system SHALL require a Bearer token matching `WebAdminToken` in `server.ini` for all HTTP endpoints under `/api/admin/*`.

#### Scenario: Acesso Não Autorizado
- **WHEN** a client sends a request to `/api/admin/rates` without a valid Bearer token
- **THEN** the system returns HTTP 401 Unauthorized.

### Requirement: Hot-Reload de Rates do Servidor
The system SHALL update server EXP, SP, Adena, and Drop rates in RAM immediately when modified through POST `/api/admin/rates`.

#### Scenario: Alteração de Rate de EXP
- **WHEN** an authenticated GM submits a POST request to `/api/admin/rates` with `{"rateXp": 10.0}`
- **THEN** the system updates `Config.RATE_XP` in RAM immediately and returns HTTP 200 OK.

### Requirement: Controle do Módulo FakePlayers e Alt+B
The system SHALL support toggling fake player modules and reloading XML configuration templates for Fake Traders, Fake Hunters, and Alt+B Community Board shops via POST `/api/admin/reload`.

#### Scenario: Reload de XML de Lojas
- **WHEN** an authenticated GM requests POST `/api/admin/reload` with `{"target": "fake_traders"}`
- **THEN** the system re-parses `fake_traders_spawns.xml` and updates active trader instances without restarting the server.

### Requirement: Feed do Chat e Monitor de Players Reais
The system SHALL capture recent in-game chat messages (Global, Trade, Shout, PM) and provide a detailed list of active human players via GET `/api/admin/chat` and GET `/api/admin/players`.

#### Scenario: Monitoramento de Chat
- **WHEN** a player sends a message in the Global or Trade chat channel
- **THEN** the system records the message into the live web chat stream buffer.

### Requirement: Endpoint de Inspeção Detalhada de FakePlayers
The system MUST provide a REST API endpoint (`GET /api/admin/fakeplayers/{name}`) returning complete character state including CP, paperdoll equipment, active buffs, and inventory items.

#### Scenario: GM solicita inspeção de um FakeHunter ativo
- **GIVEN** o bot "DespairArcher" está ativo e caçando
- **WHEN** o administrador envia requisição GET para `/api/admin/fakeplayers/DespairArcher` com cabeçalho de autenticação GM
- **THEN** a resposta JSON inclui HP/MP/CP numéricos, lista de itens do inventário, equipados e a lista de buffs ativos.

### Requirement: Modal de Inspeção no Dashboard Web Frontend
The system MUST render an interactive inspector modal on the web frontend when a GM clicks on a bot row in the FakePlayer administration table.

#### Scenario: GM clica em um bot no dashboard web
- **GIVEN** o administrador está visualizando a aba "Fake Players" no dashboard web
- **WHEN** o administrador clica na linha do bot "DespairArcher"
- **THEN** um modal exibe o inventário completo, lista de buffs com tempo restante e equipamentos atuais.

### Requirement: Chat Feed ao Vivo com Localização e Filtro por Região
O terminal de chat no Dashboard Web de GM MUST exibir a localização detalhada do remetente (Nome da Região e Coordenadas X, Y, Z) e MUST oferecer um filtro dinâmico por região.

#### Scenario: Visualizar localização de remetente no chat
- **GIVEN** o administrador está autenticado no Dashboard GM
- **WHEN** novas mensagens são recebidas via `/api/admin/chat`
- **THEN** cada linha do chat MUST exibir `(@NomeDaRegiao [X, Y, Z])` junto ao nome do jogador.

#### Scenario: Filtrar mensagens por região
- **GIVEN** o chat contém mensagens de múltiplas zonas (ex: "Gludio Town", "Giran Castle Town")
- **WHEN** o administrador seleciona uma região no dropdown "Filtro de Região"
- **THEN** o terminal MUST filtrar e exibir apenas as mensagens originadas naquela região.
