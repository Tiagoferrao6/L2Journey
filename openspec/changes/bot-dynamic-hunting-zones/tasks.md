## 1. Configuração XML & Data Model

- [x] 1.1 Criar o arquivo `dist/game/data/fakeplayer_hunting_zones.xml` com suporte a `minLevel`, `maxLevel` e waypoints (`SINGLE`, `LINE`, `CIRCLE`).
- [x] 1.2 Criar o gerenciador `FakePlayerHuntingZoneManager.java` para parsear o XML e fornecer busca por nível e trocas dinâmicas de waypoints.

## 2. Motores Táticos & Consumíveis

- [x] 2.1 Atualizar `LLMCompanionManager.java` para creditar 2.000 Soulshots NG e 50 Poções de Cura no spawn dos bots.
- [x] 2.2 Integrar `FakePlayerHuntingZoneManager` com `ShirouTacticalEngine.java` e `CrystalTacticalEngine.java` para seguir as rotas dos waypoints ativos.
- [x] 2.3 Implementar transição de nível automática (Overlevel check) e troca por ausência de mobs / KS.

## 3. Frontend Web Inspector

- [x] 3.1 Adicionar botão `🔍 Inspecionar` na tabela de bots do Dashboard GM em `web/index.html`.
- [x] 3.2 Criar o Modal UI **Bot Inspector** em `web/index.html` com abas para Equipamentos, Inventário Completo e Buffs Ativos.
- [x] 3.3 Copiar e sincronizar `web/index.html` para `dist/game/web/index.html`.
