# Proposal: Expand Player Stat Caps, EXP/SP Bonus Caps, and Server Rates

## Why

Unlock the full potential of High-Tier gear (Royal Set, Royal Dynasty Weapons, Level 6 Tattoos), cumulative subclass skill combinations, and smoother leveling by expanding combat stat caps, EXP/SP bonus limits, and server multipliers. Raising base rates to 5x, party EXP bonus to +20% (1.2x), quest rewards to 10x, combat caps (P.Atk/M.Atk 100,000, Atk.Spd/Cast.Spd 3,500), and EXP bonus cap to 5.0x ensures an engaging mid-rate gameplay experience.

## What Changes

### Combat Stat Caps (`character.ini`)
- **MaxPAtk (Physical Attack Cap)**: Increased from 37,850 to **100,000**.
- **MaxMAtk (Magic Attack Cap)**: Increased from 50,000 to **100,000**.
- **MaxPAtkSpeed (Physical Attack Speed Cap)**: Increased from 1,400 to **3,500**.
- **MaxMAtkSpeed (Magic Cast Speed Cap)**: Increased from 1,850 to **3,500**.
- **MaxExpBonus (Maximum EXP Bonus Cap)**: Increased from 3.5x to **5.0x**.
- **MaxSpBonus (Maximum SP Bonus Cap)**: Increased from 3.5x to **5.0x**.

### Server Multipliers & Rates (`rates.ini`)
- **RateXp / RateSp**: Increased from 1x to **5x**.
- **DeathDropAmountMultiplier / SpoilDropAmountMultiplier**: Increased from 1x to **5x**.
- **DropAmountMultiplierByItemId (Adena ID 57)**: Set Adena drop multiplier to **5x**.
- **RatePartyXp / RatePartySp**: Set to **1.2x** (+20% bonus in Party).
- **RateQuestDrop**: Increased from 1x to **10x**.
- **RateQuestRewardXP / RateQuestRewardSP / RateQuestRewardAdena / RateQuestReward**: Increased from 1x to **10x**.

## Capabilities

### New Capabilities
- `player-stat-caps`: Configuration update for player combat stat caps, EXP/SP bonus multipliers, and server rate multipliers in `character.ini` and `rates.ini`.

### Modified Capabilities
*None*

## Impact

- **Configuration Files**: Updates `dist/game/config/player/character.ini` and `dist/game/config/admin/rates.ini`.
- **Game Server**: Requires reloading character and rates configurations via `//reload config` or restarting `l2journey_gameserver_1` container.
