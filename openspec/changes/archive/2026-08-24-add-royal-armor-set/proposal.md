## Why

To provide players with an S Grade top-tier custom armor set ("Royal Set") usable starting at Level 76 (3rd class change) that preserves the iconic Dynasty visual skin, icons, and class specialization mechanics (Option A - Shield Master, Weapon Master, Dagger Master, Bow Master, Wizard, Healer, etc.) while delivering stats and set bonuses superior to S84 Elegia. The set includes a custom **Royal Cloak (Capa Royal)** granting advanced passive stats and an active Resurrection skill with 70% XP recovery. The set will be obtainable exclusively through an NPC exchange using Conqueror's Badges (Item ID 99000) and will feature complete client-side DAT integration.

## What Changes

- **Royal Armor Items Data (S Grade / Level 76)**: Define custom S Grade armor pieces (Royal Breastplates for each specialization, Royal Gaiters, Royal Helmet, Royal Gauntlets, Royal Boots, Royal Shield, Royal Sigil, and **Royal Cloak**) with level 76 requirement and stats ~12% higher than Elegia.
- **Royal Cloak (Capa Royal) & Resurrection Skill**: Add a custom Royal Cloak conferring passive stat boosts and granting an active skill: **Royal Resurrection** (Resurrection with 70% XP recovery, `power="70"`).
- **Royal Armor Set Skills**: Create custom set bonus skills for all 11+ Dynasty class specializations with expanded stat modifiers (e.g. STR+4, DEX+2, CON+1, higher P.Atk%, M.Atk%, Crit Rate, HP/MP boost, and status resistances).
- **Royal Armor Set XML**: Register all set combinations in `dist/game/data/stats/armorsets/royal_set.xml`.
- **Badge NPC Exchange (Multisell)**: Configure multisell XML entries to allow players to exchange Conqueror's Badges (Item ID 99000) for Royal Set pieces and Royal Cloak at the custom NPC shop.
- **Client DAT Integration**: Provide formatted text entries for `itemname-e.dat` and `ArmorGrp.dat` so the L2 Client displays "Royal..." item names while linking to Dynasty textures, cloak models, and icons.

## Capabilities

### New Capabilities
- `royal-armor-set`: Custom top-tier S Grade (Level 76+) armor set and Royal Cloak based on Dynasty skin, icons, and class specialization mechanics with boosted stats and 70% XP Resurrection skill.
- `badge-npc-exchange`: Dedicated NPC multisell exchange system allowing Royal Set acquisition exclusively using Conqueror's Badges.

### Modified Capabilities
*(None - no core requirement changes to existing capabilities)*

## Impact

- **Server Data**:
  - `dist/game/data/stats/items/custom/royal_set.xml`
  - `dist/game/data/stats/armorsets/royal_set.xml`
  - `dist/game/data/stats/skills/custom/royal_set.xml`
  - `dist/game/data/multisell/custom/`
- **Client Integration**:
  - Client DAT file entries (`itemname-e.dat`, `ArmorGrp.dat`)
