# Proposal: Add TitanTester Test Character Setup

## Why

Provide a comprehensive, max-tier test character (`TitanTester`, ID `300000001`) under account `tester` on the L2Journey server. This enables full-suite end-to-end testing for end-game PvP balance of Orc Warrior (Titan) main class, cumulative subclass interactions (Warrior + Summoner + Assassin skills on Titan), maximum skill enchantments (+30/+15), Hero status mechanics, full Clan skills, custom Royal Armor sets, all 16 custom Royal Dynasty Weapons, Level 6 Tattoos, boss jewelry, and raid access items.

## What Changes

- **Character Identity & Account**:
  - Create character `TitanTester` (`charId = 300000001`) under account `tester`.
  - Set level to 85, race to Orc, base class to `Titan` (Class ID 113), Noblesse status (`nobless = 1`).
  - Assign to the same clan as `SilverTester` (`clanid = 100000`) with full clan privileges (`clan_privs`).
- **Subclasses (Level 85 each)**:
  - Subclass 1 (Warrior): `DreadNought` (Class ID 89) or `Duelist` (Class ID 88).
  - Subclass 2 (Summoner): `Spectral Master` (Class ID 111).
  - Subclass 3 (Assassin): `Ghost Hunter` (Class ID 108).
- **Skills & Skill Enchantments**:
  - Inject all `Titan` skills at maximum skill level and maximum enchantment (+30 for 2nd/3rd class skills where applicable, +15 for 3rd class).
  - Inject cumulative skills from all 3 active subclasses into the Main Class (`Titan`) according to server cumulative subclass rules.
  - Unlock and populate all Clan Skills (`clan_skills`) at max level.
- **Hero Status & Hero Skills**:
  - Register `TitanTester` in the `heroes` database table (`charId = 300000001`, `class_id = 113`, `played = 1`, `claimed = 'true'`).
  - Unlock Hero skills (Heroic Valor, Heroic Grandeur, Heroic Miracle, Heroic Berserker, Heroic Dread).
- **Custom Armors & Weapons**:
  - **Custom Armors (Royal Set)**: Full Royal Heavy Set (Breastplate Weapon Master `99201`, Gaiters `99204`, Helmet `99205`, Gauntlet `99206`, Boots `99207`, Shield `99208`, Cloak `99224`) + full Light and Robe sets in inventory.
  - **Custom Weapons (All 16 Royal Dynasty Weapons)**: Complete collection of items `99300` through `99315` (Blade, Guardian 2H Sword, Crusher Hammer, Cudgel, Baghnakh Fists, Dagger Knife, Bow, Crossbow, Halberd, Dual Blades, Dual Daggers, Phantom Staff, Mace, Staff, Rapier, Ancient Sword).
- **Level 6 Tattoos (Complete Collection)**:
  - Right Slot (Underwear): IDs `41006`, `41012`, `41018`, `41024`, `41030`, `41036`, `41042`.
  - Left Slot (Hair2): IDs `41048`, `41054`, `41060`, `41066`, `41072`, `41078`, `41084`.
- **Boss Jewelry & Epic Accessories**:
  - Ring of Baium (`6658`), Zaken's Earring (`6659`), Antharas' Earring (`6656`), Valakas' Necklace (`6657`), Queen Ant's Ring (`6660`), Beleth's Ring (`10314`).
- **Supplies, Consumables & Boss Access (Same as SilverTester)**:
  - 1,000,000,000 Adena (`57`).
  - 500x Greater CP (`5592`), 500x Greater HP (`1539`), 500x Mana Potions (`728`).
  - 100x BSOE (`1538`), 100x BRES (`6393`).
  - 100,000x Soulshot S-Grade (`1467`), 100,000x Blessed Spiritshot S-Grade (`3952`).
  - 500x Giant's Code - Mastery (`9625`), 500x Giant's Code (`6622`), 100x Top Life Stone Lv84/85 (`12753`), 300x Attribute Stones & Crystals.
  - Boss Access: Blooded Fabric (`4295`), Floating Stone (`3865`), Portal Stone (`7267`), Frintezza's Scroll (`8073`), Noblesse Tiara (`7694`).

## Capabilities

### New Capabilities
- `titan-tester-setup`: SQL database script and setup automation for `TitanTester` (`300000001`), including hero status, max enchanted skills, cumulative subclass skills, clan skills, custom armors/weapons, tattoos, epic jewelry, supplies, and boss access.

### Modified Capabilities
- `test-character-setup`: Expands test character infrastructure to include `TitanTester` alongside `SilverTester`.

## Impact

- **Database Tables**: Inserts/updates records in `characters`, `character_subclasses`, `items`, `character_skills`, `clan_data`, `clan_skills`, `heroes`, and `character_variables` for `charId = 300000001`.
- **Game Server**: Requires executing SQL update while `TitanTester` is offline.
