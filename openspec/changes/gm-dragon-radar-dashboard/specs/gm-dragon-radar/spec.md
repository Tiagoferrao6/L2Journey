## ADDED Requirements

### Requirement: REST Endpoint para Telemetria de Radar
O servidor WebAPI SHALL disponibilizar o endpoint `GET /api/admin/radar` que aceita os parâmetros `botName` e `radius` (500, 2500, 5000), retornando a localização precisa do bot e de todas as criaturas no raio.

#### Scenario: Consulta de radar bem-sucedida
- **WHEN** o administrador envia uma requisição `GET /api/admin/radar?botName=PaladinBot&radius=2500`
- **THEN** o servidor responde com HTTP 200 contendo o JSON com o bot central $(X,Y,Z)$, heading e a lista de entidades visíveis catalogadas por tipo (MOB, PLAYER, NPC, PARTY).

### Requirement: Interface Dragon Radar Retro
O painel GM SHALL renderizar um radar circular em HTML5 Canvas imitando a estética retrô verde CRT (grid quadriculada, brilho fluorescente e botões de moldura metálica).

#### Scenario: Visualização do Bot e Entidades no Radar
- **WHEN** o usuário seleciona um bot (`PaladinBot`, `HawkeyeBot` ou `BishopBot`) no painel web
- **THEN** o radar exibe um triângulo vermelho no centro indicando a orientação do bot e pontos luminosos coloridos para cada entidade próxima dentro do raio de zoom configurado.

### Requirement: Seleção Multi-Zoom (500, 2500, 5000)
A interface do Dragon Radar SHALL disponibilizar seletores de zoom para alternar entre as escalas de 500, 2500 e 5000 unidades do jogo.

#### Scenario: Alternância de escala de zoom
- **WHEN** o administrador clica no botão de zoom `500`
- **THEN** o radar ajusta a projeção das coordenadas $\Delta X, \Delta Y$ para focar na área imediata de 500 unidades ao redor do bot.
