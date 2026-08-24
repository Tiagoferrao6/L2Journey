## Context

The user requested a custom top-tier armor set ("Royal Set") based on Dynasty visual skins, icons, and class specialization mechanics (Option A). The Royal Set must be S Grade (equippable starting at Level 76), feature stats superior to S84 Elegia, include a custom **Royal Cloak (Capa Royal)** with an active 70% XP Resurrection skill, be obtainable exclusively via NPC exchange using Conqueror's Badges (Item ID 99000), and include complete client-side DAT integration.

## Goals / Non-Goals

**Goals:**
- Implement custom S Grade Royal Armor items (IDs `99200` to `99226`) with `crystal_type="S"` and minimum level requirement 76 (`<player minLevel="76" />`), maintaining Dynasty icon definitions and ~12% higher stats than Elegia.
- Implement custom **Royal Cloak (Capa Royal)** (Item ID `99226`, `bodypart="back"`) providing passive stat boosts and an active skill **Royal Resurrection** (Skill ID `99220`, `power="70"`).
- Implement custom Royal Set skills (IDs `99200` to `99215`) covering all 11+ Dynasty class specializations (Shield Master, Weapon Master, Force Master, Bard, Dagger Master, Bow Master, Healer, Enchanter, Summoner, Wizard).
- Register all set definitions in `dist/game/data/stats/armorsets/royal_set.xml`.
- Create a multisell XML configuration (`dist/game/data/multisell/custom/royal_set_shop.xml`) for NPC exchange using Conqueror's Badges (ID 99000).
- Provide client-side DAT snippet text (`itemname-e.dat` and `ArmorGrp.dat`) for client integration.

**Non-Goals:**
- Creating new 3D mesh files (reusing standard Dynasty client meshes and S-Grade cloak meshes).
- Allowing Royal Set drops from mobs/bosses (exclusive to NPC Badge exchange).

## Decisions

1. **Grade & Level Requirement (S Grade / Level 76+)**:
   - `crystal_type="S"` and `<player minLevel="76" />` across all items so players can equip the set immediately upon reaching 3rd class at Level 76.
   - *Rationale*: Fits the S-Grade endgame progression tier requested by the user.

2. **Royal Cloak & 70% XP Resurrection Skill**:
   - Royal Cloak item grants passive defense/HP attributes and allows casting **Royal Resurrection** (Skill `99220`), implemented using `<effect name="Resurrection"><param power="70" /></effect>`.
   - *Rationale*: Fulfills the explicit user requirement for a cloak with 70% XP resurrection utility.

3. **Option A Architecture (Full Specialization Hierarchy)**:
   - Each Dynasty upper body variant (Shield Master, Weapon Master, Force Master, Bard, Dagger Master, Bow Master, Healer, Enchanter, Summoner, Wizard) will have a dedicated Royal Breastplate item and passive set skill.
   - *Rationale*: Preserves 100% of Dynasty's class-specific gameplay depth.

4. **Item ID Range (`99200+`) & Custom Directory Placement**:
   - Store item definitions in `dist/game/data/stats/items/custom/royal_set.xml` and skills in `dist/game/data/stats/skills/custom/royal_set.xml`.
   - *Rationale*: Keeps custom content isolated from core H5 datapack files, avoiding conflicts during updates.

## Risks / Trade-offs

- **[Risk] Client display mismatches without client patch**: If a player logs in without updated client DAT files, items will display default or fallback text.
  - *Mitigation*: Provide ready-to-paste `itemname-e.dat` and `ArmorGrp.dat` line definitions for L2 FileEdit.
