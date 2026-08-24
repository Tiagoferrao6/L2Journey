# Design: Custom Rework for Frenzy, Guts, Fury Fists, and Spirit Totems

## Context

Orc Warriors (Destroyers) and Orc Monks (Tyrants) currently operate under High Five (H5) skill definitions, which heavily restrict skill bonuses to specific weapon types (2H Sword/Blunt for Frenzy, Dual Fists for Totems) and apply lower multipliers (+15%/+77% P.Atk for Frenzy, +200% P.Def for Guts, +25% Atk.Spd for Fury Fists). The user requested a custom balance profile combining C4-style freedom (unrestricted weapons, +300% P.Atk Frenzy, +300% P.Def Guts, +33% Atk.Spd Fury Fists) with HP ≤ 40% threshold for both Frenzy and Guts, and 5-minute durations for all Tyrant Totems without weapon limitations.

## Skill XML Rework Matrix

```
┌─────────────────┬───────────┬───────────────────┬───────────────────┬────────────────────────────────┐
│ Skill Name      │ Skill ID  │ Target Attribute  │ Original (H5)     │ New Custom Rework              │
├─────────────────┼───────────┼───────────────────┼───────────────────┼────────────────────────────────┤
│ Frenzy          │ 176       │ HP / P.Atk / Weapon│ HP ≤ 60%, +77% 2H │ HP ≤ 40%, +300% P.Atk (ANY)    │
│ Guts            │ 139       │ HP / P.Def        │ HP ≤ 30%, +200%   │ HP ≤ 40%, +300% P.Def (4x Def) │
│ Fury Fists      │ 222       │ Atk.Spd / Weapon  │ +25%, Dual Fist   │ +33% Atk.Spd, ANY WEAPON       │
│ Spirit Totems   │ 83,76,282 │ Weapon / Duration │ Dual Fist restricted│ ANY WEAPON, 5 Minutes (300s) │
│ (All 7 Totems)  │ 109,298,  │                   │ Bison: 60s        │                                │
│                 │ 292,425   │                   │                   │                                │
└─────────────────┴───────────┴───────────────────┴───────────────────┴────────────────────────────────┘
```

## Detailed File Modifications

### 1. `dist/game/data/stats/skills/00100-00199.xml`
- **Guts (ID 139)**:
  - Change `<player hp="30" />` to `<player hp="40" />`.
  - Change `<table name="#pDef">2 2.5 3</table>` to `<table name="#pDef">4 4 4</table>` or `<mul stat="pDef" val="4.0" />`.
- **Frenzy (ID 176)**:
  - Change `<player hp="60" />` to `<player hp="40" />`.
  - Update effects to apply `<mul stat="pAtk" val="4.0" />` (+300% P. Atk) regardless of weapon type, removing `<using kind="SWORD,BLUNT" />` and `<using slot="lrhand" />` conditions.

### 2. `dist/game/data/stats/skills/00200-00299.xml`
- **Fury Fists (ID 222)**:
  - Change `<mul stat="pAtkSpd" val="1.25" />` to `<mul stat="pAtkSpd" val="1.33" />`.
- **Wolf Spirit Totem (ID 83)**: Remove `<using kind="DUALFIST" />` condition, set `abnormalTime` = `300`.
- **Bear Spirit Totem (ID 76)**: Remove `<using kind="DUALFIST" />` condition, set `abnormalTime` = `300`.
- **Puma Spirit Totem (ID 282)**: Remove `<using kind="DUALFIST" />` condition, set `abnormalTime` = `300`.
- **Ogre Spirit Totem (ID 109)**: Remove `<using kind="DUALFIST" />` condition, set `abnormalTime` = `300`.
- **Rabbit Spirit Totem (ID 298)**: Remove `<using kind="DUALFIST" />` condition, set `abnormalTime` = `300`.
- **Bison Spirit Totem (ID 292)**: Remove `<using kind="DUALFIST" />` condition, set `abnormalTime` = `300`.

### 3. `dist/game/data/stats/skills/00400-00499.xml`
- **Hawk Spirit Totem (ID 425)**: Remove `<using kind="DUALFIST" />` condition, set `abnormalTime` = `300`.

## Risks & Trade-offs

- **[Risk] High Burst Damage with Bows/Daggers under Frenzy + Totems** → **Mitigation**: This is an intentional custom design choice requested by the server admin for classic C4-style unconstrained Orc gameplay.
