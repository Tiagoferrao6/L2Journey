# Technical Design: Party NPE Fix & Mercenary Pet UI / BabyPet AI Integration

## Context

Players creating parties or hiring mercenaries experienced `NullPointerException`s in `PartySmallWindowAll`. Additionally, Mercenaries lacked the automatic support AI of `BabyPet` (Kookaburra), native Pet HUD integration (`Alt+Y` Pet Action window), running speed, permanent buffs, and an Alt+B dismiss button.

## Technical Decisions

### 1. Party Distribution Type Safety
- **Player.java**: In `getPartyDistributionType()`, return `_partyDistributionType != null ? _partyDistributionType : PartyDistributionType.FINDERS_KEEPERS`.
- **Party.java**: Ensure the constructor defaults `_distributionType` to `PartyDistributionType.FINDERS_KEEPERS` if `null` is passed.
- **PartySmallWindowAll.java & PartySmallWindowAdd.java**: Use `(_party.getDistributionType() != null ? _party.getDistributionType() : PartyDistributionType.FINDERS_KEEPERS).getId()` during packet writing.

### 2. BabyPet-Style AI Engine in `MercenaryInstance.java`
- Refactor `HealerAITick` to evaluate:
  1. **Major Heal**: If owner/party member HP < 30%, cast Major Heal (`Battle Heal` / `Greater Heal`).
  2. **Minor Heal**: If owner/party member HP < 70%, cast Minor Heal.
  3. **Recharge MP**: If owner is in combat (`owner.isInCombat()`) and owner MP < 60%, cast Recharge.
  4. **Continuous Buffing**: Iterate through support skills (`Wind Walk`, `Acumen`, `Empower`, `Might`, `Shield`, `Berserker Spirit`) and automatically cast missing buffs on owner and party members.

### 3. Native Pet UI & HUD Integration (`PetInfo` / `PetStatusShow`)
- **MercenaryManager.java**: When a mercenary is spawned or reloaded, send `PetInfo` and `PetStatusShow` packets to the owner's `GameClient`.
- **MercenaryInstance.java**: Support Pet Actions (`PetAction` 15 = Follow/Stay, `PetAction` 16 = Attack target).

### 4. Running Mode & Speed Boosts
- **MercenaryInstance.java**: Call `setRunning()` on spawn and during movement ticks to guarantee full running speed.

### 5. Alt+B Dismiss Command & UI Button
- **RequestBypassToServer.java**: Handle `_merc_dismiss` by invoking `MercenaryManager.getInstance().dismissMercenary(player, true)` and notifying the player with `"Mercenário dispensado com sucesso."`.
- **home.html**: Add `<button value="Dispensar" action="bypass _merc_dismiss" width=90 height=25 back="L2UI_CT1.Button_DF_Down" fore="L2UI_CT1.Button_DF">` to the Mercenaries control bar.
