## ADDED Requirements

### Requirement: Endpoints REST para Status do Servidor
The system SHALL expose HTTP GET endpoints providing real-time server metrics (Uptime, Real Players online, Fake Hunters online, Fake Traders online, RAM usage) in JSON format.

#### Scenario: Consulta de Status
- **WHEN** a client requests GET `/api/status`
- **THEN** the system returns a HTTP 200 JSON payload containing online player counts, uptime, and server health metrics.

### Requirement: Endpoints REST para Economia e Lojas
The system SHALL expose HTTP GET `/api/economy` listing active private stores, items on sale/buy, and town economic statistics in JSON format.

#### Scenario: Consulta de Economia
- **WHEN** a client requests GET `/api/economy`
- **THEN** the system returns active store details filtered by town and store type (SELL, BUY, CRAFT).

### Requirement: Caching de Respostas HTTP
The system SHALL cache HTTP response payloads for 2 seconds to prevent CPU spikes from rapid requests.

#### Scenario: Cache em Requisições Concorrentes
- **WHEN** multiple HTTP requests hit `/api/status` within a 2-second window
- **THEN** the system serves the cached response without recalculating world state statistics.
