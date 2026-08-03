# Proposal: Dashboard Web Chat Location Tracking & Region Filter

## Summary
Atualizar a interface do Dashboard Web GM (`dist/game/web/index.html`) para formatar e exibir as coordenadas (X, Y, Z) e a Região do mapa de cada mensagem do chat enviada pelo backend, além de adicionar um filtro seletor `<select>` por Região para filtrar mensagens em tempo real no terminal de chat.

## Motivation
O backend `WebAPIManager.java` (no handler `AdminChatHandler`) já serializa e envia em JSON os campos `x`, `y`, `z` e `regionName` de cada mensagem do chat. Porém, o frontend `dist/game/web/index.html` ignora esses campos e exibe apenas `[timestamp] [type] sender: text`. O Administrador/GM necessita dessa visualização geoespacial e filtragem por zona para monitorar atividades de players e bots no mundo em tempo real.

## Proposed Changes
- **Format Chat Message**: No `fetchChatStream()`, concatenar `(@RegionName [X, Y, Z])` ao remetente quando `regionName` e coordenadas estiverem presentes.
- **Region Selector Dropdown**: Adicionar elemento `<select id="chatRegionFilter">` na barra superior do card de chat.
- **Dynamic Region Collector & Filter**: Popular o dropdown com as regiões únicas detectadas nas mensagens e aplicar o filtro visualmente ao selecionar uma região.

## Verification
- Abrir `http://localhost:8080/`, logar no painel de GM e enviar mensagens ou aguardar logs de chat de players/bots.
- Confirmar que as coordenadas e nome da zona aparecem ao lado do nome do jogador.
- Selecionar uma região no filtro e verificar que apenas mensagens daquela região permanecem visíveis.
