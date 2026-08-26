# Tasks: Increase Player Stat Caps, EXP/SP Bonus Caps, and Rework Server Rates

## Implementation Tasks

- [x] 1. **Configuration File Update - Player Stat & EXP Caps**
  - Update `dist/game/config/player/character.ini`: Set `MaxPAtk = 100000`, `MaxMAtk = 100000`, `MaxPAtkSpeed = 3500`, `MaxMAtkSpeed = 3500`, `MaxExpBonus = 5.0`, and `MaxSpBonus = 5.0`.

- [x] 2. **Configuration File Update - Server Multipliers & Rates**
  - Update `dist/game/config/admin/rates.ini`: Set `RateXp = 5`, `RateSp = 5`, `DeathDropAmountMultiplier = 5`, `SpoilDropAmountMultiplier = 5`, `DropAmountMultiplierByItemId = 57,5;...`, `RatePartyXp = 1.2`, `RatePartySp = 1.2`, `RateQuestDrop = 10`, `RateQuestRewardXP = 10`, `RateQuestRewardSP = 10`, `RateQuestRewardAdena = 10`, and `RateQuestReward = 10`.

- [x] 3. **GameServer Reload & Verification**
  - Restart or reload configuration in GameServer container (`l2journey_gameserver_1`).
  - Verify stat caps, rates, and EXP bonus caps in `Config` and on test character (`TitanTester` / `SilverTester`).
