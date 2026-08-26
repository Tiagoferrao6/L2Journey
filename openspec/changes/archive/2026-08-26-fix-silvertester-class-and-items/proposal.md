# Proposal: Fix SilverTester Class and Equipment

## Intent
Correct `SilverTester` (`charId = 300000000`) so that her primary class is **Moonlight Sentinel (Class 106)** and her primary subclass is **Sword Muse (Class 99)**, equipped with the **Royal Light Armor Set (+6)** and **Royal Dynasty Bow (+8)**, resolving the class and item mismatch observed in-game.

## Scope
- Update character `300000000` (`SilverTester`) in MariaDB (`characters`, `character_subclasses`, `items`).
- Update [`dist/db_installer/sql/game/custom_test_characters_setup.sql`](file:///home/tiago/L2Journey/L2Journey/dist/db_installer/sql/game/custom_test_characters_setup.sql) so that future database initializations create `SilverTester` correctly.

## Requirements
1. `SilverTester` base class set to `106` (Moonlight Sentinel / Silver Ranger 3rd Class).
2. `SilverTester` subclass 1 set to `99` (Sword Muse / Swordsinger 3rd Class).
3. Paperdoll equipped with:
   - `99303` (Royal Dynasty Bow +8) on RHand (`loc_data = 5`).
   - `99210` (Royal Light Armor - Bow Master +6) on Chest (`loc_data = 6`).
   - `99211` (Royal Light Leggings +6) on Legs (`loc_data = 10`).
   - `99212` (Royal Leather Helmet +6) on Head (`loc_data = 1`).
   - `99213` (Royal Leather Gloves +6) on Gloves (`loc_data = 11`).
   - `99214` (Royal Leather Boots +6) on Feet (`loc_data = 12`).
   - `99224` (Royal Cloak +6) on Back (`loc_data = 13`).
   - Epic Jewels (Valakas, Antharas, Zaken, Baium, Queen Ant).
4. Inventory populated with `99311` (Royal Dynasty Dual Sword +8) for Sword Muse songs.
