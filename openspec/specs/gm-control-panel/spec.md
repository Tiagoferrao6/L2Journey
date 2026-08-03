# gm-control-panel Specification

## Purpose
TBD - created by archiving change real-time-web-dashboard. Update Purpose after archive.
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

