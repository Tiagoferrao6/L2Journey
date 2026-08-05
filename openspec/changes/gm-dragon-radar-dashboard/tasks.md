## 1. Backend REST Endpoint

- [x] 1.1 Criar a classe `RadarHandler` em `WebAPIManager.java` para o contexto `/api/admin/radar`.
- [x] 1.2 Implementar a busca de entidades vizinhas via `World.getInstance().getVisibleObjectsInRange(bot, Creature.class, radius)`.
- [x] 1.3 Serializar posições $(X, Y, Z)$, heading, tipo de entidade (MOB, PLAYER, NPC, PARTY) e estado de agressividade em JSON.

## 2. Frontend Interface (Dragon Radar HTML5 Canvas)

- [x] 2.1 Criar a estrutura HTML/CSS retrô do Dragon Radar (moldura circular branca, botão metálico no topo e tela verde CRT).
- [x] 2.2 Implementar a lógica de desenho HTML5 Canvas com a grade quadriculada e o triângulo vermelho centralizado no bot ativo.
- [x] 2.3 Implementar a conversão matemática de coordenadas $\Delta X, \Delta Y$ para pixels do canvas nos níveis de zoom 500, 2500 e 5000.
- [x] 2.4 Renderizar blips luminosos coloridos para cada entidade próxima (Amarelo = Mob, Azul = Player, Dourado = Trio, Roxo = Mob Agressivo).

## 3. Testes & Integração

- [x] 3.1 Adicionar seletores de bot (`PaladinBot`, `HawkeyeBot`, `BishopBot`) e botões de zoom (500, 2500, 5000) no painel web.
- [x] 3.2 Testar a atualização em tempo real (1s polling) e validar a visualização durante o combate e movimentação do bot.
