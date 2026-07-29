# Tasks: Mercenary Healer System Implementation (MVP)

- [ ] **Fase 1: Configuração em `fakeplayers.ini` & XML Profile**
  - [x] Adicionar `EnableMercenaries = True` no arquivo `dist/game/config/npcs/fakeplayers.ini`.
  - [ ] Adicionar `MercenaryHireFee = 1` no arquivo `fakeplayers.ini`.
  - [ ] Criar o schema XML `dist/game/config/npcs/mercenary_profiles.xml` mapeando o perfil do Mercenário Healer (*Elenora*).

- [ ] **Fase 2: Tabela SQL `character_mercenaries` & Backend Java**
  - [ ] Criar a tabela `character_mercenaries` no MariaDB para salvar nível, EXP e dados do Healer por personagem.
  - [ ] Criar as classes `MercenaryManager.java` e `MercenaryInstance.java` com suporte a nivelamento dinâmico pelo jogador.

- [ ] **Fase 3: Contratação On-Demand & Função Reload/Reset (1 Adena)**
  - [ ] Implementar a lógica de contratação inicial no nível atual do jogador cobrando 1 Adena.
  - [ ] Implementar a função `reloadContract()` para resetar e resincronizar o Healer com o nível atual do jogador por 1 Adena.

- [ ] **Fase 4: Inteligência Artificial do Healer**
  - [ ] Implementar IA de cura em tempo real (HP < 70%, Cleanse, Resurrection).
  - [ ] Integrar divisão justa de EXP/SP na Party e bloqueios de Looting (`canPickup = false`) e Trade.

- [ ] **Fase 5: Interface Community Board (`Alt + B`)**
  - [ ] Adicionar o botão **"Contratar Mercenário Healer (1 Adena)"** na Community Board.
  - [ ] Adicionar o botão **"Resetar Contrato (1 Adena)"** e botões de atalho *Follow/Anchor*.
