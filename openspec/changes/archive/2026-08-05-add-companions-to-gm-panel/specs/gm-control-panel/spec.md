## MODIFIED Requirements

### Requirement: Endpoint de Inspeção Detalhada de FakePlayers
The system MUST provide a REST API endpoint (`GET /api/admin/fakeplayers/{name}`) and summary list (`GET /api/admin/fakeplayers`) returning complete character state including CP, paperdoll equipment, active buffs, and inventory items for Hunters, Traders, and AI Companions (`COMPANION`).

#### Scenario: GM solicita inspeção de um AI Companion ativo
- **GIVEN** o bot companion "PaladinBot" está ativo em modo autônomo ou coop
- **WHEN** o administrador envia requisição GET para `/api/admin/fakeplayers/PaladinBot` com cabeçalho de autenticação GM
- **THEN** a resposta JSON inclui o tipo `"COMPANION"`, HP/MP/CP numéricos, posição de localização, lista de itens do inventário, equipados e buffs ativos.

#### Scenario: Listagem consolidada de FakePlayers incluindo Companions
- **GIVEN** existem FakeHunters, FakeTraders e AI Companions online
- **WHEN** o administrador solicita `GET /api/admin/fakeplayers`
- **THEN** o sistema retorna a lista consolidada contendo bots do tipo `"HUNTER"`, `"TRADER"` e `"COMPANION"`.
