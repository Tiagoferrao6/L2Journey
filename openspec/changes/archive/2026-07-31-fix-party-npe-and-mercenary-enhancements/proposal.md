# Proposal: Fix Party NPE & Enhance Mercenaries with Native Pet UI, BabyPet Support AI, Speed & Buffs

## Why

1. **Party NullPointerException**: Players joining or creating parties with mercenaries experience client packet serialization errors (`PartySmallWindowAll`, `PartySmallWindowAdd`) due to uninitialized `PartyDistributionType` returning `null`.
2. **Mercenary AI & Reaction**: Mercenaries currently use a basic follow tick without dynamic HP/MP evaluation or automatic continuous buffing similar to `BabyPet` (Baby Kookaburra/Cougar/Buffalo).
3. **Native HUD & Pet Control Panel**: Players lack native pet frame status (HP/MP bar, Pet Action Window `Alt+Y`) to issue direct Attack, Follow, and Dismiss orders to their active Mercenary.
4. **Alt+B Dismiss Control**: Players need a direct "Dispensar" button in the Alt+B Community Shop to dismiss/unsummon mercenaries.

## What Changes

- **Fix Party NPE & Defensive Packets**:
  - Default `_partyDistributionType` in `Player.java` and fallback in `Party.java` to `PartyDistributionType.FINDERS_KEEPERS`.
  - Add null-checks in `PartySmallWindowAll` and `PartySmallWindowAdd` packet serialization.
- **BabyPet-Style Support AI Engine for Mercenary Healer**:
  - Implement dynamic HP/MP evaluation in `MercenaryInstance` (Major Heal when HP < 30%, Minor Heal when HP < 70%, Recharge MP when owner in combat & MP < 60%).
  - Implement automatic continuous buff maintenance (checks missing buffs on owner/party and casts them automatically).
- **Native Pet UI & HUD Integration (`PetInfo` / `PetStatusShow`)**:
  - Transmit `PetInfo` and `PetStatusShow` packets when summoning/loading mercenaries so the Lineage 2 native Pet HUD and `Alt+Y` Pet Action window render on the player's screen.
  - Support `PetAction` commands (Attack, Follow, Stay) for Mercenaries.
- **Mercenary Running Mode & Permanent Buffs**:
  - Reconfigure `MercenaryInstance` to run by default (`setRunning()`).
  - Apply permanent movement speed (`Wind Walk`), attack/cast speed (`Haste`/`Acumen`), and stat buffs (`Might`, `Shield`, `Empower`, `Berserker Spirit`).
- **Alt+B Community Board Dismiss Button**:
  - Add `_merc_dismiss` bypass command in `RequestBypassToServer.java` calling `MercenaryManager.dismissMercenary(player, true)`.
  - Update `dist/game/data/html/CommunityBoard/home.html` with the "Dispensar" button.

## Impacted Components

- `com.l2journey.gameserver.model.actor.Player`
- `com.l2journey.gameserver.model.groups.Party`
- `com.l2journey.gameserver.network.serverpackets.PartySmallWindowAll`
- `com.l2journey.gameserver.network.serverpackets.PartySmallWindowAdd`
- `com.l2journey.gameserver.model.actor.instance.MercenaryInstance`
- `com.l2journey.gameserver.managers.MercenaryManager`
- `com.l2journey.gameserver.network.clientpackets.RequestBypassToServer`
- `dist/game/data/html/CommunityBoard/home.html`
