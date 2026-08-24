## Why

To complement the Royal Armor Set, we need a high-tier S Grade custom weapon line (**Royal Dynasty Weapons**) usable starting at Level 76 (3rd class change). These 16 weapons will incorporate the full Masterwork & PvP Refinement characteristics (suffixes Great Gale, Destruction, Thunder, Earth, Storm, Gale, Earthquake, Shadow, Nature, Wisdom, Chaos, Breeze), 3 selectable Special Ability (SA) options per weapon, and exclusive PvP trigger effects (Cancel, Ignore Shield, CP Drain, Mirage, Rapid Fire, Reset Cooldown, Casting Speed Burst). Like the Royal Armor Set, Royal Dynasty Weapons will be obtainable exclusively through NPC exchange using Conqueror's Badges (Item ID 99000) and will include full client DAT integration.

## What Changes

- **Royal Dynasty Weapon Item Definitions (16 Weapons / S Grade / Level 76+)**: Define 16 custom S Grade weapons named `Royal Dynasty [Weapon] - [Suffix]` (Blade, Guardian, Crusher, Cudgel, Baghnakh, Knife, Bow, Crossbow, Halberd, Dual Blade, Dual Daggers, Phantom, Mace, Staff, Rapier, Ancient Sword) with level 76 requirement and S84+ stats.
- **Custom Weapon Skills Datapack (`royal_dynasty_weapon_skills.xml`)**:
  - **Masterwork Passives (`93101` to `93112`)**: Native passive skills for Great Gale, Destruction, Thunder, Earth, Storm, Gale, Earthquake, Shadow, Nature, Wisdom, Chaos, Breeze.
  - **PvP Refine & Triggers (`93130` to `93138`)**: +5% PvP Dmg & +500 CP (`93130`), CP Drain (`93131`), Cancel (`93132`), Ignore Shield (`93133`), Attack Chance (`93134`), Casting Speed Burst (`93135`), Rapid Fire (`93136`), Mirage (`93138`).
- **3 Selectable SA Options**: Support 3 SA choices per weapon (e.g. Focus / Health / Haste, Acumen / Empower / Magic Silence) selectable at the NPC shop.
- **Badge NPC Exchange (Multisell)**: Configure multisell XML entries to allow players to exchange Conqueror's Badges (Item ID 99000) for Royal Dynasty Weapons at the custom NPC shop.
- **Client DAT Integration**: Provide ready-to-paste text lines for `itemname-e.dat` and `Weapongrp.dat` so the L2 Client displays "Royal Dynasty [Weapon Name] - [Suffix]" with Dynasty weapon icons and 3D models.

## Capabilities

### New Capabilities
- `royal-dynasty-weapons`: Complete 16-weapon S Grade (Level 76+) line with Masterwork passives, 3 SA options, and PvP refine trigger effects based on Dynasty models.
- `badge-npc-weapon-exchange`: Integration of Royal Dynasty Weapons into the Conqueror's Badge NPC multisell shop.

### Modified Capabilities
*(None - no core requirement changes to existing capabilities)*

## Impact

- **Server Data**:
  - `dist/game/data/stats/items/custom/royal_dynasty_weapons.xml`
  - `dist/game/data/stats/skills/custom/royal_dynasty_weapon_skills.xml`
  - `dist/game/data/multisell/custom/99300.xml`
- **Client Integration**:
  - Client DAT file entries (`itemname-e.dat`, `Weapongrp.dat`)
