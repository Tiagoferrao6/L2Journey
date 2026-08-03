# Design Document: Fix Player XP, Mercenary Companion Party UI & Despair Spawns

## Technical Architecture

### 1. EXP Distribution & Party Cutoff Engine (`Party.java`, `Attackable.java`)
- **Level Gap Bypass**: In `Party.calculateExpSpPartyCutoff`, if a party member is a `MercenaryInstance`, exclude it from calculating `topLvl` for the cutoff penalty.
- **Level Resync**: When a player hires a mercenary, set `merc.getStat().setLevel(owner.getLevel())` and auto-equip Top D-Grade or Top Grade matching the owner's level.
- **Full Player XP**: Allocate 100% of monster EXP share to human player members when fighting with a `MercenaryInstance`.

### 2. Party Packet Transmission & Client UI (`MercenaryManager.java`, `Party.java`)
- **Client Frame Initialization**: Before adding a mercenary to a newly created party, send `owner.sendPacket(new PartySmallWindowAll(owner, party))` to initialize the party UI frame on the L2 client.
- **Auto-Teleport Hook**: Ensure `Player.teleToLocation` calls `merc.teleToLocation(owner.getLocation(), false)`.

### 3. Tester Character Setup (`Player.java`, SQL/Admin scripts)
- Set tester character (`tiagof`) to Level 20.
- Equip Top D-Grade Armor Set, Top D-Grade Weapon, and Top D-Grade Jewels via `FakePlayerEquipmentData.autoEquip(player, Grade.D_GRADE)`.

### 4. Permanent Active Bots in Ruins of Despair (`fake_hunters_spawns.xml`, `FakeHunterManager.java`)
- **XML Coordinates Update**:
  - `RUINS_OF_DESPAIR`: `x="-19120" y="136816" z="-3752" radius="1500" amount="5"`.
- **Permanent Activity (No Sleep Mode)**:
  - Add exemption in `FakeHunterManager` / `ZoneListener` for *Ruins of Despair* bots so they remain active 100% of the time, independent of human player proximity.
- **Bot Compositions & Roles**:
  - `DespairArcher`: Solo Sagittarius / Phantom Ranger with kiting AI.
  - `DespairTank`: Paladin with `Hate` (ID 28) / `Taunt` (ID 18) targeting mobs.
  - `DespairHealer`: Bishop / Elven Elder in party with Tank & Dagger, executing `Greater Heal` (ID 1217) and support buffs.
  - `DespairDagger`: Treasure Hunter in party, positioning at mob's rear and casting `Backstab` (ID 30) / `Deadly Blow` (ID 263).
  - `DespairSpoil`: Scavenger / Bounty Hunter casting `Spoil` (ID 254) on target mobs and `Sweep` (ID 42) on corpses.
