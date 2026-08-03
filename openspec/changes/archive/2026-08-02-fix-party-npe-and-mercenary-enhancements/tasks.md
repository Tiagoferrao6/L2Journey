# Tasks: Party NPE Fix, Single Mercenary Enforcer & Pet Command Window

## 1. Party NullPointerException Safety
- [x] **1.1 Fix Party Distribution Type Nullability in `Player.java` and `Party.java`**
  - Default `getPartyDistributionType()` in `Player.java` to `PartyDistributionType.FINDERS_KEEPERS`.
  - Ensure `Party` constructor handles `null` distribution types safely.
- [x] **1.2 Add Defensive Checks in Party Packets (`PartySmallWindowAll.java`, `PartySmallWindowAdd.java`)**
  - Add fallback to `PartyDistributionType.FINDERS_KEEPERS.getId()` if `getDistributionType()` is `null`.

## 2. Single Mercenary Per Player Enforcer
- [x] **2.1 Guarantee 1 Active Mercenary Per Player in `MercenaryManager.java`**
  - Despawn and delete DB record of any existing mercenary before hiring a new contract.

## 3. Native Pet UI & HUD Integration (`PetInfo` / `PetStatusShow`)
- [x] **3.1 Transmit `PetInfo` and `PetStatusShow` Packets in `MercenaryManager.java`**
  - Send Pet HUD and `Alt+Y` Pet Action Window packets to client on mercenary spawn/login.
- [x] **3.2 Handle Pet Actions (`PetAction` 15 Follow/Stay, 16 Attack, 19 Dismiss) for Mercenaries**
  - Allow players to control Mercenary movement and targets via Pet Action Window in `RequestActionUse.java`.

## 4. Alt+B Community Board Dismiss Button
- [x] **4.1 Add `_merc_dismiss` Bypass in `RequestBypassToServer.java`**
  - Handle `_merc_dismiss` command and trigger `MercenaryManager.dismissMercenary(player, true)`.
- [x] **4.2 Add Dispensar Button in `dist/game/data/html/CommunityBoard/home.html`**
  - Render "Dispensar Mercenário" button linking to `bypass _merc_dismiss`.
