## Context

O servidor L2Journey possui uma API REST embutida em `WebAPIManager.java` rodando na porta HTTP 8080. Atualmente, o painel do GM renderiza métricas estáticas e logs textuais de autorrecuperação dos bots `PaladinBot`, `HawkeyeBot` e `BishopBot`. Falta uma representação espacial em tempo real que permita ao GM visualizar a dinâmica de caça, combate e kiting em um mapa com estilo retrô "Dragon Radar".

## Goals / Non-Goals

**Goals:**
- Adicionar o endpoint REST `/api/admin/radar` no servidor Java para consultar entidades ao redor de um bot em um raio de até 5000 unidades.
- Criar a interface retrô Dragon Radar no painel web (HTML5 Canvas) com tela circular verde CRT, moldura branca metálica e grade quadriculada.
- Suportar seletores de zoom (500, 2500, 5000 unidades).
- Exibir bot central como triângulo vermelho com heading e entidades vizinhas como pontos brilhantes (Amarelo = Mob, Azul = Player, Dourado = Trio, Roxo = Mob Agressivo).

**Non-Goals:**
- Não inclui renderização 3D WebGL complexa (Canvas 2D retrô é suficiente e mais performático).
- Não substitui o cliente do jogo Lineage 2.

## Decisions

1. **Endpoint Java HTTP Handler em `WebAPIManager` (`RadarHandler`):**
   - Utilizar `World.getInstance().getVisibleObjectsInRange(bot, Creature.class, radius)` para obter as entidades próximas sem impactar a performance da thread principal.

2. **Projeção Tridimensional para 2D Canvas ($0 \to 100\%$):**
   - $\Delta X = X_i - Bot_X$, $\Delta Y = Y_i - Bot_Y$.
   - $Canvas_X = Center_X + (\Delta X / Zoom) \times R$.
   - $Canvas_Y = Center_Y - (\Delta Y / Zoom) \times R$.

3. **Interface Canvas HTML5 em Loop de Animação (1s Polling):**
   - Utilizar `requestAnimationFrame` e polling de 1 segundo via `fetch('/api/admin/radar?botName=...&radius=...')` para manter a tela limpa e fluida.

## Risks / Trade-offs

- **[Risco]** Alto número de entidades (ex: 200 mobs) em raio de 5000 na cidade pode poluir a tela do radar.
  - *Mitigação*: Agrupar contadores no radar ou limitar o retorno JSON às 50 entidades mais próximas do bot.
