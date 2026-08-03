# Tasks: Enable Geodata Pathfinding & Single-Bot Behavior Tree Testing

## 1. Geodata & Docker Configuration
- [x] **1.1 Ativar Pathfinding em `geodata.ini`**
  - Atualizar `dist/game/config/admin/geodata.ini` definindo `PathFinding = 2` e `CoordSynchronize = 2`.
- [x] **1.2 Mapear Volumes de Geodata em `docker-compose.yml`**
  - Adicionar o mapeamento de volume de `./Geodata` para `/opt/l2journey/game/data/geodata` e de `geodata.ini`.

## 2. Modos de Teste e Behavior Tree
- [x] **2.1 Reduzir Spawn para 1 Bot de Teste**
  - Atualizar `FakeHunterManager.java` para instanciar apenas 1 bot (`TestBot`) para observação direta no jogo.
- [x] **2.2 Implementar Nós da Behavior Tree (`FakePlayerBehaviorTree`)**
  - Criar nós de ação para navegação urbana (`BTActionWalkToNpc`) e de bypass de NPC (`BTActionInteractBypass`).

## 3. Compilação e Validação em Jogo
- [x] **3.1 Rebuild e Validação de Navegação**
  - Executar `l2rebuild` e monitorar o comportamento do bot em Gludio desviando de construções e interagindo com a GK.
