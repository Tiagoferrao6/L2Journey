## Context

Following the creation of the Royal Armor Set, the user requested a matching custom weapon line (**Royal Dynasty Weapons**) incorporating all 16 Dynasty weapon types, Masterwork suffixes (Great Gale, Destruction, Thunder, Earth, Storm, Gale, Earthquake, Shadow, Nature, Wisdom, Chaos, Breeze), 3 selectable SA choices per weapon, and exclusive PvP refinement triggers (Cancel, Ignore Shield, CP Drain, Mirage, Rapid Fire, Reset Cooldown, Casting Speed Burst). The weapons must be S Grade (equippable starting at Level 76), feature stats superior to S84 Elegia weapons, be obtainable exclusively via NPC exchange using Conqueror's Badges (Item ID 99000), and include complete client-side DAT integration.

## Goals / Non-Goals

**Goals:**
- Implement 16 custom S Grade Royal Dynasty Weapons (IDs `99300` to `99315`) with `crystal_type="S"` and minimum level requirement 76 (`<player minLevel="76" />`), maintaining Dynasty icon/model definitions and ~12% higher stats than Elegia weapons.
- Create custom skills datapack `dist/game/data/stats/skills/custom/royal_dynasty_weapon_skills.xml` containing:
  - Masterwork Passives (`93101` to `93112`): Great Gale, Destruction, Thunder, Earth, Storm, Gale, Earthquake, Shadow, Nature, Wisdom, Chaos, Breeze.
  - PvP Multipliers & Triggers (`93130` to `93138`): +5% PvP Dmg & +500 CP (`93130`), CP Drain (`93131`), Cancel (`93132`), Ignore Shield (`93133`), Attack Chance (`93134`), Casting Speed Burst (`93135`), Rapid Fire (`93136`), Mirage (`93138`).
- Implement 3 SA options per weapon (IDs `99330` to `99360`).
- Update multisell XML configuration (`dist/game/data/multisell/custom/99300.xml`) for NPC exchange using Conqueror's Badges (ID 99000).
- Provide client-side DAT snippet text (`itemname-e.dat` and `Weapongrp.dat`) for client integration.

**Non-Goals:**
- Creating new 3D mesh files (reusing standard Dynasty client weapon meshes).
- Allowing Royal Dynasty Weapon drops from mobs/bosses (exclusive to NPC Badge exchange).

## Decisions

1. **Naming & Identity (Royal Dynasty Prefix + Suffix)**:
   - Items named `Royal Dynasty [Weapon Name] - [Suffix]` (e.g. `Royal Dynasty Blade - Great Gale`, `Royal Dynasty Bow - Storm`, `Royal Dynasty Knife - Earth`).
   - *Rationale*: Preserves the "Royal" identity established in the armor set while incorporating the iconic Dynasty Masterwork suffixes requested by the user.

2. **16-Weapon Complete Roster**:
   - Covers 1H Sword, 2H Sword, 2H Blunt, 1H Blunt, Dual Fist, Dagger, Bow, Crossbow, Polearm, Dual Swords, Dual Daggers, Magic Sword, Magic Blunt, Magic Staff, Rapier, Ancient Sword.
   - *Rationale*: Guarantees 100% weapon coverage for all High Five classes.

3. **Masterwork & PvP Skill Datapack (`93101` - `93138`)**:
   - Masterwork passives and PvP triggers stored in `dist/game/data/stats/skills/custom/royal_dynasty_weapon_skills.xml`.
   - *Rationale*: Cleanly isolates custom weapon skills from stock datapack skills.

4. **Grade & Level Requirement (S Grade / Level 76+)**:
   - `crystal_type="S"` and `<player minLevel="76" />` across all weapons.
   - *Rationale*: Matches the S-Grade endgame progression tier established by the Royal Armor Set.

## Risks / Trade-offs

- **[Risk] Client display mismatches without client patch**: If a player logs in without updated client DAT files, weapons will display default or fallback text.
  - *Mitigation*: Provide ready-to-paste `itemname-e.dat` and `Weapongrp.dat` line definitions for L2 FileEdit.
