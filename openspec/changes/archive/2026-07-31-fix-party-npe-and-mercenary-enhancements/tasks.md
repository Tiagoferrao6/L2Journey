# Tasks: Party NPE Fix & Mercenary Pet UI / BabyPet AI Integration

## 1. Party NullPointerException Safety
- [x] **1.1 Fix Party Distribution Type Nullability in `Player.java` and `Party.java`** <!-- id: 0 -->
  - Default `getPartyDistributionType()` in `Player.java` to `PartyDistributionType.FINDERS_KEEPERS`.
  - Ensure `Party` constructor handles `null` distribution types safely.
- [x] **1.2 Add Defensive Checks in Party Packets (`PartySmallWindowAll.java`, `PartySmallWindowAdd.java`)** <!-- id: 1 -->
  - Add fallback to `PartyDistributionType.FINDERS_KEEPERS.getId()` if `getDistributionType()` is `null`.

## 2. BabyPet-Style AI Engine & Speed/Buffs
- [x] **2.1 Reconfigure Mercenaries to Run by Default in `MercenaryInstance.java`** <!-- id: 2 -->
  - Call `setRunning()` on spawn and during follow ticks.
- [x] **2.2 Implement BabyPet-Style Automatic Support AI in `MercenaryInstance.java`** <!-- id: 3 -->
  - Implement Major Heal (HP < 30%), Minor Heal (HP < 70%), Recharge MP (owner in combat & MP < 60%), and auto-buffing (Wind Walk, Acumen, Empower, Might, Shield, Berserker Spirit).

## 3. Native Pet UI & HUD Integration (`PetInfo` / `PetStatusShow`)
- [x] **3.1 Transmit `PetInfo` and `PetStatusShow` Packets in `MercenaryManager.java`** <!-- id: 4 -->
  - Send Pet HUD and `Alt+Y` Pet Action Window packets to client on mercenary spawn/login.
- [x] **3.2 Handle Pet Actions (`PetAction` 15 Follow/Stay, 16 Attack) for Mercenaries** <!-- id: 5 -->
  - Allow players to control Mercenary movement and targets via Pet Action Window.

## 4. Alt+B Community Board Dismiss Button
- [x] **4.1 Add `_merc_dismiss` Bypass in `RequestBypassToServer.java`** <!-- id: 6 -->
  - Handle `_merc_dismiss` command and trigger `MercenaryManager.dismissMercenary(player, true)`.
- [x] **4.2 Add Dispensar Button in `dist/game/data/html/CommunityBoard/home.html`** <!-- id: 7 -->
  - Render "Dispensar" button linking to `bypass _merc_dismiss`.
