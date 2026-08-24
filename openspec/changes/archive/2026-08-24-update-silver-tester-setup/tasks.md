## 1. Database Setup Script Preparation

- [x] 1.1 Create SQL script `setup_silver_tester.sql` in scratch directory for character `SilverTester` (charId `300000000`).
- [x] 1.2 Add Adena update query (`item_id = 57`, `count = 1000000000`).
- [x] 1.3 Add items insertion query for combat consumables (500x CP, 500x HP, 500x MP, 100x SOE, 100x BRES, 100k SS/BSS).
- [x] 1.4 Add items insertion query for all 14 Level 6 Tattoos (Right: 41006, 41012, 41018, 41024, 41030, 41036, 41042; Left: 41048, 41054, 41060, 41066, 41072, 41078, 41084).
- [x] 1.5 Add items insertion query for Boss access items (4295, 3865, 7267, 8073) and Giant's Codes / Life Stones.

## 2. Cumulative Subclass Skills Injections

- [x] 2.1 Add SQL statements to insert `Sword Muse` (Class ID 107) song skills into `character_skills` for `charId = 300000000` (Song of Hunter, Song of Warding, Song of Wind, Song of Renewal, Song of Champion, Song of Earth, Song of Vitality).
- [x] 2.2 Add SQL statements to populate cumulative skill sets across subclasses (Duelist ID 88, DreadNought ID 89, Archmage ID 94).

## 3. Database Execution & Verification

- [x] 3.1 Execute `setup_silver_tester.sql` against `l2journey_db_1` via `podman exec`.
- [x] 3.2 Query database to verify Adena balance, item counts, tattoo items, and cumulative subclass skills for `SilverTester`.
