## Why

Provide a comprehensive, high-tier test setup for the test character (`SilverTester`, ID `300000000`) on the L2Journey server. This enables full-suite end-to-end testing for end-game PvP balance, cumulative subclass interactions (Sword Muse songs on Moonlight Sentinel), tattoo level 6 stats, boss access quests, and combat consumable mechanics.

## What Changes

- **Database / Character Economy**: Set Adena balance to 1,000,000,000 (1 Billion) for `SilverTester`.
- **Combat Consumables & Supplies**:
  - 500x Greater CP Potions (`5592`)
  - 500x Greater Healing Potions (`1539`)
  - 500x Mana Potions (`728`)
  - 100x Blessed Scroll of Escape (`1538`)
  - 100x Blessed Scroll of Resurrection (`6393`)
  - 100.000x Soulshot S-Grade (`1467`)
  - 100.000x Blessed Spiritshot S-Grade (`3952`)
- **Enhancement & Augmentation Materials**:
  - 500x Giant's Code - Mastery (`9625`) / Normal (`6622`)
  - 100x Top-Grade Life Stone Level 84/85 (`12753`)
  - 300x Attribute Stones & Crystals (Fire, Water, Wind, Earth, Holy, Dark)
- **Tattoos Level 6 (Complete Collection)**:
  - Underwear (Right Slot): Ogre Lv6 (`41006`), Monk Lv6 (`41012`), Assassin Lv6 (`41018`), Blood Lv6 (`41024`), Soul Lv6 (`41030`), Flame Lv6 (`41036`), Absolute Lv6 (`41042`).
  - Hair2 (Left Slot): Ogre Lv6 (`41048`), Monk Lv6 (`41054`), Assassin Lv6 (`41060`), Blood Lv6 (`41066`), Soul Lv6 (`41072`), Flame Lv6 (`41078`), Absolute Lv6 (`41084`).
- **Quest & Boss Access Items**:
  - `Blooded Fabric` (`4295`) - Baium Access
  - `Floating Stone` (`3865`) - Antharas Access
  - `Portal Stone` (`7267`) - Valakas Access
  - `Frintezza's Scroll` (`8073`) - Frintezza Access
  - Subclass Certification Books & Noblesse Tiara (`7694`)
- **Cumulative Subclass Configuration**:
  - Inject `Sword Muse` (ID 107) song skills into Main Class (`Moonlight Sentinel`, ID 102).
  - Inject cumulative skill sets into Subclass 1 (`Duelist`, ID 88), Subclass 2 (`DreadNought`, ID 89), and Subclass 3 (`Archmage`, ID 94).

## Capabilities

### New Capabilities
- `test-character-setup`: Setup script and SQL automation for test character items, tattoos, quests, adena, and cumulative subclass skills.

### Modified Capabilities
*None*

## Impact

- **Database Tables**: Directly populates/updates `characters`, `items`, `character_skills`, and `character_variables` for `charId = 300000000`.
- **Game Server**: Requires running SQL script or executing DB update while character is offline or triggering reload in GameServer.
