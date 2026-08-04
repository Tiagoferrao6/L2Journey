# GM Control Panel Delta Specification

## MODIFIED Requirements

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
