# Spec: GM Control Panel Inspector

## ADDED Requirements

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
