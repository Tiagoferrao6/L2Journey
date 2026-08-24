# Design: TitanTester Test Character Architecture & SQL Provisioning

## Context

The test character `TitanTester` (`charId = 300000001`) under account `tester` is designed to complement `SilverTester` (`300000000`) for end-to-end testing of physical/Orc Warrior gameplay mechanics on the `l2journey` MariaDB database. `TitanTester` features Titan (Class ID 113) main class with subclasses in Warrior, Summoner, and Assassin categories, max level enchanted skills (+30/+15), active Hero status with Hero skills, full Clan skills, custom Royal armors and all 16 custom Royal Dynasty weapons, 14 Level 6 Tattoos, epic boss jewelry, and identical supplies/access items to `SilverTester`.

## Architecture & Data Mapping

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              Account: tester                                │
│                          Character: TitanTester                             │
│                           (charId: 300000001)                               │
└─────────────────────────────────────────────────────────────────────────────┘
                                       │
      ┌────────────────────────────────┼────────────────────────────────┐
      ▼                                ▼                                ▼
┌─────────────┐               ┌─────────────────┐              ┌─────────────────┐
│ Main Class  │               │   Subclasses    │              │   Hero Status   │
│   Titan     │               │ 1. DreadNought  │              │  Table: heroes  │
│ (ID: 113)   │               │ 2. Spec. Master │              │  Hero Skills:   │
│  Level 85   │               │ 3. Ghost Hunter │              │  Valor, Dread,  │
└─────────────┘               └─────────────────┘              │  Miracle, etc.  │
      │                                                         └─────────────────┘
      ├────────────────────────────────┬────────────────────────────────┐
      ▼                                ▼                                ▼
┌─────────────────┐           ┌──────────────────┐             ┌──────────────────┐
│ Character Skills│           │ Custom Inventory │             │   Clan & Skills  │
│ - Max Titan     │           │ - Royal Armor Set│             │ - Clan ID: 100000│
│   +30/+15 Ench. │           │ - 16 Royal Weaps │             │ - Full Clan      │
│ - Cumulative    │           │ - 14 Tattoos Lv6 │             │   Skills Maxed   │
│   Sub Skills    │           │ - Epic Boss Jewels             │                  │
└─────────────────┘           │ - Adena & Supplies             │                  │
                              └──────────────────┘             └──────────────────┘
```

## SQL Provisioning Strategy

### 1. Character & Subclass Population (`characters` & `character_subclasses`)
- `charId = 300000001`, `account_name = 'tester'`, `char_name = 'TitanTester'`, `level = 85`, `classid = 113`, `base_class = 113`, `race = 3` (Orc), `nobless = 1`.
- Insert active subclasses into `character_subclasses`:
  - `class_id = 89` (DreadNought - Warrior)
  - `class_id = 111` (Spectral Master - Summoner)
  - `class_id = 108` (Ghost Hunter - Assassin)

### 2. Skill & Enchantment Injection (`character_skills`)
- Insert all Titan skill IDs with maximum level and `enchant_level = 130` / `230` (+30 for 2nd/3rd class skills) or `115` (+15 for 3rd class skills).
- Inject cumulative skills from subclasses (DreadNought, Spectral Master, Ghost Hunter) into `character_skills` for `charId = 300000001`.

### 3. Hero Status & Hero Skills (`heroes` & `character_skills`)
- Insert into `heroes`: `(charId, class_id, count, played, claimed, message)` = `(300000001, 113, 1, 1, 'true', 'Titan Hero Tester')`.
- Inject Hero skills: Skill 395 (Heroic Valor), 396 (Heroic Grandeur), 1374 (Heroic Miracle), 1375 (Heroic Berserker), 1376 (Heroic Dread).

### 4. Clan & Clan Skills (`clan_skills` & `characters`)
- Associate `TitanTester` to `clanid = 100000` with `clan_privs = 16777215` (Full Leader Privileges).
- Insert all Clan Skills (`clan_skills`) for `clan_id = 100000` at max levels.

### 5. Equipment & Items Insertion (`items`)
- Custom Armor: Royal Heavy set (`99201`, `99204`, `99205`, `99206`, `99207`, `99208`, `99224`) + Light & Robe sets.
- Custom Weapons: All 16 Royal Dynasty Weapons (`99300` - `99315`) inserted with `enchant_level = 0` (or +16 as required for testing).
- Tattoos: All 14 Level 6 Tattoos (`41006`-`41042` and `41048`-`41084`).
- Epic Jewels: Baium Ring (`6658`), Zaken Earring (`6659`), Antharas Earring (`6656`), Valakas Necklace (`6657`), Queen Ant Ring (`6660`), Beleth Ring (`10314`).
- Supplies & Access Items: 1B Adena (`57`), 500 CP/HP/MP potions, BSOE/BRES, 100k SS/BSS, Giant's Codes, Life Stones, Attribute Stones, and Boss Access Stones/Fabrics.

## Risks & Trade-offs

- **[Risk] Primary Key Object ID collision in `items` table** → **Mitigation**: Calculate `MAX(object_id) + offset` when executing SQL script.
- **[Risk] Character cache inconsistency if game server is live** → **Mitigation**: Run SQL script while character is offline or restart/reload server.
