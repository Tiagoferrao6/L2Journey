# Design: Player Stat & EXP Bonus Caps Expansion and Rate Rework

## Context

The L2Journey server features high-tier custom gear (Royal Set, Royal Dynasty Weapons, Level 6 Tattoos) and custom cumulative subclass skills. The existing stat caps, EXP bonus limits, and 1x base rates in `dist/game/config/player/character.ini` and `dist/game/config/admin/rates.ini` clamped player combat scaling and progression speed.

## Configuration Comparison Matrix

```
┌───────────────────────────────────┬───────────────────┬───────────────────┐
│ Parameter / Setting               │ Original Value    │ New Configured    │
├───────────────────────────────────┼───────────────────┼───────────────────┤
│ Max Physical Attack (MaxPAtk)     │ 37,850            │ 100,000           │
│ Max Magic Attack (MaxMAtk)        │ 50,000            │ 100,000           │
│ Max Attack Speed (MaxPAtkSpeed)   │ 1,400             │ 3,500             │
│ Max Cast Speed (MaxMAtkSpeed)     │ 1,850             │ 3,500             │
│ Max EXP Bonus (MaxExpBonus)       │ 3.5x              │ 5.0x (500%)       │
│ Max SP Bonus (MaxSpBonus)         │ 3.5x              │ 5.0x (500%)       │
│ Base Server Rates (XP, SP, Drops) │ 1x                │ 5x                │
│ Party EXP / SP Bonus (RatePartyXp)│ 1.0x              │ 1.2x (+20% bonus) │
│ Quest Reward Rates (XP, SP, Adena)│ 1x                │ 10x               │
└───────────────────────────────────┴───────────────────┴───────────────────┘
```

## Implementation Details

- **Target File 1**: `dist/game/config/player/character.ini`
  - `MaxPAtk = 100000`
  - `MaxMAtk = 100000`
  - `MaxPAtkSpeed = 3500`
  - `MaxMAtkSpeed = 3500`
  - `MaxExpBonus = 5.0`
  - `MaxSpBonus = 5.0`
- **Target File 2**: `dist/game/config/admin/rates.ini`
  - `RateXp = 5`
  - `RateSp = 5`
  - `RatePartyXp = 1.2`
  - `RatePartySp = 1.2`
  - `DeathDropAmountMultiplier = 5`
  - `SpoilDropAmountMultiplier = 5`
  - `DropAmountMultiplierByItemId = 57,5;6656,1;6657,1;6658,1;6659,1;6660,1;6661,1;6662,1;8191,1;10170,1;10314,1`
  - `RateQuestDrop = 10`
  - `RateQuestRewardXP = 10`
  - `RateQuestRewardSP = 10`
  - `RateQuestRewardAdena = 10`
  - `RateQuestReward = 10`

## Risks & Trade-offs

- **[Risk] High Attack/Cast Speed client animation desync** → **Mitigation**: Modern High Five L2 client handles up to ~4,000 Atk/Cast speed gracefully. Setting 3,500 ensures rapid skill execution while maintaining animation stability.
