# Proposal: Party NPE Safety, Single Mercenary Enforcer & Pet Command Window Integration

## Why
1. Party creation and packet transmission currently run the risk of `NullPointerException` if a player's `getPartyDistributionType()` evaluates to `null`.
2. Players hiring mercenaries should be strictly limited to **one active mercenary at a time**. Hiring a new mercenary must gracefully dismiss any previously active contract before spawning the new companion.
3. While mercenaries appear in the Party Window (`ALT+F`), players lack a direct native command interface. Integrating Pet HUD packets (`PetInfo`, `PetStatusShow`) and `ALT+Y` Action Window controls allows players to directly command their mercenary (Follow, Stay, Attack, Stop, Dismiss) alongside BabyPet-style automated AI.

## What Changes
- **Party Distribution Type Null Safety**:
  - In `Player.java`: Default `getPartyDistributionType()` to `PartyDistributionType.FINDERS_KEEPERS` if unset.
  - In `PartySmallWindowAll.java` and `PartySmallWindowAdd.java`: Add fallback for null distribution types.
- **Single Mercenary Per Player Guarantee**:
  - In `MercenaryManager.java`: Enforce strict 1-mercenary limit per player object ID (`_activeMercenaries.containsKey(ownerId)`). Automatically dismiss existing mercenary and delete DB record before contract renewal or new hiring.
- **Native Pet HUD & Action Window (`ALT+Y`) Integration**:
  - In `MercenaryManager.java`: Transmit `PetStatusShow` and `PetInfo` packets on mercenary spawn/login to open the native Pet Status HUD and enable the `ALT+Y` Action Window.
  - In `RequestActionUse.java`: Intercept pet action commands (`Action 15` Follow, `Action 16` Attack, `Action 17` Stop, `Action 19` Dismiss) for Mercenary companions.
- **Alt+B Community Board Dismiss Bypass**:
  - In `RequestBypassToServer.java`: Add `_merc_dismiss` bypass.
  - In `dist/game/data/html/CommunityBoard/home.html`: Render a "Dispensar Mercenário" button linking to `bypass _merc_dismiss`.

## Capabilities

### New Capabilities
- `mercenary-single-companion-limit`: Strict 1-mercenary per player constraint with automatic previous contract teardown.
- `mercenary-pet-action-window`: Native Pet Status HUD and `ALT+Y` Action Window integration for direct player command.

### Modified Capabilities
- None

## Impact
- **Backend / GameServer**:
  - `com.l2journey.gameserver.managers.MercenaryManager`: Single-mercenary check & Pet HUD packets.
  - `com.l2journey.gameserver.model.actor.Player`: Distribution type fallback to `FINDERS_KEEPERS`.
  - `com.l2journey.gameserver.network.clientpackets.RequestActionUse`: Pet action interception for Mercenaries.
  - `com.l2journey.gameserver.network.clientpackets.RequestBypassToServer`: `_merc_dismiss` bypass.
- **Datapack HTML**:
  - `dist/game/data/html/CommunityBoard/home.html`: "Dispensar" button integration.
