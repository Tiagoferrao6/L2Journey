# Tasks: Mercenary Healer System Implementation (MVP)

- [x] **Fase 1: Configuração em `fakeplayers.ini` & XML Profile**
  - [x] Adicionar `EnableMercenaries = True` no arquivo `dist/game/config/npcs/fakeplayers.ini`.
  - [x] Adicionar `MercenaryHireFee = 1` no arquivo `fakeplayers.ini`.
  - [x] Criar o schema XML `dist/game/config/npcs/mercenary_profiles.xml` mapeando o perfil do Mercenário Healer (*Elenora*).

- [x] **Fase 2: Tabela SQL `character_mercenaries` & Backend Java**
  - [x] Criar a tabela `character_mercenaries` no MariaDB para salvar nível, EXP e dados do Healer por personagem.
  - [x] Criar as classes `MercenaryManager.java` e `MercenaryInstance.java` com suporte a nivelamento dinâmico pelo jogador.

- [x] **Fase 3: Contratação On-Demand & Função Reload/Reset (1 Adena)**
  - [x] Implementar a lógica de contratação inicial no nível atual do jogador cobrando 1 Adena.
  - [x] Implementar a função `reloadContract()` para resetar e resincronizar o Healer com o nível atual do jogador por 1 Adena.

- [x] **Fase 4: Inteligência Artificial do Healer**
  - [x] Implementar IA de cura em tempo real (HP < 70%, Cleanse, Resurrection).
  - [x] Integrar divisão justa de EXP/SP na Party e bloqueios de Looting (`canPickup = false`) e Trade.

- [x] **Fase 5: Interface Community Board (`Alt + B`)**
  - [x] Adicionar o botão **"Contratar Mercenário Healer (1 Adena)"** na Community Board.
  - [x] Adicionar o botão **"Resetar Contrato (1 Adena)"** e botões de atalho *Follow/Anchor*.
