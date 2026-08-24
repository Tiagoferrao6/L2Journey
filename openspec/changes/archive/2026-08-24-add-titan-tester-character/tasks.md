# Tasks: Add TitanTester Test Character Setup

## Implementation Tasks

- [x] 1. **Character & Subclass SQL Setup**
  - Create SQL statements for `TitanTester` (`charId = 300000001`, `account_name = 'tester'`, `level = 85`, `classid = 113`, `nobless = 1`, `clanid = 100000`).
  - Create `character_subclasses` entries for `DreadNought` (89), `Spectral Master` (111), and `Ghost Hunter` (108).

- [x] 2. **Skills, Hero & Clan SQL Provisioning**
  - Inject all `Titan` (113) skills at maximum level with +30/+15 enchantment levels into `character_skills`.
  - Inject cumulative skills from Warrior, Summoner, and Assassin subclasses into `character_skills`.
  - Insert record into `heroes` table and add Hero skills (Valor, Grandeur, Miracle, Berserker, Dread).
  - Insert full Clan Skills (`clan_skills`) for clan `100000`.

- [x] 3. **Inventory, Custom Armors/Weapons, Tattoos & Supplies SQL Injection**
  - Insert Royal Armor Set (Heavy `99201`, `99204`, `99205`, `99206`, `99207`, `99208`, `99224`, plus Light & Robe sets) into `items`.
  - Insert all 16 Royal Dynasty Weapons (`99300` through `99315`) into `items`.
  - Insert all 14 Level 6 Tattoos (Right: `41006`-`41042`, Left: `41048`-`41084`).
  - Insert Epic Boss Jewels (Baium, Zaken, Antharas, Valakas, Queen Ant, Beleth).
  - Insert 1B Adena, CP/HP/MP potions, BSOE/BRES, SS/BSS S-grade, Giant's Codes, Life Stones, Attribute Stones, and Boss Access items (Blooded Fabric, Floating Stone, Portal Stone, Frintezza's Scroll).

- [x] 4. **Execution & Database Validation**
  - Save SQL script `dist/db_installer/sql/game/custom_titan_tester_setup.sql`.
  - Execute SQL script against MariaDB container (`podman exec l2journey_db_1 mariadb ...`).
  - Validate database records in `characters`, `character_skills`, `heroes`, and `items`.
