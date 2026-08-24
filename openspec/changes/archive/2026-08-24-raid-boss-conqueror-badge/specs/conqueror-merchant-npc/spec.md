# Capability: Conqueror Merchant NPC

## ADDED Requirements

### Requirement: Location and Spawn
The merchant NPC named `Conqueror's Store` MUST be spawned in Giran near the Gatekeeper (GK) location.

#### Scenario: Spawn em Giran perto da GK
- **GIVEN** que o GameServer está inicializando
- **WHEN** os spawns de NPCs forem carregados
- **THEN** o NPC de trocas `Conqueror's Store` MUST ser instanciado próximo à Gatekeeper em Giran.

### Requirement: NPC Appearance and Scaling
The merchant NPC MUST use the visual mesh/template of Death Lord Shax (displayId `25282`) scaled to normal Gatekeeper height (`collisionHeight` ~28-30, `collisionRadius` ~10-12).

#### Scenario: Visual e Proporção do NPC
- **GIVEN** que o NPC `Conqueror's Store` é renderizado no cliente
- **WHEN** um jogador se aproximar do NPC em Giran
- **THEN** o NPC MUST ter o modelo visual do Death Lord Shax com tamanho proporcional ao de uma Gatekeeper comum.

### Requirement: Interactive HTML Dialogs
The NPC MUST provide interactive HTML dialogs with navigation options for Weapons, Armor, and Tattoos.

#### Scenario: Visualizar Categorias no Diálogo do NPC
- **GIVEN** que o jogador conversa com o NPC
- **WHEN** a janela de diálogo for aberta
- **THEN** o diálogo MUST apresentar opções navegáveis para Armas, Armaduras e Tattoos.

### Requirement: Multisell Exchange
The exchange stores MUST use `Conqueror's Badge` as the required ingredient item for all offered products.

#### Scenario: Realizar Troca na Loja Multisell
- **GIVEN** que o jogador possui a quantidade necessária de `Conqueror's Badge`
- **WHEN** o jogador selecionar um item na loja multisell
- **THEN** a moeda MUST ser consumida e o item selecionado entregue ao jogador.
