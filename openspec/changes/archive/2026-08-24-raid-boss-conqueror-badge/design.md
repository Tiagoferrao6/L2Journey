# Design Document: Raid Boss Conqueror's Badge & Merchant System

## Architecture Overview

O sistema é dividido em 3 camadas principais:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           1. Custom Item XML                            │
├─────────────────────────────────────────────────────────────────────────┤
│ ID: Custom Item (ex: 99000)                                             │
│ Name: Conqueror's Badge                                                 │
│ Properties: Stackable, Tradeable, Dropable                              │
└─────────────────────────────────────────────────────────────────────────┘
                                     │
                                     ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                      2. Drop Handler (Java / Custom)                    │
├─────────────────────────────────────────────────────────────────────────┤
│ Intercepta OnKill de L2RaidBossInstance / L2GrandBossInstance           │
│ Aplica tabela Rnd.get por Level Range                                   │
│ Invoca attackable.dropItem(killer, itemId, count)                       │
└─────────────────────────────────────────────────────────────────────────┘
                                     │
                                     ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                 3. NPC Merchant & Multisells (Giran)                    │
├─────────────────────────────────────────────────────────────────────────┤
│ Name: Conqueror's Store                                                 │
│ Visual Skin: Death Lord Shax (Template 25282)                           │
│ Scaling: Altura/Colisão de GK (collisionHeight ~28-30)                  │
│ Location: Giran (Próximo à GK)                                          │
│ Diálogos HTML: Index, Armas, Armaduras, Tattoos                         │
│ Multisells: 90000_weapons.xml, 90000_armors.xml, 90000_tattoos.xml      │
└─────────────────────────────────────────────────────────────────────────┘
```

## Proposed Changes

### Core GameServer / Scripts
- **Drop Manager / Custom Listener**:
  Criar um script em `dist/game/data/scripts/custom/RaidBossReward/RaidBossReward.java` ou integrar no `EventDropManager.java` / `Attackable.java` para escutar abates de Raid Boss e Grand Boss de mundo aberto.

### Data (XML / HTML / Spawns)
- **Item XML**: Adicionar item `Conqueror's Badge` no XML de itens customizados (`dist/game/data/stats/items/`).
- **NPC XML**: Criar registro do NPC Comerciante `Conqueror's Store` (`dist/game/data/stats/npcs/custom/` ou similar), utilizando o `displayId` 25282 (Death Lord Shax) e ajustando `<collision><radius normal="10"/><height normal="30"/></collision>`.
- **HTML Dialogs**: Criar os arquivos em `dist/game/data/html/merchant/` para o NPC `Conqueror's Store`.
- **Multisell XMLs**: Criar arquivos de multisell para Armas, Armaduras e Tattoos em `dist/game/data/multisell/`.
- **Spawn XML**: Adicionar spawn do NPC em Giran próximo à GK (`x, y, z, heading`).

## Decision Log
- **Visual do NPC (`Death Lord Shax` com altura de GK)**: Utilizar a mesh marcante do Death Lord Shax, porém redimensionada para tamanho humano comum (equivalente a uma GK) sob o nome `Conqueror's Store`.
- **Drop em Chão (`dropItem`)**: Optado por drop no chão em vez de auto-loot direto no inventário para incentivar a competição e movimentação após a vitória sobre o Boss.
- **Estrutura Modular de HTML**: Separados os canais de venda em Armas, Armaduras e Tattoos para permitir a fácil inclusão dos futuros itens personalizados.
