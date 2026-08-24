# Proposal: Custom Rework for Destroyer and Tyrant Skills

## Why

Resparcularize and revamp the core combat skills of Orc Warriors (Destroyer) and Orc Monks (Tyrant) on the L2Journey server by combining C4 classic power fantasy mechanics with modern H5 duration quality-of-life. This change removes weapon type restrictions from Destroyer limiters (*Frenzy* / *Guts*) and Tyrant skills (*Fury Fists* / *Spirit Totems*), while adjusting activation HP thresholds and boosting stat multipliers (+300% P. Atk, +300% P. Def, +33% Atk. Spd).

## What Changes

- **Frenzy (Skill ID 176)**:
  - Activation HP threshold updated from **HP ≤ 60%** to **HP ≤ 40%**.
  - P. Atk multiplier boosted to **+300% P. Atk** (`val="4.0"`).
  - Weapon restrictions removed: Bonus applies to **ALL WEAPON TYPES** (Bows, Daggers, Duals, Swords, Spears, Fists, etc.).
- **Guts (Skill ID 139)**:
  - Activation HP threshold updated from **HP ≤ 30%** to **HP ≤ 40%**.
  - P. Def multiplier boosted to **+300% P. Def** (`val="4.0"`, 4x base physical defense).
- **Fury Fists (Skill ID 222)**:
  - Attack Speed multiplier increased from +25% to **+33% Atk. Spd.** (`val="1.33"`).
  - Weapon restriction removed: Toggle effect applies regardless of equipped weapon type.
- **Tyrant Spirit Totems (Skill IDs 83, 76, 282, 109, 298, 292, 425)**:
  - Weapon restriction (`DUALFIST`) removed from all 7 totens (*Wolf, Bear, Puma, Ogre, Rabbit, Bison, Hawk*).
  - Standardized duration set to **5 minutes (300 seconds)** across all totens (specifically adjusting Bison Totem from 60s to 300s).

## Capabilities

### New Capabilities
- `custom-orc-warrior-monk-skills`: XML skill definition rework for Frenzy, Guts, Fury Fists, and all 7 Tyrant Spirit Totems.

### Modified Capabilities
*None*

## Impact

- **XML Data Files**: Updates `dist/game/data/stats/skills/00100-00199.xml`, `00200-00299.xml`, and `00400-00499.xml`.
- **Game Server**: Requires running `//reload skill` GM command or restarting GameServer container to apply updated XML stats.
