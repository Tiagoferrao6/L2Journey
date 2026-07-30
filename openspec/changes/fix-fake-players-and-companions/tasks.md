# Tasks: Fix & Enhance FakePlayers, Mercenary Companions & Dashboard Control Engine

## 1. Equipment & Grade System (`FakePlayerEquipmentData.java`)
- [x] **1.1 Criar Matriz de Equipamentos por Grade (No-Grade até S-Grade)**
  - Mapear sets de armadura (Heavy, Light, Robe), armas top e joias para cada Grade (NG, D, C, B, A, S).
- [x] **1.2 Auto-Equipamento em `FakePlayer` e `MercenaryInstance`**
  - Implementar auto-equip no spawn/level-up com atualização de paperdoll e recalculação de atributos.

## 2. Injeção Dupla de Shots & Consumíveis Infinitos
- [x] **2.1 Auto-Uso Infinito Simultâneo de Soulshots e Blessed Spiritshots**
  - Injetar ativação automática de Soulshot (ataques/skills físicas) e Blessed Spiritshot (magias/curas) em paralelo.
- [x] **2.2 Auto-Uso Infinito de Health Potions**
  - Consumo automático de Poções de Cura quando HP < 80% para Hunters e Mercenários.

## 3. Despacho em Cidade com Caminhada até a Gatekeeper
- [x] **3.1 Algoritmo de Caminhada até a GK (`FakeHunterManager.java`)**
  - Fazer o FakeHunter caminhar (`moveToLocation`) até o NPC Gatekeeper mais próximo da cidade antes de disparar o teleporte para a zona de caça.

## 4. Mercenário Companion (Party Healer) & Catálogo de Skills por Grade
- [x] **4.1 Catálogo de Skills de Cura & Buffs por Grade (NG a S)**
  - Implementar catálogo de skills de suporte escalonado por nível e grade.
- [x] **4.2 Suporte e Cura para Todos os Membros da Party**
  - Fazer a IA do Companion monitorar e curar/buffar a party inteira do Mestre.

## 5. Companion Auto-Teleport & Painel de Gerenciamento (`.companion`)
- [x] **5.1 Auto-Teleporte do Companion com o Mestre**
  - Interceptação de teleporte do jogador (`Player.teleToLocation()`) para transportar o companion automaticamente.
- [x] **5.2 Tela de Gerenciamento do Companion (Voiced Command `.companion` / Alt+B)**
  - Interface HTML interativa para recall, seleção de buffs, alteração de modo e sincronização.

## 6. Motor de Simulação Fiel em Campo (PvE & PvP AI Engine)
- [x] **6.1 Target Acquisition, Kiting & Posicionamento**
  - Implementar movimentação humana com kiting para arqueiros/magos e pequenos desvios estocásticos.
- [x] **6.2 Retaliação PvP, Looting & Rest**
  - Adicionar reação anti-gank, pausa de 1-3s para coletar loot e sentar para regerar MP quando MP < 10%.

## 7. Dashboard GM Painel de Gerenciamento de FakePlayers
- [x] **7.1 Endpoint de Edição de FakePlayer (`POST /api/admin/fakeplayers/edit`)**
  - Implementar alteração de localização (teleport), reload XML, toggle Ativar/Desativar, alteração de Stance (HUNTING/PAUSE/SAFETY_FLEE), Nível (1-85) e Grade Tier.
- [x] **7.2 Modal de Edição de Bot no Frontend Web (`web/index.html`)**
  - Adicionar controles de edição por linha na tabela de bots com visualização de coordenadas X, Y, Z, seletores de nível/grade, botões de ação e mover localização.

## 8. Dashboard Live Chat Tracking de Localização & Filtro por Região
- [x] **8.1 Registrar Coordenadas X, Y, Z e Região em `Say2.java` & `WebAPIManager.java`**
  - Atualizar `ChatMessageRecord` para armazenar `x`, `y`, `z` e `regionName` do jogador no momento da fala.
- [x] **8.2 Format da Mensagem e Filtro por Região no Frontend (`web/index.html`)**
  - Exibir tag de localização `(@Região [X, Y, Z])` no terminal do chat e adicionar dropdown de filtro por região.

## 9. Compilação e Validação E2E
- [x] **9.1 Compilação e Build de Containers**
  - Recompilação do `Gameserver.jar` e rebuild da imagem Docker.
- [x] **9.2 Validação Visual e Funcional no Jogo & Dashboard**
  - Validar caminhada dos hunters até a GK, injeção dupla de shots, cura de party do companion, painel `.companion`, edição de bots e filtro do chat no Dashboard Web.
