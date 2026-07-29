# Tasks: Fake Players Gludio (População e Otimização Regional)

## 1. Gludio Fake Traders (Economia e Lojas)
- [x] **1.1 Engine de Fake Traders & XML Parsers** (Concluído em `fake-traders-engine` / `fake-traders-parsers`)
- [x] **1.2 Spawns Iniciais de Fake Traders em Gludio** (8 SELL, 4 BUY, 3 CRAFT configurados em `dist/game/config/npcs/fake_traders_spawns.xml`)
- [x] **1.3 Refinamento de Perfis Econômicos D-Grade** (Validar e ajustar tabelas de itens e preços em `fake_traders_economy.xml`)

## 2. Gludio Fake Hunters (Caçadores PvE e DNA)
- [x] **2.1 Configuração de XML `fake_hunters_spawns.xml` em Gludio**
  - [x] Mapear 10 Hunters solo em Ruins of Despair
  - [x] Mapear 2 parties de Hunters (20 bots) entre Gludio e Ruins of Agony
- [x] **2.2 Vinculação de DNA de IA de Combate**
  - [x] Definir perfis de agressividade, fuga e cooperação de party em `fake_hunters_dna.xml`
- [x] **2.3 Rotas e Retorno Seguro**
  - [x] Configurar mecanismo de fuga/leash para evitar atração indevida de mobs (*Train*) para a cidade de Gludio

## 3. Otimização Regional (Zone Listeners & Sleep Mode)
- [x] **3.1 Zone Listener para Gludio Town & Zonas Adjacentes**
  - [x] Detectar presença de jogadores humanos na região geográfica de Gludio
- [x] **3.2 Gerenciamento do Estado de IA (Sleep / Awake)**
  - [x] Colocar Fake Hunters em repouso (*Sleep Mode*) quando nenhum jogador real estiver na zona
  - [x] Despertar a IA dos bots assim que o primeiro jogador entrar na área
