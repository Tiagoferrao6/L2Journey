# Change Proposal: bot-dynamic-hunting-zones

## Contexto & Problema
Atualmente, os bots autônomos (`FakePlayer`, `PaladinBot`, `HawkeyeBot`, `BishopBot`) possuem coordenadas fixas de caça hardcoded nos motores táticos (`ShirouTacticalEngine`, `CrystalTacticalEngine`), além de iniciarem sem suprimentos de consumo (Soulshots e Poções). Isso faz com que os bots fiquem presos na cidade tentando comprar suprimentos ou se concentrem todos na mesma coordenada fixa no mapa.

Para permitir uma movimentação natural, orgânica e com progressão de nível, é necessário um sistema estruturado de **Zonas de Caça e Waypoints Dinâmicos**.

## Solução Proposta
1. **Configuração XML de Zonas de Caça (`data/fakeplayer_hunting_zones.xml`):**
   - Mapear zonas de caça indexadas por faixa de nível (`minLevel`, `maxLevel`).
   - Permitir até 20 waypoints por Zona de Caça.
   - Suportar 3 tipos de Waypoints:
     - `SINGLE`: Ponto único de patrulha local.
     - `LINE`: Caminho de 2 a 5 pontos percorrido em ida e volta (ping-pong).
     - `CIRCLE`: Caminho de 2 a 5 pontos percorrido em circuito fechado (loop).

2. **Gerenciador de Zonas (`FakePlayerHuntingZoneManager.java`):**
   - Seleção inteligente de Zona de Caça compatível com o nível atual do bot.
   - Algoritmo de evasão e troca dinâmica de Waypoint quando o local atual estiver sem mobs, lotado por outros players ou interrompido.
   - Transição automática de Zona quando o bot ultrapassar o `maxLevel` da zona atual.

3. **Consumíveis de Inicialização & Inspector Modal Web:**
   - Creditar 2.000 Soulshots NG e 50 Poções de Cura no spawn dos bots companions.
   - Implementar o modal **Bot Inspector** na interface web ([web/index.html](file:///home/tiago/L2Journey/L2Journey/web/index.html)) para inspecionar o inventário completo, equipamentos e buffs dos bots em tempo real.

## Arquivos Envolvidos
- `dist/game/data/fakeplayer_hunting_zones.xml` [NOVO]
- `java/com/l2journey/gameserver/managers/FakePlayerHuntingZoneManager.java` [NOVO]
- `java/com/l2journey/gameserver/managers/LLMCompanionManager.java` [MODIFICAR]
- `java/com/l2journey/gameserver/managers/ShirouTacticalEngine.java` [MODIFICAR]
- `java/com/l2journey/gameserver/managers/CrystalTacticalEngine.java` [MODIFICAR]
- `web/index.html` [MODIFICAR]
- `dist/game/web/index.html` [MODIFICAR]
